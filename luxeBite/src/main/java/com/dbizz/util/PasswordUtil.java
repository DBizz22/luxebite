package com.dbizz.util;

import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.regex.*;

public class PasswordUtil {

    private static final int PASSWORD_LENGTH = 8;
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private PasswordUtil() {

    }

    public static void validate(String password) {
        if (password == null || password.length() < PASSWORD_LENGTH || !pattern.matcher(password).matches()) {
            throw new IllegalArgumentException("Invalid Password");
        }
    }

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public static void verify(String passwordEntry, String passwordHash) {
        if (!encoder.matches(passwordEntry, passwordHash))
            throw new IllegalArgumentException("Invalid Credentials");
    }

    public static boolean equals(String passwordEntry, String passwordHash) {
        return encoder.matches(passwordEntry, passwordHash);
    }
}
