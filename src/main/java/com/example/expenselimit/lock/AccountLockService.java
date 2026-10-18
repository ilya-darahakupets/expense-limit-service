package com.example.expenselimit.lock;

import com.example.expenselimit.common.ExpenseCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLockService {

    private final EntityManager entityManager;

    public AccountLockService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void lock(String accountFrom, ExpenseCategory category) {
        entityManager.createNativeQuery("""
                        INSERT INTO account_category_lock (account_from, category)
                        VALUES (:account, :category)
                        ON CONFLICT (account_from, category) DO NOTHING
                        """)
                .setParameter("account", accountFrom)
                .setParameter("category", category.name())
                .executeUpdate();

        entityManager.createQuery(
                        "SELECT l FROM AccountCategoryLock l WHERE l.accountFrom = :account AND l.category = :category",
                        AccountCategoryLock.class)
                .setParameter("account", accountFrom)
                .setParameter("category", category)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getSingleResult();
    }
}
