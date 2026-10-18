package com.example.expenselimit.limit;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LimitCalculatorTest {

    @Test
    void equalToLimitIsNotExceeded() {
        assertThat(LimitCalculator.isExceeded(new BigDecimal("1000.00"), new BigDecimal("1000.00")))
                .isFalse();
    }

    @Test
    void oneCentOverLimitIsExceeded() {
        assertThat(LimitCalculator.isExceeded(new BigDecimal("1000.01"), new BigDecimal("1000.00")))
                .isTrue();
    }
}
