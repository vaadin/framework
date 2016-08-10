package com.vaadin.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.legacy.data.validator.LegacyByteRangeValidator;

public class ByteRangeValidatorTest {

    private LegacyByteRangeValidator cleanValidator = new LegacyByteRangeValidator(
            "no values", null, null);
    private LegacyByteRangeValidator minValidator = new LegacyByteRangeValidator(
            "no values", (byte) 10, null);
    private LegacyByteRangeValidator maxValidator = new LegacyByteRangeValidator(
            "no values", null, (byte) 100);
    private LegacyByteRangeValidator minMaxValidator = new LegacyByteRangeValidator(
            "no values", (byte) 10, (byte) 100);

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
                cleanValidator.isValid((byte) -15));
        assertTrue("Didn't accept valid value", minValidator.isValid((byte) 15));
        assertFalse("Accepted too small value", minValidator.isValid((byte) 9));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid((byte) 112));
        assertTrue("Didn't accept valid value", maxValidator.isValid((byte) 15));
        assertFalse("Accepted too large value",
                maxValidator.isValid((byte) 120));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((byte) 15));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((byte) 99));
        assertFalse("Accepted too small value",
                minMaxValidator.isValid((byte) 9));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid((byte) 110));
    }
}
