package com.dbizz.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    public static final String CSRF_TOKEN = "CSRF_TOKEN";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken() {

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);

    }
}
