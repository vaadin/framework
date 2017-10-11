package com.vaadin.v7.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.vaadin.v7.data.validator.BigDecimalRangeValidator;

public class BigDecimalRangeValidatorTest {

    private BigDecimalRangeValidator cleanValidator = new BigDecimalRangeValidator(
            "no values", null, null);
    private BigDecimalRangeValidator minValidator = new BigDecimalRangeValidator(
            "no values", new BigDecimal(10.1), null);
    private BigDecimalRangeValidator maxValidator = new BigDecimalRangeValidator(
            "no values", null, new BigDecimal(100.1));
    private BigDecimalRangeValidator minMaxValidator = new BigDecimalRangeValidator(
            "no values", new BigDecimal(10.5), new BigDecimal(100.5));

    @Test
    public void testNullValue() {
        assertTrue("Didn't accept null", cleanValidator.isValid(null));
        assertTrue("Didn't accept null", minValidator.isValid(null));
        assertTrue("Didn't accept null", maxValidator.isValid(null));
        assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(new BigDecimal(-15.0)));
        assertTrue("Didn't accept valid value",
                minValidator.isValid(new BigDecimal(10.1)));
        assertFalse("Accepted too small value",
                minValidator.isValid(new BigDecimal(10.0)));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(new BigDecimal(1120.0)));
        assertTrue("Didn't accept valid value",
                maxValidator.isValid(new BigDecimal(15.0)));
        assertFalse("Accepted too large value",
                maxValidator.isValid(new BigDecimal(100.6)));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(new BigDecimal(10.5)));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(new BigDecimal(100.5)));
        assertFalse("Accepted too small value",
                minMaxValidator.isValid(new BigDecimal(10.4)));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid(new BigDecimal(100.6)));
    }
}
