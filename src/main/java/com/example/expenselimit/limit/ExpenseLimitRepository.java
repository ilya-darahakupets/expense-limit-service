package com.example.expenselimit.limit;

import com.example.expenselimit.common.ExpenseCategory;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseLimitRepository extends JpaRepository<ExpenseLimit, Long> {

    List<ExpenseLimit> findByAccountFromOrderByEffectiveAtAscVersionNoAsc(String accountFrom);

    @Query("""
            SELECT COALESCE(MAX(l.versionNo), 0)
            FROM ExpenseLimit l
            WHERE l.accountFrom = :account
              AND l.category = :category
              AND l.effectiveAt = :effectiveAt
            """)
    int maxVersionAt(
            @Param("account") String account,
            @Param("category") ExpenseCategory category,
            @Param("effectiveAt") Instant effectiveAt);

    @Query(
            value = """
                    SELECT l.*
                    FROM expense_limit l
                    JOIN (
                        SELECT account_from, category, MAX(effective_at) AS effective_at
                        FROM expense_limit
                        WHERE account_from = :account
                          AND category = :category
                          AND effective_at <= :at
                        GROUP BY account_from, category
                    ) latest
                      ON latest.account_from = l.account_from
                     AND latest.category = l.category
                     AND latest.effective_at = l.effective_at
                    WHERE l.version_no = (
                        SELECT MAX(same_time.version_no)
                        FROM expense_limit same_time
                        WHERE same_time.account_from = l.account_from
                          AND same_time.category = l.category
                          AND same_time.effective_at = l.effective_at
                    )
                    """,
            nativeQuery = true)
    Optional<ExpenseLimit> findApplicable(
            @Param("account") String account,
            @Param("category") String category,
            @Param("at") Instant at);
}
