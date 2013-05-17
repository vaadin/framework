package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.StringLengthValidator;

public class TestStringLengthValidator extends TestCase {

    private StringLengthValidator validator = new StringLengthValidator("Error");
    private StringLengthValidator validatorNoNull = new StringLengthValidator(
            "Error", 1, 5, false);
    private StringLengthValidator validatorMinValue = new StringLengthValidator(
            "Error", 5, null, true);
    private StringLengthValidator validatorMaxValue = new StringLengthValidator(
            "Error", null, 15, true);

    public void testValidatorWithNull() {
        assertTrue("Didn't accept null", validator.isValid(null));
        assertTrue("Didn't accept null", validatorMinValue.isValid(null));
    }

    public void testValidatorNotAcceptingNull() {
        assertFalse("Accepted null", validatorNoNull.isValid(null));
    }

    public void testEmptyString() {
        assertTrue("Didn't accept empty String", validator.isValid(""));
        assertTrue("Didn't accept empty String", validatorMaxValue.isValid(""));
        assertFalse("Accepted empty string even though has lower bound of 1",
                validatorNoNull.isValid(""));
        assertFalse("Accepted empty string even though has lower bound of 5",
                validatorMinValue.isValid(""));
    }

    public void testTooLongString() {
        assertFalse("Too long string was accepted",
                validatorNoNull.isValid("This string is too long"));
        assertFalse("Too long string was accepted",
                validatorMaxValue.isValid("This string is too long"));
    }

    public void testNoUpperBound() {
        assertTrue(
                "String not accepted even though no upper bound",
                validatorMinValue
                        .isValid("This is a really long string to test that no upper bound exists"));
    }

    public void testNoLowerBound() {
        assertTrue("Didn't accept short string", validatorMaxValue.isValid(""));
        assertTrue("Didn't accept short string", validatorMaxValue.isValid("1"));
    }

    public void testStringLengthValidatorWithOkStringLength() {
        assertTrue("Didn't accept string of correct length",
                validatorNoNull.isValid("OK!"));
        assertTrue("Didn't accept string of correct length",
                validatorMaxValue.isValid("OK!"));
    }

    public void testTooShortStringLength() {
        assertFalse("Accepted a string that was too short.",
                validatorMinValue.isValid("shot"));
    }
}
