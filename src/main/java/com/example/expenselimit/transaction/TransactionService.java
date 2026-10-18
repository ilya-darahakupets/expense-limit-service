package com.example.expenselimit.transaction;

import com.example.expenselimit.common.Money;
import com.example.expenselimit.common.ProcessingStatus;
import com.example.expenselimit.config.AppProperties;
import com.example.expenselimit.error.BusinessException;
import com.example.expenselimit.exchange.ExchangeRateService;
import com.example.expenselimit.exchange.FxConversion;
import com.example.expenselimit.limit.ExpenseLimit;
import com.example.expenselimit.limit.ExpenseLimitRepository;
import com.example.expenselimit.limit.LimitCalculator;
import com.example.expenselimit.lock.AccountLockService;
import com.example.expenselimit.transaction.dto.ExceededTransactionResponse;
import com.example.expenselimit.transaction.dto.TransactionAcceptedResponse;
import com.example.expenselimit.transaction.dto.TransactionRequest;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final ExpenseTransactionRepository transactionRepository;
    private final ExpenseLimitRepository limitRepository;
    private final AccountLockService accountLockService;
    private final ExchangeRateService exchangeRateService;
    private final Clock clock;
    private final AppProperties properties;

    public TransactionService(
            ExpenseTransactionRepository transactionRepository,
            ExpenseLimitRepository limitRepository,
            AccountLockService accountLockService,
            ExchangeRateService exchangeRateService,
            Clock clock,
            AppProperties properties) {
        this.transactionRepository = transactionRepository;
        this.limitRepository = limitRepository;
        this.accountLockService = accountLockService;
        this.exchangeRateService = exchangeRateService;
        this.clock = clock;
        this.properties = properties;
    }

    @Transactional
    public ExpenseTransaction saveIncoming(TransactionRequest request) {
        String currency = request.currencyShortname().toUpperCase();
        if (!exchangeRateService.isSupported(currency)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Unsupported currency: " + currency);
        }
        ZoneId zone = ZoneId.of(properties.zoneId());
        Instant occurredAt = request.datetime().toInstant();
        LocalDate monthStart = LocalDate.ofInstant(occurredAt, zone).withDayOfMonth(1);
        ExpenseTransaction tx = ExpenseTransaction.received(
                request.accountFrom(),
                request.accountTo(),
                currency,
                Money.usd(request.sum()),
                request.expenseCategory(),
                occurredAt,
                request.datetime().getOffset().getId(),
                Instant.now(clock),
                monthStart);
        return transactionRepository.saveAndFlush(tx);
    }

    @Transactional
    public TransactionAcceptedResponse process(Long id) {
        ExpenseTransaction tx = transactionRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Transaction not found: " + id));
        if (tx.getStatus() == ProcessingStatus.PROCESSED) {
            return toAccepted(tx);
        }

        ZoneId zone = ZoneId.of(properties.zoneId());
        LocalDate rateDate = LocalDate.ofInstant(tx.getOccurredAt(), zone);
        Optional<FxConversion> conversion =
                exchangeRateService.convert(tx.getOriginalSum(), tx.getCurrencyShortname(), rateDate);
        if (conversion.isEmpty()) {
            tx.markPendingRate();
            return toAccepted(tx);
        }

        accountLockService.lock(tx.getAccountFrom(), tx.getCategory());
        tx = transactionRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Transaction not found: " + id));
        if (tx.getStatus() == ProcessingStatus.PROCESSED) {
            return toAccepted(tx);
        }

        BigDecimal spentBefore = transactionRepository.sumProcessedUsd(
                tx.getAccountFrom(),
                tx.getCategory(),
                tx.getMonthStart(),
                ProcessingStatus.PROCESSED,
                tx.getId());
        BigDecimal spentAfter = spentBefore.add(conversion.get().sumUsd());

        Optional<ExpenseLimit> applicable = limitRepository.findApplicable(
                tx.getAccountFrom(), tx.getCategory().name(), tx.getOccurredAt());
        BigDecimal limitSum = applicable.map(ExpenseLimit::getLimitSum).orElse(Money.usd(properties.defaultLimitUsd()));
        Instant limitDatetime = applicable
                .map(ExpenseLimit::getEffectiveAt)
                .orElse(tx.getMonthStart().atStartOfDay(zone).toInstant());
        String limitCurrency = applicable.map(ExpenseLimit::getCurrency).orElse("USD");
        Long limitId = applicable.map(ExpenseLimit::getId).orElse(null);
        boolean exceeded = LimitCalculator.isExceeded(spentAfter, limitSum);

        tx.markProcessed(
                conversion.get().sumUsd(),
                exceeded,
                limitId,
                limitSum,
                limitDatetime,
                limitCurrency,
                conversion.get().exchangeRateId());
        return toAccepted(tx);
    }

    @Transactional(readOnly = true)
    public List<ExceededTransactionResponse> findExceeded(String accountFrom) {
        return transactionRepository
                .findByAccountFromAndLimitExceededIsTrueOrderByOccurredAtAscIdAsc(accountFrom)
                .stream()
                .map(this::toExceeded)
                .toList();
    }

    private TransactionAcceptedResponse toAccepted(ExpenseTransaction tx) {
        return new TransactionAcceptedResponse(tx.getId(), tx.getStatus(), tx.getLimitExceeded(), tx.getSumUsd());
    }

    private ExceededTransactionResponse toExceeded(ExpenseTransaction tx) {
        return new ExceededTransactionResponse(
                tx.getAccountFrom(),
                tx.getAccountTo(),
                tx.getCurrencyShortname(),
                tx.getOriginalSum(),
                tx.getCategory(),
                OffsetDateTime.ofInstant(tx.getOccurredAt(), ZoneOffset.of(tx.getOriginalOffset())),
                tx.getLimitSumUsd(),
                OffsetDateTime.ofInstant(tx.getLimitDatetime(), ZoneId.of(properties.zoneId())),
                tx.getLimitCurrency());
    }
}
