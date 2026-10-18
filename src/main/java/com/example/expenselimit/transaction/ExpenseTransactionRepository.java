package com.example.expenselimit.transaction;

import com.example.expenselimit.common.ExpenseCategory;
import com.example.expenselimit.common.ProcessingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    @Query("""
            SELECT COALESCE(SUM(t.sumUsd), 0)
            FROM ExpenseTransaction t
            WHERE t.accountFrom = :account
              AND t.category = :category
              AND t.monthStart = :monthStart
              AND t.status = :status
              AND t.id <> :excludeId
            """)
    BigDecimal sumProcessedUsd(
            @Param("account") String account,
            @Param("category") ExpenseCategory category,
            @Param("monthStart") LocalDate monthStart,
            @Param("status") ProcessingStatus status,
            @Param("excludeId") Long excludeId);

    List<ExpenseTransaction> findByAccountFromAndLimitExceededIsTrueOrderByOccurredAtAscIdAsc(String accountFrom);
}
