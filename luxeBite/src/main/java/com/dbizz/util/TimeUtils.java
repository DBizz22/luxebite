package com.dbizz.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeUtils {

    private TimeUtils() {

    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
