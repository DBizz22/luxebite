package com.dbizz.util;

public class IDCheck {
    private IDCheck() {

    }

    public static void validate(int id) {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid ID : " + id);
    }
}
