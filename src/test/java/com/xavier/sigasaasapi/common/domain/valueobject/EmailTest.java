package com.xavier.sigasaasapi.common.domain.valueobject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Email value object.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
class EmailTest {

    @Test
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail() {
        String validEmail = "user@example.com";
        Email email = new Email(validEmail);

        assertNotNull(email);
        assertEquals("user@example.com", email.getValue());
        assertEquals("example.com", email.getDomain());
        assertEquals("user", email.getLocalPart());
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmail() {
        Email email = new Email("User@Example.COM");
        assertEquals("user@example.com", email.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@domain.com",
            "user.name@example.co.mz",
            "first+last@company.org",
            "email123@sub.domain.com"
    })
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidFormats(String validEmail) {
        assertDoesNotThrow(() -> new Email(validEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "@domain.com",
            "user@",
            "user@.com",
            "user@domain",
            "user space@domain.com",
            "user@domain..com"
    })
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidFormats(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject null or empty email")
    void shouldRejectNullOrEmpty(String email) {
        assertThrows(IllegalArgumentException.class, () -> new Email(email));
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void shouldImplementEqualsAndHashCode() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        Email email3 = new Email("other@example.com");

        assertEquals(email1, email2);
        assertNotEquals(email1, email3);
        assertEquals(email1.hashCode(), email2.hashCode());
        assertNotEquals(email1.hashCode(), email3.hashCode());
    }
}

/**
 * Unit tests for Phone value object.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
class PhoneTest {

    @Test
    @DisplayName("Should create valid Mozambican mobile number")
    void shouldCreateValidMozambicanMobile() {
        Phone phone = new Phone("847123456");

        assertNotNull(phone);
        assertEquals("258847123456", phone.getValue());
        assertEquals(Phone.PhoneType.MOBILE, phone.getType());
        assertEquals("+258 84 712 3456", phone.getFormatted());
    }

    @Test
    @DisplayName("Should create valid Mozambican landline")
    void shouldCreateValidMozambicanLandline() {
        Phone phone = new Phone("21123456");

        assertNotNull(phone);
        assertEquals("25821123456", phone.getValue());
        assertEquals(Phone.PhoneType.LANDLINE, phone.getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "847123456",
            "258847123456",
            "+258847123456",
            "84 712 3456",
            "84-712-3456"
    })
    @DisplayName("Should accept various mobile formats")
    void shouldAcceptVariousMobileFormats(String phoneNumber) {
        assertDoesNotThrow(() -> new Phone(phoneNumber));
    }

    @Test
    @DisplayName("Should normalize phone numbers")
    void shouldNormalizePhoneNumbers() {
        Phone phone1 = new Phone("84-712-3456");
        Phone phone2 = new Phone("84 712 3456");
        Phone phone3 = new Phone("847123456");

        assertEquals(phone1.getValue(), phone2.getValue());
        assertEquals(phone2.getValue(), phone3.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject null or empty phone")
    void shouldRejectNullOrEmpty(String phone) {
        assertThrows(IllegalArgumentException.class, () -> new Phone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "99999999999999999",
            "abcdefghi",
            "12-345-678"
    })
    @DisplayName("Should reject invalid phone formats")
    void shouldRejectInvalidFormats(String invalidPhone) {
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }
}

/**
 * Unit tests for Money value object.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
class MoneyTest {

    @Test
    @DisplayName("Should create valid money amount")
    void shouldCreateValidMoney() {
        Money money = new Money(new BigDecimal("100.50"));

        assertNotNull(money);
        assertEquals(new BigDecimal("100.50"), money.getAmount());
        assertTrue(money.isGreaterThanZeruo());
    }

    @Test
    @DisplayName("Should add money amounts")
    void shouldAddMoney() {
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"));

        Money result = money1.add(money2);

        assertEquals(new BigDecimal("150.75"), result.getAmount());
    }

    @Test
    @DisplayName("Should subtract money amounts")
    void shouldSubtractMoney() {
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"));

        Money result = money1.subtract(money2);

        assertEquals(new BigDecimal("50.25"), result.getAmount());
    }

    @Test
    @DisplayName("Should multiply money by integer")
    void shouldMultiplyMoney() {
        Money money = new Money(new BigDecimal("10.50"));

        Money result = money.multiply(3);

        assertEquals(new BigDecimal("31.50"), result.getAmount());
    }

    @Test
    @DisplayName("Should reject negative amounts")
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Should reject negative subtraction result")
    void shouldRejectNegativeSubtractionResult() {
        Money money1 = new Money(new BigDecimal("50.00"));
        Money money2 = new Money(new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class,
                () -> money1.subtract(money2));
    }

    @Test
    @DisplayName("Should reject negative multiplier")
    void shouldRejectNegativeMultiplier() {
        Money money = new Money(new BigDecimal("10.00"));

        assertThrows(IllegalArgumentException.class,
                () -> money.multiply(-2));
    }

    @Test
    @DisplayName("Should compare money amounts")
    void shouldCompareMoney() {
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("50.00"));

        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
    }

    @Test
    @DisplayName("Should handle ZERO constant")
    void shouldHandleZeroConstant() {
        assertNotNull(Money.ZERO);
        assertEquals(BigDecimal.ZERO, Money.ZERO.getAmount());
        assertFalse(Money.ZERO.isGreaterThanZeruo());
    }
}