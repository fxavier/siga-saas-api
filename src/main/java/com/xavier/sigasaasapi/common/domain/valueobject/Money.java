package com.xavier.sigasaasapi.common.domain.valueobject;
/**
 * Money value object representing a monetary amount.
 * Provides methods for arithmetic operations and comparisons.
 * Ensures immutability and non-negative values.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
    private final BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-null and non-negative");
        }
        this.amount = amount;
    }

    public boolean isGreaterThanZeruo() {
        return this.amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money must be non-null");
        }
        return this.amount.compareTo(other.amount) > 0;
    }

    public Money add(Money money) {
        return new Money(setScale(this.amount.add(money.getAmount())));
    }

    public Money subtract(Money money) {
        BigDecimal result = this.amount.subtract(money.getAmount());
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting amount cannot be negative");
        }
        return new Money(setScale(result));
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be non-negative");
        }
        return new Money(setScale(this.amount.multiply(BigDecimal.valueOf(multiplier))));
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}
