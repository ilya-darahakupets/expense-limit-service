package com.example.expenselimit.limit.dto;

import com.example.expenselimit.common.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateLimitRequest(
        @NotNull ExpenseCategory expenseCategory,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal limitSum) {
}
