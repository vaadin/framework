package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.StringLengthValidator;

public class TestStringLengthValidator extends TestCase {

    private StringLengthValidator validator = new StringLengthValidator("Error");
    private StringLengthValidator validatorNoNull = new StringLengthValidator(
            "Error", 0, 5, false);
    private StringLengthValidator validatorMinValue = new StringLengthValidator(
            "Error", 5, 15, true);

    public void testValidatorWithNull() {
        assertTrue("Didn't accept null", validator.isValid(null));
        assertTrue("Didn't accept null", validatorMinValue.isValid(null));
    }

    public void testValidatorNotAcceptingNull() {
        assertFalse("Accepted null", validatorNoNull.isValid(null));
    }

    public void testEmptyString() {
        assertTrue("Didn't accept empty String", validator.isValid(""));
        assertTrue("Didn't accept empty String", validatorNoNull.isValid(""));
    }

    public void testTooLongString() {
        assertFalse("Too long string was accepted",
                validatorNoNull.isValid("This string is too long"));
    }

    public void testStringLengthValidatorWithOkStringLength() {
        assertTrue("Didn't accept string of correct length",
                validatorNoNull.isValid("OK!"));
    }

    public void testTooShortStringLength() {
        assertFalse("Accepted a string that was too short.",
                validatorMinValue.isValid("shot"));
    }
}
