package com.vaadin.tests.data.validator;

import java.math.BigInteger;

import junit.framework.TestCase;

import com.vaadin.data.validator.BigIntegerRangeValidator;

public class BigIntegerRangeValidatorTest extends TestCase {

    private BigIntegerRangeValidator cleanValidator = new BigIntegerRangeValidator(
            "no values", null, null);
    private BigIntegerRangeValidator minValidator = new BigIntegerRangeValidator(
            "no values", BigInteger.valueOf(10), null);
    private BigIntegerRangeValidator maxValidator = new BigIntegerRangeValidator(
            "no values", null, BigInteger.valueOf(100));
    private BigIntegerRangeValidator minMaxValidator = new BigIntegerRangeValidator(
            "no values", BigInteger.valueOf(10), BigInteger.valueOf(100));

    public void testNullValue() {
        assertTrue("Didn't accept null", cleanValidator.isValid(null));
        assertTrue("Didn't accept null", minValidator.isValid(null));
        assertTrue("Didn't accept null", maxValidator.isValid(null));
        assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    public void testMinValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(BigInteger.valueOf(-15)));
        assertTrue("Didn't accept valid value",
                minValidator.isValid(BigInteger.valueOf(15)));
        assertFalse("Accepted too small value",
                minValidator.isValid(BigInteger.valueOf(9)));
    }

    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(BigInteger.valueOf(1120)));
        assertTrue("Didn't accept valid value",
                maxValidator.isValid(BigInteger.valueOf(15)));
        assertFalse("Accepted too large value",
                maxValidator.isValid(BigInteger.valueOf(120)));
    }

    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(BigInteger.valueOf(15)));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(BigInteger.valueOf(99)));
        assertFalse("Accepted too small value",
                minMaxValidator.isValid(BigInteger.valueOf(9)));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid(BigInteger.valueOf(110)));
    }
}
