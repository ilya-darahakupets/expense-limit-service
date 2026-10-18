package com.example.expenselimit.limit;

import com.example.expenselimit.common.ExpenseCategory;
import com.example.expenselimit.common.Money;
import com.example.expenselimit.config.AppProperties;
import com.example.expenselimit.limit.dto.CreateLimitRequest;
import com.example.expenselimit.limit.dto.LimitResponse;
import com.example.expenselimit.lock.AccountLockService;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimitService {

    private final ExpenseLimitRepository limitRepository;
    private final AccountLockService accountLockService;
    private final Clock clock;
    private final AppProperties properties;

    public LimitService(
            ExpenseLimitRepository limitRepository,
            AccountLockService accountLockService,
            Clock clock,
            AppProperties properties) {
        this.limitRepository = limitRepository;
        this.accountLockService = accountLockService;
        this.clock = clock;
        this.properties = properties;
    }

    @Transactional
    public LimitResponse create(String accountFrom, CreateLimitRequest request) {
        accountLockService.lock(accountFrom, request.expenseCategory());
        Instant effectiveAt = clock.instant();
        int version = limitRepository.maxVersionAt(accountFrom, request.expenseCategory(), effectiveAt) + 1;
        ExpenseLimit saved = limitRepository.save(new ExpenseLimit(
                accountFrom,
                request.expenseCategory(),
                Money.usd(request.limitSum()),
                "USD",
                effectiveAt,
                version));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LimitResponse> list(String accountFrom) {
        return limitRepository.findByAccountFromOrderByEffectiveAtAscVersionNoAsc(accountFrom).stream()
                .map(this::toResponse)
                .toList();
    }

    private LimitResponse toResponse(ExpenseLimit limit) {
        ZoneId zone = ZoneId.of(properties.zoneId());
        return new LimitResponse(
                limit.getId(),
                limit.getAccountFrom(),
                limit.getCategory(),
                limit.getLimitSum(),
                limit.getCurrency(),
                OffsetDateTime.ofInstant(limit.getEffectiveAt(), zone));
    }
}
