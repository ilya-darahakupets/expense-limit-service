package com.example.expenselimit.exchange;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findFirstByCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
            String currency, String quoteCurrency, LocalDate rateDate);
}
