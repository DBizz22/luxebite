package com.dbizz.model;

public enum ProductCategory {

    MAIN("MAIN"),
    APPETIZER("APPETIZER"),
    SIDE("SIDE"),
    DRINK("DRINK");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductCategory fromString(String category) {
        for (ProductCategory pc : ProductCategory.values()) {
            if (pc.name().equalsIgnoreCase(category) || pc.getDisplayName().equalsIgnoreCase(category)) {
                return pc;
            }
        }
        return null;
    }

}
