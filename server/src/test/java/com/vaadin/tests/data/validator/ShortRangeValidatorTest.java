package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.ShortRangeValidator;

public class ShortRangeValidatorTest extends TestCase {

    private ShortRangeValidator cleanValidator = new ShortRangeValidator(
            "no values", null, null);
    private ShortRangeValidator minValidator = new ShortRangeValidator(
            "no values", (short) 10, null);
    private ShortRangeValidator maxValidator = new ShortRangeValidator(
            "no values", null, (short) 100);
    private ShortRangeValidator minMaxValidator = new ShortRangeValidator(
            "no values", (short) 10, (short) 100);

    public void testNullValue() {
        assertTrue("Didn't accept null", cleanValidator.isValid(null));
        assertTrue("Didn't accept null", minValidator.isValid(null));
        assertTrue("Didn't accept null", maxValidator.isValid(null));
        assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    public void testMinValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid((short) -15));
        assertTrue("Didn't accept valid value",
                minValidator.isValid((short) 15));
        assertFalse("Accepted too small value", minValidator.isValid((short) 9));
    }

    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid((short) 1120));
        assertTrue("Didn't accept valid value",
                maxValidator.isValid((short) 15));
        assertFalse("Accepted too large value",
                maxValidator.isValid((short) 120));
    }

    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((short) 15));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((short) 99));
        assertFalse("Accepted too small value",
                minMaxValidator.isValid((short) 9));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid((short) 110));
    }
}
