package com.example.expenselimit.exchange;

import java.math.BigDecimal;

public record FxConversion(BigDecimal sumUsd, Long exchangeRateId) {
}
