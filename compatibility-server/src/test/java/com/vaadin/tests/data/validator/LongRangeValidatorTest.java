package com.vaadin.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.LegacyLongRangeValidator;

public class LongRangeValidatorTest {

    private LegacyLongRangeValidator cleanValidator = new LegacyLongRangeValidator(
            "no values", null, null);
    private LegacyLongRangeValidator minValidator = new LegacyLongRangeValidator(
            "no values", 10l, null);
    private LegacyLongRangeValidator maxValidator = new LegacyLongRangeValidator(
            "no values", null, 100l);
    private LegacyLongRangeValidator minMaxValidator = new LegacyLongRangeValidator(
            "no values", 10l, 100l);

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
                cleanValidator.isValid(-15l));
        assertTrue("Didn't accept valid value", minValidator.isValid(15l));
        assertFalse("Accepted too small value", minValidator.isValid(9l));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(1120l));
        assertTrue("Didn't accept valid value", maxValidator.isValid(15l));
        assertFalse("Accepted too large value", maxValidator.isValid(120l));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(15l));
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(99l));
        assertFalse("Accepted too small value", minMaxValidator.isValid(9l));
        assertFalse("Accepted too large value", minMaxValidator.isValid(110l));
    }
}
