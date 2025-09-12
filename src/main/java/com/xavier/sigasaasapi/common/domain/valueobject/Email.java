package com.xavier.sigasaasapi.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email value object representing a valid email address.
 * Ensures immutability and validates email format.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public class Email {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    private final String value;

    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        // Check for spaces in the email address
        if (value.contains(" ")) {
            throw new IllegalArgumentException("Email address cannot contain spaces");
        }


        String normalizedEmail = value.trim().toLowerCase();
        if (!isValid(normalizedEmail)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }

        this.value = normalizedEmail;
    }

    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Additional validation for consecutive dots
        if (email.contains("..")) {
            return false;
        }

        return PATTERN.matcher(email.trim().toLowerCase()).matches();
    }



    public String getValue() {
        return value;
    }

    public String getDomain() {
        int atIndex = value.indexOf('@');
        return atIndex != -1 ? value.substring(atIndex + 1) : "";
    }

    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return atIndex != -1 ? value.substring(0, atIndex) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}