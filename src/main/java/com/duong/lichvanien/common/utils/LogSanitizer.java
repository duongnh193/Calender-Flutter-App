package com.duong.lichvanien.common.utils;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing sensitive data in logs to prevent PII leakage.
 */
public class LogSanitizer {

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    /**
     * Masks a date string to hide the day component for privacy.
     * Example: "1990-02-15" -> "1990-02-XX"
     *
     * @param date Date string in format yyyy-MM-dd
     * @return Masked date string with day replaced by XX
     */
    public static String maskDate(String date) {
        if (date == null || date.isEmpty()) {
            return date;
        }
        return DATE_PATTERN.matcher(date).replaceFirst("$1-$2-XX");
    }

    /**
     * Masks a date string if it's not null, otherwise returns null.
     *
     * @param date Date string to mask
     * @return Masked date or null if input is null
     */
    public static String maskDateOrNull(String date) {
        return date != null ? maskDate(date) : null;
    }
}
