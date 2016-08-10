package com.vaadin.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.legacy.data.validator.LegacyShortRangeValidator;

public class ShortRangeValidatorTest {

    private LegacyShortRangeValidator cleanValidator = new LegacyShortRangeValidator(
            "no values", null, null);
    private LegacyShortRangeValidator minValidator = new LegacyShortRangeValidator(
            "no values", (short) 10, null);
    private LegacyShortRangeValidator maxValidator = new LegacyShortRangeValidator(
            "no values", null, (short) 100);
    private LegacyShortRangeValidator minMaxValidator = new LegacyShortRangeValidator(
            "no values", (short) 10, (short) 100);

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
                cleanValidator.isValid((short) -15));
        assertTrue("Didn't accept valid value",
                minValidator.isValid((short) 15));
        assertFalse("Accepted too small value", minValidator.isValid((short) 9));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid((short) 1120));
        assertTrue("Didn't accept valid value",
                maxValidator.isValid((short) 15));
        assertFalse("Accepted too large value",
                maxValidator.isValid((short) 120));
    }

    @Test
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
