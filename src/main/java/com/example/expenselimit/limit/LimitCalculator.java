package com.example.expenselimit.limit;

import java.math.BigDecimal;

public final class LimitCalculator {

    private LimitCalculator() {
    }

    public static boolean isExceeded(BigDecimal spentAfterIncludingCurrent, BigDecimal applicableLimit) {
        return spentAfterIncludingCurrent.compareTo(applicableLimit) > 0;
    }
}
