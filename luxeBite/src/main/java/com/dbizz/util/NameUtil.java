package com.dbizz.util;

public class NameUtil {
    private NameUtil() {

    }

    public static void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty : " + name);
        }
    }

    public static void validateUsername(String username) {
        if (username == null || username.length() < 5) {
            throw new IllegalArgumentException("Username cannot be empty or less than 5 characters : " + username);
        }
    }
}
