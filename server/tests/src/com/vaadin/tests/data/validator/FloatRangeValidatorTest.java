package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.FloatRangeValidator;

public class FloatRangeValidatorTest extends TestCase {

    private FloatRangeValidator cleanValidator = new FloatRangeValidator(
            "no values", null, null);
    private FloatRangeValidator minValidator = new FloatRangeValidator(
            "no values", 10.1f, null);
    private FloatRangeValidator maxValidator = new FloatRangeValidator(
            "no values", null, 100.1f);
    private FloatRangeValidator minMaxValidator = new FloatRangeValidator(
            "no values", 10.5f, 100.5f);

    public void testNullValue() {
        assertTrue("Didn't accept null", cleanValidator.isValid(null));
        assertTrue("Didn't accept null", minValidator.isValid(null));
        assertTrue("Didn't accept null", maxValidator.isValid(null));
        assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    public void testMinValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(-15.0f));
        assertTrue("Didn't accept valid value", minValidator.isValid(10.1f));
        assertFalse("Accepted too small value", minValidator.isValid(10.0f));
    }

    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(1120.0f));
        assertTrue("Didn't accept valid value", maxValidator.isValid(15.0f));
        assertFalse("Accepted too large value", maxValidator.isValid(100.6f));
    }

    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(10.5f));
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(100.5f));
        assertFalse("Accepted too small value", minMaxValidator.isValid(10.4f));
        assertFalse("Accepted too large value", minMaxValidator.isValid(100.6f));
    }
}
