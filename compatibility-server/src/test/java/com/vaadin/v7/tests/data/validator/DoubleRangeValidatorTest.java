package com.vaadin.v7.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.DoubleRangeValidator;

public class DoubleRangeValidatorTest {

    private DoubleRangeValidator cleanValidator = new DoubleRangeValidator(
            "no values", null, null);
    private DoubleRangeValidator minValidator = new DoubleRangeValidator(
            "no values", 10.1, null);
    private DoubleRangeValidator maxValidator = new DoubleRangeValidator(
            "no values", null, 100.1);
    private DoubleRangeValidator minMaxValidator = new DoubleRangeValidator(
            "no values", 10.5, 100.5);

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
                cleanValidator.isValid(-15.0));
        assertTrue("Didn't accept valid value", minValidator.isValid(10.1));
        assertFalse("Accepted too small value", minValidator.isValid(10.0));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(1120.0));
        assertTrue("Didn't accept valid value", maxValidator.isValid(15.0));
        assertFalse("Accepted too large value", maxValidator.isValid(100.6));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(10.5));
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(100.5));
        assertFalse("Accepted too small value", minMaxValidator.isValid(10.4));
        assertFalse("Accepted too large value", minMaxValidator.isValid(100.6));
    }
}
