package com.example.expenselimit.limit;

import com.example.expenselimit.limit.dto.CreateLimitRequest;
import com.example.expenselimit.limit.dto.LimitResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/accounts/{accountFrom}/limits")
@Tag(name = "Limits", description = "Monthly expense limit management")
public class LimitController {

    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @Operation(summary = "Create expense limit", description = "Creates a new monthly expense limit for an account and category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LimitResponse create(
            @Parameter(description = "Account number (10 digits)") @PathVariable @Pattern(regexp = "\\d{10}") String accountFrom,
            @Valid @RequestBody CreateLimitRequest request) {
        return limitService.create(accountFrom, request);
    }

    @Operation(summary = "Get account limits", description = "Returns all expense limits for an account")
    @GetMapping
    public List<LimitResponse> list(@Parameter(description = "Account number (10 digits)") @PathVariable @Pattern(regexp = "\\d{10}") String accountFrom) {
        return limitService.list(accountFrom);
    }
}
