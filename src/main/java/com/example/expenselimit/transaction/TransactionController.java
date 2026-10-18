package com.example.expenselimit.transaction;

import com.example.expenselimit.transaction.dto.ExceededTransactionResponse;
import com.example.expenselimit.transaction.dto.TransactionAcceptedResponse;
import com.example.expenselimit.transaction.dto.TransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Transactions", description = "Expense transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Create expense transaction", description = "Accepts a new expense transaction and processes it against monthly limits")
    @PostMapping("/api/integration/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionAcceptedResponse ingest(@Valid @RequestBody TransactionRequest request) {
        var saved = transactionService.saveIncoming(request);
        return transactionService.process(saved.getId());
    }

    @Operation(summary = "Get exceeded transactions", description = "Returns all transactions that exceeded the monthly limit for an account")
    @GetMapping("/api/accounts/{accountFrom}/transactions/exceeded")
    public List<ExceededTransactionResponse> exceeded(
            @Parameter(description = "Account number (10 digits)") @PathVariable @Pattern(regexp = "\\d{10}") String accountFrom) {
        return transactionService.findExceeded(accountFrom);
    }
}
