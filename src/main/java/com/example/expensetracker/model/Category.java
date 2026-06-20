package com.example.expensetracker.model;

public enum Category {
    FOOD("Food"),
    TRAVEL("Travel"),
    SHOPPING("Shopping"),
    EDUCATION("Education"),
    ENTERTAINMENT("Entertainment"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
