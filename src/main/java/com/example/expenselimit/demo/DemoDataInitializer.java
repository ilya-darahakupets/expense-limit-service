package com.example.expenselimit.demo;

import com.example.expenselimit.common.ExpenseCategory;
import com.example.expenselimit.exchange.ExchangeRate;
import com.example.expenselimit.exchange.ExchangeRateRepository;
import com.example.expenselimit.limit.ExpenseLimit;
import com.example.expenselimit.limit.ExpenseLimitRepository;
import com.example.expenselimit.transaction.ExpenseTransactionRepository;
import com.example.expenselimit.transaction.TransactionService;
import com.example.expenselimit.transaction.dto.TransactionRequest;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Seeds demo accounts, limits, exchange rates and transactions for the current UTC month.
 * Runs once: skipped when any demo account already has transactions.
 */
@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {

    public static final String ACCOUNT_OVER_LIMIT = "1000000001";
    public static final String ACCOUNT_DEFAULT_LIMIT = "1000000002";
    public static final String ACCOUNT_LIMIT_CHANGED = "1000000003";
    public static final String COUNTERPARTY = "9000000001";

    static final String RATE_PROVIDER = "demo";
    static final BigDecimal KZT_TO_USD = new BigDecimal("0.0020000000");
    static final BigDecimal RUB_TO_USD = new BigDecimal("0.0125000000");

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);
    private static final ZoneOffset CLIENT_OFFSET = ZoneOffset.ofHours(6);

    private final ExpenseLimitRepository limitRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExpenseTransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final Clock clock;

    public DemoDataInitializer(
            ExpenseLimitRepository limitRepository,
            ExchangeRateRepository exchangeRateRepository,
            ExpenseTransactionRepository transactionRepository,
            TransactionService transactionService,
            Clock clock) {
        this.limitRepository = limitRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.clock = clock;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> accounts = List.of(ACCOUNT_OVER_LIMIT, ACCOUNT_DEFAULT_LIMIT, ACCOUNT_LIMIT_CHANGED);
        if (accounts.stream().anyMatch(transactionRepository::existsByAccountFrom)) {
            log.info("Demo data already present, skipping");
            return;
        }

        LocalDate monthStart = LocalDate.now(clock.withZone(ZoneOffset.UTC)).withDayOfMonth(1);
        Instant base = monthStart.atStartOfDay(ZoneOffset.UTC).toInstant();

        seedRates(monthStart.minusYears(1));

        // 1000000001: product limit 2000 USD, service uses default 1000 USD; both get exceeded
        limit(ACCOUNT_OVER_LIMIT, ExpenseCategory.PRODUCT, "2000.00", base);
        tx(ACCOUNT_OVER_LIMIT, "USD", "500.00", ExpenseCategory.PRODUCT, base.plus(Duration.ofHours(10)));
        tx(ACCOUNT_OVER_LIMIT, "KZT", "500000.00", ExpenseCategory.PRODUCT, base.plus(Duration.ofHours(11)));
        tx(ACCOUNT_OVER_LIMIT, "RUB", "50000.00", ExpenseCategory.PRODUCT, base.plus(Duration.ofHours(12)));
        tx(ACCOUNT_OVER_LIMIT, "USD", "1200.00", ExpenseCategory.SERVICE, base.plus(Duration.ofHours(13)));

        // 1000000002: no custom limits (default 1000 USD), nothing exceeded
        tx(ACCOUNT_DEFAULT_LIMIT, "USD", "300.00", ExpenseCategory.PRODUCT, base.plus(Duration.ofHours(10)));
        tx(ACCOUNT_DEFAULT_LIMIT, "KZT", "200000.00", ExpenseCategory.SERVICE, base.plus(Duration.ofHours(11)));

        // 1000000003: service limit 100 USD exceeded, then raised to 500 USD
        limit(ACCOUNT_LIMIT_CHANGED, ExpenseCategory.SERVICE, "100.00", base);
        tx(ACCOUNT_LIMIT_CHANGED, "USD", "150.00", ExpenseCategory.SERVICE, base.plus(Duration.ofHours(10)));
        limit(ACCOUNT_LIMIT_CHANGED, ExpenseCategory.SERVICE, "500.00", base.plus(Duration.ofHours(12)));
        tx(ACCOUNT_LIMIT_CHANGED, "USD", "200.00", ExpenseCategory.SERVICE, base.plus(Duration.ofHours(14)));

        log.info("Demo data created for accounts {}", accounts);
    }

    private void seedRates(LocalDate rateDate) {
        Instant now = clock.instant();
        for (var rate : List.of(
                new ExchangeRate(RATE_PROVIDER, "KZT", "USD", rateDate, KZT_TO_USD, now),
                new ExchangeRate(RATE_PROVIDER, "RUB", "USD", rateDate, RUB_TO_USD, now))) {
            boolean exists = exchangeRateRepository
                    .findFirstByCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                            rate.getCurrency(), rate.getQuoteCurrency(), rateDate)
                    .isPresent();
            if (!exists) {
                exchangeRateRepository.save(rate);
            }
        }
    }

    private void limit(String account, ExpenseCategory category, String sum, Instant effectiveAt) {
        int version = limitRepository.maxVersionAt(account, category, effectiveAt) + 1;
        limitRepository.save(new ExpenseLimit(account, category, new BigDecimal(sum), "USD", effectiveAt, version));
    }

    private void tx(String account, String currency, String sum, ExpenseCategory category, Instant occurredAt) {
        var request = new TransactionRequest(
                account,
                COUNTERPARTY,
                currency,
                new BigDecimal(sum),
                category,
                OffsetDateTime.ofInstant(occurredAt, CLIENT_OFFSET));
        transactionService.process(transactionService.saveIncoming(request).getId());
    }
}
