package com.example.expenselimit.limit.dto;

import com.example.expenselimit.common.ExpenseCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LimitResponse(
        Long id,
        String accountFrom,
        ExpenseCategory expenseCategory,
        BigDecimal limitSum,
        String limitCurrency,
        OffsetDateTime limitDatetime) {
}
