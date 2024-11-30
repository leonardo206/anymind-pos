package com.anymind.pos.validator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceValidatorTest {

    private final PriceValidator priceValidator = new PriceValidator();

    @Test
    void testValidPrice() {
        BigDecimal validPrice = new BigDecimal("10.50");
        assertTrue(priceValidator.isValid(validPrice, null), "The price should be valid.");
    }

    @Test
    void testZeroPrice() {
        BigDecimal zeroPrice = BigDecimal.ZERO;
        assertFalse(priceValidator.isValid(zeroPrice, null), "The price should not be valid for zero.");
    }

    @Test
    void testNegativePrice() {
        BigDecimal negativePrice = new BigDecimal("-5.00");
        assertFalse(priceValidator.isValid(negativePrice, null), "The price should not be valid for negative values.");
    }

    @Test
    void testNullPrice() {
        assertFalse(priceValidator.isValid(null, null), "The price should not be valid when it's null.");
    }
}

