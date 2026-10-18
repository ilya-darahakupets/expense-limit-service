package com.example.expenselimit.transaction.dto;

import com.example.expenselimit.common.ExpenseCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ExceededTransactionResponse(
        String accountFrom,
        String accountTo,
        String currencyShortname,
        BigDecimal sum,
        ExpenseCategory expenseCategory,
        OffsetDateTime datetime,
        BigDecimal limitSum,
        OffsetDateTime limitDatetime,
        String limitCurrency) {
}
