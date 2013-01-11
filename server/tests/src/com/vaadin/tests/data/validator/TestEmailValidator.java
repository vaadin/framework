package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.EmailValidator;

public class TestEmailValidator extends TestCase {

    private EmailValidator validator = new EmailValidator("Error");

    public void testEmailValidatorWithNull() {
        assertTrue(validator.isValid(null));
    }

    public void testEmailValidatorWithEmptyString() {
        assertTrue(validator.isValid(""));
    }

    public void testEmailValidatorWithFaultyString() {
        assertFalse(validator.isValid("not.an.email"));
    }

    public void testEmailValidatorWithOkEmail() {
        assertTrue(validator.isValid("my.name@email.com"));
    }
}
