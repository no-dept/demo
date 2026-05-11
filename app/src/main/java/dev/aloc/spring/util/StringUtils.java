package dev.aloc.spring.util;

/**
 * Lightweight string helpers used across the framework.
 *
 * <p>Kept dependency-free so it can be referenced from annotation/scanner
 * paths without triggering bean wiring.
 */
public final class StringUtils {

    private StringUtils() {
        // utility class
    }

    /**
     * Returns true when the input is null or contains only whitespace.
     */
    public static boolean isBlank(String value) {
        if (value == null) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lowercases the first character if the string is non-empty.
     * Useful for converting class names like "UserService" into bean names
     * such as "userService" without pulling in a full naming util.
     */
    public static String uncapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        char first = value.charAt(0);
        if (!Character.isUpperCase(first)) {
            return value;
        }
        return Character.toLowerCase(first) + value.substring(1);
    }

    /**
     * Joins string parts with the given separator, skipping null elements.
     * Returns an empty string for an empty/null array.
     */
    public static String joinNonNull(String separator, String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String p : parts) {
            if (p == null) {
                continue;
            }
            if (!first) {
                sb.append(separator);
            }
            sb.append(p);
            first = false;
        }
        return sb.toString();
    }
}
