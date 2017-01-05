package com.vaadin.v7.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.StringLengthValidator;

public class StringLengthValidatorTest {

    private StringLengthValidator validator = new StringLengthValidator(
            "Error");
    private StringLengthValidator validatorNoNull = new StringLengthValidator(
            "Error", 1, 5, false);
    private StringLengthValidator validatorMinValue = new StringLengthValidator(
            "Error", 5, null, true);
    private StringLengthValidator validatorMaxValue = new StringLengthValidator(
            "Error", null, 15, true);

    @Test
    public void testValidatorWithNull() {
        assertTrue("Didn't accept null", validator.isValid(null));
        assertTrue("Didn't accept null", validatorMinValue.isValid(null));
    }

    @Test
    public void testValidatorNotAcceptingNull() {
        assertFalse("Accepted null", validatorNoNull.isValid(null));
    }

    @Test
    public void testEmptyString() {
        assertTrue("Didn't accept empty String", validator.isValid(""));
        assertTrue("Didn't accept empty String", validatorMaxValue.isValid(""));
        assertFalse("Accepted empty string even though has lower bound of 1",
                validatorNoNull.isValid(""));
        assertFalse("Accepted empty string even though has lower bound of 5",
                validatorMinValue.isValid(""));
    }

    @Test
    public void testTooLongString() {
        assertFalse("Too long string was accepted",
                validatorNoNull.isValid("This string is too long"));
        assertFalse("Too long string was accepted",
                validatorMaxValue.isValid("This string is too long"));
    }

    @Test
    public void testNoUpperBound() {
        assertTrue("String not accepted even though no upper bound",
                validatorMinValue.isValid(
                        "This is a really long string to test that no upper bound exists"));
    }

    @Test
    public void testNoLowerBound() {
        assertTrue("Didn't accept short string", validatorMaxValue.isValid(""));
        assertTrue("Didn't accept short string",
                validatorMaxValue.isValid("1"));
    }

    @Test
    public void testStringLengthValidatorWithOkStringLength() {
        assertTrue("Didn't accept string of correct length",
                validatorNoNull.isValid("OK!"));
        assertTrue("Didn't accept string of correct length",
                validatorMaxValue.isValid("OK!"));
    }

    @Test
    public void testTooShortStringLength() {
        assertFalse("Accepted a string that was too short.",
                validatorMinValue.isValid("shot"));
    }
}
