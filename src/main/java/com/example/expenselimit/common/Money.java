package com.example.expenselimit.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    public static final int USD_SCALE = 2;
    public static final int RATE_SCALE = 10;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private Money() {
    }

        public static BigDecimal usd(BigDecimal value) {
        return value.setScale(USD_SCALE, ROUNDING);
    }

    public static BigDecimal toUsd(BigDecimal amount, BigDecimal usdPerUnit) {
        return usd(amount.multiply(usdPerUnit));
    }
}
