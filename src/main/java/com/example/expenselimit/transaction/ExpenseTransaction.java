package com.example.expenselimit.transaction;

import com.example.expenselimit.common.ExpenseCategory;
import com.example.expenselimit.common.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "expense_transaction")
public class ExpenseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_from", nullable = false, length = 10)
    private String accountFrom;

    @Column(name = "account_to", nullable = false, length = 10)
    private String accountTo;

    @Column(name = "currency_shortname", nullable = false, length = 3)
    private String currencyShortname;

    @Column(name = "original_sum", nullable = false, precision = 19, scale = 2)
    private BigDecimal originalSum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ExpenseCategory category;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "original_offset", nullable = false, length = 18)
    private String originalOffset;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "month_start", nullable = false)
    private LocalDate monthStart;

    @Column(name = "sum_usd", precision = 19, scale = 2)
    private BigDecimal sumUsd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ProcessingStatus status;

    @Column(name = "limit_exceeded")
    private Boolean limitExceeded;

    @Column(name = "applied_limit_id")
    private Long appliedLimitId;

    @Column(name = "limit_sum_usd", precision = 19, scale = 2)
    private BigDecimal limitSumUsd;

    @Column(name = "limit_datetime")
    private Instant limitDatetime;

    @Column(name = "limit_currency", length = 3)
    private String limitCurrency;

    @Column(name = "exchange_rate_id")
    private Long exchangeRateId;

    protected ExpenseTransaction() {
    }

    public static ExpenseTransaction received(
            String accountFrom,
            String accountTo,
            String currencyShortname,
            BigDecimal originalSum,
            ExpenseCategory category,
            Instant occurredAt,
            String originalOffset,
            Instant receivedAt,
            LocalDate monthStart) {
        ExpenseTransaction tx = new ExpenseTransaction();
        tx.accountFrom = accountFrom;
        tx.accountTo = accountTo;
        tx.currencyShortname = currencyShortname;
        tx.originalSum = originalSum;
        tx.category = category;
        tx.occurredAt = occurredAt;
        tx.originalOffset = originalOffset;
        tx.receivedAt = receivedAt;
        tx.monthStart = monthStart;
        tx.status = ProcessingStatus.RECEIVED;
        return tx;
    }

    public void markPendingRate() {
        this.status = ProcessingStatus.PENDING_RATE;
        this.limitExceeded = null;
    }

    public void markProcessed(
            BigDecimal sumUsd,
            boolean limitExceeded,
            Long appliedLimitId,
            BigDecimal limitSumUsd,
            Instant limitDatetime,
            String limitCurrency,
            Long exchangeRateId) {
        this.status = ProcessingStatus.PROCESSED;
        this.sumUsd = sumUsd;
        this.limitExceeded = limitExceeded;
        this.appliedLimitId = appliedLimitId;
        this.limitSumUsd = limitSumUsd;
        this.limitDatetime = limitDatetime;
        this.limitCurrency = limitCurrency;
        this.exchangeRateId = exchangeRateId;
    }

    public Long getId() {
        return id;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public String getCurrencyShortname() {
        return currencyShortname;
    }

    public BigDecimal getOriginalSum() {
        return originalSum;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOriginalOffset() {
        return originalOffset;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public LocalDate getMonthStart() {
        return monthStart;
    }

    public BigDecimal getSumUsd() {
        return sumUsd;
    }

    public ProcessingStatus getStatus() {
        return status;
    }

    public Boolean getLimitExceeded() {
        return limitExceeded;
    }

    public Long getAppliedLimitId() {
        return appliedLimitId;
    }

    public BigDecimal getLimitSumUsd() {
        return limitSumUsd;
    }

    public Instant getLimitDatetime() {
        return limitDatetime;
    }

    public String getLimitCurrency() {
        return limitCurrency;
    }

    public Long getExchangeRateId() {
        return exchangeRateId;
    }
}
