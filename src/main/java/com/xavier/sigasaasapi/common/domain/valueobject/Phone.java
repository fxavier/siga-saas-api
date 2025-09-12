package com.xavier.sigasaasapi.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Phone value object representing a valid phone number.
 * Supports Mozambican phone numbers and international formats.
 * Ensures immutability and validates phone format.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

public class Phone {
    // Mozambican phone number patterns
    private static final String MZ_MOBILE_PATTERN = "^(\\+258|258)?[8][2-7]\\d{7}$";
    private static final String MZ_LANDLINE_PATTERN = "^(\\+258|258)?[2][1-9]\\d{6}$";
    // International format
    private static final String INTL_PATTERN = "^\\+[1-9]\\d{1,14}$";

    private static final Pattern MZ_MOBILE = Pattern.compile(MZ_MOBILE_PATTERN);
    private static final Pattern MZ_LANDLINE = Pattern.compile(MZ_LANDLINE_PATTERN);
    private static final Pattern INTL = Pattern.compile(INTL_PATTERN);

    private final String value;
    private final PhoneType type;

    public enum PhoneType {
        MOBILE,
        LANDLINE,
        INTERNATIONAL
    }

    public Phone(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        String normalized = normalizePhone(value);

        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }

        this.value = normalized;
        this.type = determineType(normalized);
    }

    private String normalizePhone(String phone) {
        // Remove all non-digit characters except +
        String cleaned = phone.replaceAll("[^\\d+]", "");

        // If starts with 8 and has 9 digits, assume it's Mozambican
        if (cleaned.matches("^8[2-7]\\d{7}$")) {
            cleaned = "258" + cleaned;
        }
        // If starts with 2 and has 8 digits, assume it's Mozambican landline
        else if (cleaned.matches("^2[1-9]\\d{6}$")) {
            cleaned = "258" + cleaned;
        }

        return cleaned;
    }

    public static boolean isValid(String phone) {
        if (phone == null) return false;
        return MZ_MOBILE.matcher(phone).matches() ||
                MZ_LANDLINE.matcher(phone).matches() ||
                INTL.matcher(phone).matches();
    }

    private PhoneType determineType(String phone) {
        if (MZ_MOBILE.matcher(phone).matches()) {
            return PhoneType.MOBILE;
        } else if (MZ_LANDLINE.matcher(phone).matches()) {
            return PhoneType.LANDLINE;
        } else {
            return PhoneType.INTERNATIONAL;
        }
    }

    public String getValue() {
        return value;
    }

    public PhoneType getType() {
        return type;
    }

    public String getFormatted() {
        if (value.startsWith("258") && value.length() == 12) {
            // Format as +258 XX XXX XXXX
            return "+" + value.substring(0, 3) + " " +
                    value.substring(3, 5) + " " +
                    value.substring(5, 8) + " " +
                    value.substring(8);
        }
        return "+" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(value, phone.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
