package com.shopflow.shared.domain.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("VND");

    public Money {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero" + amount);
        }
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    public Money add(Money other) {
        checkCurrencyPreconditions(other);
        return new Money(amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        checkCurrencyPreconditions(other);
        if (this.amount.compareTo(other.amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Money(amount.subtract(other.amount), this.currency);
    }

    public Money multiply(double multiplier) {
        BigDecimal factor = BigDecimal.valueOf(multiplier);
        BigDecimal newAmount = this.amount.multiply(factor);
        return new Money(newAmount, this.currency);
    }

    public Money divide(double divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Cannot divide 0.");
        }
        BigDecimal divisorBD = BigDecimal.valueOf(divisor);
        int scale = this.currency.getDefaultFractionDigits();
        BigDecimal newAmount = this.amount.divide(divisorBD, scale, RoundingMode.HALF_EVEN);
        return new Money(newAmount, this.currency);
    }

    private void checkCurrencyPreconditions(Money other) {
        Objects.requireNonNull(other, "Cannot operate with null Money");
        if (! this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }

}
