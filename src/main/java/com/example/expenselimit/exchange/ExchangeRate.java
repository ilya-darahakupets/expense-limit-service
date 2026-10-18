package com.example.expenselimit.exchange;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String provider;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "quote_currency", nullable = false, length = 3)
    private String quoteCurrency;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "close_rate", nullable = false, precision = 20, scale = 10)
    private BigDecimal closeRate;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;

    protected ExchangeRate() {
    }

    public ExchangeRate(
            String provider,
            String currency,
            String quoteCurrency,
            LocalDate rateDate,
            BigDecimal closeRate,
            Instant fetchedAt) {
        this.provider = provider;
        this.currency = currency;
        this.quoteCurrency = quoteCurrency;
        this.rateDate = rateDate;
        this.closeRate = closeRate;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public String getCurrency() {
        return currency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    public BigDecimal getCloseRate() {
        return closeRate;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }
}
