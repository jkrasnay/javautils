package ca.krasnay.javautils;

import java.util.Arrays;
import java.util.List;

/**
 * Utility methods related to strings.
 *
 * @author John Krasnay <john@krasnay.ca>
 */
public final class StringUtils {

    /**
     * Null-safe equals method. Returns true if both strings are null, or if
     * neither are null and comparing them with String.equals returns true.
     */
    public static boolean equals(String s1, String s2) {
        return ObjectUtils.equals(s1, s2);
    }

    /**
     * Returns true if the given string is null, empty, or consists of only
     * whitespace.
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * Returns false if the given string is null, empty, or consists of only
     * whitespace.
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    /**
     * Returns a string containing the string representation of each item in the
     * given list, separated by the given separator.
     */
    public static String join(String separator, List<?> items) {

        assert separator != null;
        assert items != null;

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : items) {

            if (!first) {
                sb.append(separator);
            }

            sb.append(item);

            first = false;
        }

        return sb.toString();
    }

    /**
     * Returns a string containing the string representation of each item in the
     * given list, separated by the given separator.
     */
    public static String join(String separator, Object... items) {

        assert items != null;

        return join(separator, Arrays.asList(items));
    }

    /**
     * Array-aware toString method. If the given value is an array, passes it to
     * one of the toString classes in the JDK's Arrays class, else returns the
     * result of its toString method.
     */
    public static String toString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof boolean[]) {
            return Arrays.toString((boolean[]) value);
        } else if (value instanceof char[]) {
            return Arrays.toString((char[]) value);
        } else if (value instanceof byte[]) {
            return Arrays.toString((byte[]) value);
        } else if (value instanceof short[]) {
            return Arrays.toString((short[]) value);
        } else if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        } else if (value instanceof long[]) {
            return Arrays.toString((long[]) value);
        } else if (value instanceof float[]) {
            return Arrays.toString((float[]) value);
        } else if (value instanceof double[]) {
            return Arrays.toString((double[]) value);
        } else if (value instanceof Object[]) {
            return Arrays.toString((Object[]) value);
        } else {
            return value.toString();
        }
    }

    private StringUtils() {
    }
}
