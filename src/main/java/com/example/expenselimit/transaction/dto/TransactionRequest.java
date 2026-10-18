package com.example.expenselimit.transaction.dto;

import com.example.expenselimit.common.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionRequest(
        @NotBlank @Pattern(regexp = "\\d{10}") String accountFrom,
        @NotBlank @Pattern(regexp = "\\d{10}") String accountTo,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currencyShortname,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal sum,
        @NotNull ExpenseCategory expenseCategory,
        @NotNull OffsetDateTime datetime) {
}
