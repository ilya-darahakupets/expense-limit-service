package com.example.expenselimit.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseCategory {
    PRODUCT,
    SERVICE;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ExpenseCategory fromJson(String value) {
        if (value == null) {
            return null;
        }
        return ExpenseCategory.valueOf(value.trim().toUpperCase());
    }
}
