package com.example.expenselimit.limit;

import com.example.expenselimit.common.ExpenseCategory;
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

@Entity
@Table(name = "expense_limit")
public class ExpenseLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_from", nullable = false, length = 10)
    private String accountFrom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ExpenseCategory category;

    @Column(name = "limit_sum", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitSum;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "effective_at", nullable = false)
    private Instant effectiveAt;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    protected ExpenseLimit() {
    }

    public ExpenseLimit(
            String accountFrom,
            ExpenseCategory category,
            BigDecimal limitSum,
            String currency,
            Instant effectiveAt,
            Integer versionNo) {
        this.accountFrom = accountFrom;
        this.category = category;
        this.limitSum = limitSum;
        this.currency = currency;
        this.effectiveAt = effectiveAt;
        this.versionNo = versionNo;
    }

    public Long getId() {
        return id;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public BigDecimal getLimitSum() {
        return limitSum;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getEffectiveAt() {
        return effectiveAt;
    }

    public Integer getVersionNo() {
        return versionNo;
    }
}
