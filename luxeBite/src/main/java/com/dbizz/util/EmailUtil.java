package com.dbizz.util;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailUtil {
    private static final EmailValidator validator = EmailValidator.getInstance();

    private EmailUtil() {
    }

    public static boolean isValid(String email) {
        return email != null && !email.isBlank() && validator.isValid(email);
    }

    public static void validate(String email) {
        if (email == null || email.isBlank() || !validator.isValid(email))
            throw new IllegalArgumentException("Email is invalid : " + email);
    }
}
