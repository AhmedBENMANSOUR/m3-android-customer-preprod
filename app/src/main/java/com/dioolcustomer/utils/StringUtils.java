package com.dioolcustomer.utils;

import android.text.TextUtils;

import java.util.Arrays;


public class StringUtils {

    /**
     * @param value string
     * @return is string is empty
     */
    public static boolean isEmpty(String value) {
        return value == null || TextUtils.isEmpty(value.trim());
    }

    /**
     * @param value String
     * @return the length of string
     */
    public static int getStringLength(final String value) {
        if (isEmpty(value)) {
            return 0;
        }
        return value.length();
    }

    /**
     * Capitalizes the first letter of the given screen if possible
     *
     * @param str string to capitalize
     * @return Capitalized string
     */
    public static String capitalizeFirstLetter(final String str) {
        return null != str && 0 < str.length() ? Character.toUpperCase(str.charAt(0)) + str.substring(1) : str;
    }

    /**
     * Checks whether the characters in the given string are all the same
     *
     * @param str string to check
     * @return true if all characters are the same, false if null, empty or when
     * not all characters are the same.
     */
    public static boolean sameChars(final String str) {
        if (null == str || 0 == str.length()) {
            return false;
        }
        final char[] reference = new char[str.length()];
        Arrays.fill(reference, str.charAt(0));
        return str.equals(String.valueOf(reference));
    }

    /**
     * Checks whether the characters in the given string are all consecutive
     *
     * @param str string to check
     * @return true if all characters are consecutive, false if null, empty or
     * when characters are not consecutive
     */
    public static boolean consecutiveChars(final String str) {
        if (null == str || 0 == str.length()) {
            return false;
        }
        char ref = str.charAt(0);
        int sign = 0;
        for (int index = 1; index < str.length(); ++index) {
            final char cmp = str.charAt(index);
            if (0 == sign) {
                sign = (0 < cmp - ref) ? 1 : -1;
            }
            if (sign != cmp - ref) {
                // not consecutive
                return false;
            }
            // next char
            ref = cmp;
        }
        return true;
    }
}
