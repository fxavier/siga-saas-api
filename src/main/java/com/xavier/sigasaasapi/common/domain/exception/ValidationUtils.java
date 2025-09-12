package com.xavier.sigasaasapi.common.domain.exception;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 * Provides static methods for validating various data types and business rules.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Validate that an object is not null.
     * @param object the object to validate
     * @param fieldName the name of the field
     * @throws ValidationException if object is null
     */
    public static void notNull(Object object, String fieldName) {
        if (object == null) {
            throw new ValidationException(fieldName, null, "must not be null");
        }
    }

    /**
     * Validate that a string is not null or empty.
     * @param value the string to validate
     * @param fieldName the name of the field
     * @throws ValidationException if string is null or empty
     */
    public static void notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, value, "must not be empty");
        }
    }

    /**
     * Validate that a collection is not null or empty.
     * @param collection the collection to validate
     * @param fieldName the name of the field
     * @throws ValidationException if collection is null or empty
     */
    public static void notEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName, collection, "must not be empty");
        }
    }

    /**
     * Validate that a string has a minimum length.
     * @param value the string to validate
     * @param minLength the minimum length
     * @param fieldName the name of the field
     * @throws ValidationException if string is shorter than minimum
     */
    public static void minLength(String value, int minLength, String fieldName) {
        if (value != null && value.length() < minLength) {
            throw new ValidationException(fieldName, value,
                    String.format("must have at least %d characters", minLength));
        }
    }

    /**
     * Validate that a string has a maximum length.
     * @param value the string to validate
     * @param maxLength the maximum length
     * @param fieldName the name of the field
     * @throws ValidationException if string is longer than maximum
     */
    public static void maxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName, value,
                    String.format("must have at most %d characters", maxLength));
        }
    }

    /**
     * Validate that a string matches a pattern.
     * @param value the string to validate
     * @param pattern the pattern to match
     * @param fieldName the name of the field
     * @param message the error message
     * @throws ValidationException if string doesn't match pattern
     */
    public static void matches(String value, Pattern pattern, String fieldName, String message) {
        if (value != null && !pattern.matcher(value).matches()) {
            throw new ValidationException(fieldName, value, message);
        }
    }

    /**
     * Validate that a number is positive.
     * @param value the number to validate
     * @param fieldName the name of the field
     * @throws ValidationException if number is not positive
     */
    public static void positive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            throw new ValidationException(fieldName, value, "must be positive");
        }
    }

    /**
     * Validate that a number is non-negative.
     * @param value the number to validate
     * @param fieldName the name of the field
     * @throws ValidationException if number is negative
     */
    public static void nonNegative(Number value, String fieldName) {
        if (value != null && value.doubleValue() < 0) {
            throw new ValidationException(fieldName, value, "must not be negative");
        }
    }

    /**
     * Validate that a BigDecimal is positive.
     * @param value the BigDecimal to validate
     * @param fieldName the name of the field
     * @throws ValidationException if BigDecimal is not positive
     */
    public static void positive(BigDecimal value, String fieldName) {
        if (value != null && value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName, value, "must be positive");
        }
    }

    /**
     * Validate that a number is within a range.
     * @param value the number to validate
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @param fieldName the name of the field
     * @throws ValidationException if number is outside range
     */
    public static void inRange(Number value, Number min, Number max, String fieldName) {
        if (value != null) {
            double v = value.doubleValue();
            if (v < min.doubleValue() || v > max.doubleValue()) {
                throw new ValidationException(fieldName, value,
                        String.format("must be between %s and %s", min, max));
            }
        }
    }

    /**
     * Validate that a date is in the future.
     * @param date the date to validate
     * @param fieldName the name of the field
     * @throws ValidationException if date is not in the future
     */
    public static void futureDate(LocalDate date, String fieldName) {
        if (date != null && !date.isAfter(LocalDate.now())) {
            throw new ValidationException(fieldName, date, "must be in the future");
        }
    }

    /**
     * Validate that a date is in the past.
     * @param date the date to validate
     * @param fieldName the name of the field
     * @throws ValidationException if date is not in the past
     */
    public static void pastDate(LocalDate date, String fieldName) {
        if (date != null && !date.isBefore(LocalDate.now())) {
            throw new ValidationException(fieldName, date, "must be in the past");
        }
    }

    /**
     * Validate that a datetime is in the future.
     * @param datetime the datetime to validate
     * @param fieldName the name of the field
     * @throws ValidationException if datetime is not in the future
     */
    public static void futureDateTime(LocalDateTime datetime, String fieldName) {
        if (datetime != null && !datetime.isAfter(LocalDateTime.now())) {
            throw new ValidationException(fieldName, datetime, "must be in the future");
        }
    }

    /**
     * Validate that a boolean condition is true.
     * @param condition the condition to validate
     * @param message the error message if condition is false
     * @throws ValidationException if condition is false
     */
    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validate that a boolean condition is false.
     * @param condition the condition to validate
     * @param message the error message if condition is true
     * @throws ValidationException if condition is true
     */
    public static void isFalse(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validate that a string is a valid NUIT (Mozambican tax number).
     * @param nuit the NUIT to validate
     * @param fieldName the name of the field
     * @throws ValidationException if NUIT is invalid
     */
    public static void validNuit(String nuit, String fieldName) {
        if (nuit != null) {
            String cleaned = nuit.replaceAll("[^0-9]", "");
            if (cleaned.length() != 9 || !cleaned.matches("^[1-9]\\d{8}$")) {
                throw new ValidationException(fieldName, nuit, "must be a valid NUIT (9 digits)");
            }
        }
    }
}