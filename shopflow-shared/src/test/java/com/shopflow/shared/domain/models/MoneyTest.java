package com.shopflow.shared.domain.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void testCreation_Success() {
        Money money = Money.of(new BigDecimal("100000"));
        assertNotNull(money);
        assertEquals(0, new BigDecimal("100000").compareTo(money.amount()));
        assertEquals("VND", money.currency().getCurrencyCode());
    }

    @Test
    void testCreation_NullAmount_ThrowsException() {
        assertThrows(NullPointerException.class, () -> new Money(null, Currency.getInstance("VND")));
    }

    @Test
    void testCreation_NegativeAmount_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Money.of(new BigDecimal("-100"));
        });
        assertTrue(exception.getMessage().contains("Amount must be greater than zero"));
    }

    @Test
    void testZero() {
        Money zeroMoney = Money.zero();
        assertEquals(0, BigDecimal.ZERO.compareTo(zeroMoney.amount()));
    }

    @Test
    void testAdd_Success() {
        Money m1 = Money.of(new BigDecimal("100000"));
        Money m2 = Money.of(new BigDecimal("50000"));
        Money result = m1.add(m2);

        assertEquals(0, new BigDecimal("150000").compareTo(result.amount()));
    }

    @Test
    void testSubtract_Success() {
        Money m1 = Money.of(new BigDecimal("100000"));
        Money m2 = Money.of(new BigDecimal("30000"));
        Money result = m1.subtract(m2);

        assertEquals(0, new BigDecimal("70000").compareTo(result.amount()));
    }

    @Test
    void testSubtract_InsufficientFunds_ThrowsException() {
        Money m1 = Money.of(new BigDecimal("30000"));
        Money m2 = Money.of(new BigDecimal("100000"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> m1.subtract(m2));
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    void testMultiply_Success() {
        Money money = Money.of(new BigDecimal("100000"));
        Money result = money.multiply(1.5);
        
        // 100000 * 1.5 = 150000
        assertEquals(0, new BigDecimal("150000").compareTo(result.amount()));
    }

    @Test
    void testDivide_Success() {
        Money money = Money.of(new BigDecimal("100000"));
        Money result = money.divide(2.0);

        assertEquals(0, new BigDecimal("50000").compareTo(result.amount()));
    }

    @Test
    void testDivide_ByZero_ThrowsException() {
        Money money = Money.of(new BigDecimal("100000"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> money.divide(0.0));
        assertEquals("Cannot divide 0.", exception.getMessage());
    }
}
