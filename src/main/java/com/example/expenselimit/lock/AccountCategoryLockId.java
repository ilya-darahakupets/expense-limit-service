package com.example.expenselimit.lock;

import com.example.expenselimit.common.ExpenseCategory;
import java.io.Serializable;
import java.util.Objects;

public class AccountCategoryLockId implements Serializable {

    private String accountFrom;
    private ExpenseCategory category;

    public AccountCategoryLockId() {
    }

    public AccountCategoryLockId(String accountFrom, ExpenseCategory category) {
        this.accountFrom = accountFrom;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountCategoryLockId that)) {
            return false;
        }
        return Objects.equals(accountFrom, that.accountFrom) && category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountFrom, category);
    }
}
