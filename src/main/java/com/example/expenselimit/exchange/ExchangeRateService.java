package com.example.expenselimit.exchange;

import com.example.expenselimit.common.Money;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private static final Set<String> SUPPORTED = Set.of("USD", "KZT", "RUB");

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public boolean isSupported(String currency) {
        return SUPPORTED.contains(currency);
    }

    public Optional<FxConversion> convert(BigDecimal amount, String currency, LocalDate rateDate) {
        if ("USD".equals(currency)) {
            return Optional.of(new FxConversion(Money.usd(amount), null));
        }
        return exchangeRateRepository
                .findFirstByCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                        currency, "USD", rateDate)
                .map(rate -> new FxConversion(Money.toUsd(amount, rate.getCloseRate()), rate.getId()));
    }
}
