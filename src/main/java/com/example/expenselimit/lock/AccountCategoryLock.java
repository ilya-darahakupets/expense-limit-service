package com.example.expenselimit.lock;

import com.example.expenselimit.common.ExpenseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_category_lock")
@IdClass(AccountCategoryLockId.class)
public class AccountCategoryLock {

    @Id
    @Column(name = "account_from", nullable = false, length = 10)
    private String accountFrom;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 16)
    private ExpenseCategory category;

    protected AccountCategoryLock() {
    }

    public AccountCategoryLock(String accountFrom, ExpenseCategory category) {
        this.accountFrom = accountFrom;
        this.category = category;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public ExpenseCategory getCategory() {
        return category;
    }
}
