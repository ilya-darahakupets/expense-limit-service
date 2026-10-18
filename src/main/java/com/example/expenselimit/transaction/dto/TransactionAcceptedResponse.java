package com.example.expenselimit.transaction.dto;

import com.example.expenselimit.common.ProcessingStatus;
import java.math.BigDecimal;

public record TransactionAcceptedResponse(
        Long id, ProcessingStatus status, Boolean limitExceeded, BigDecimal sumUsd) {
}
