package com.vaadin.v7.tests.data.validator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.validator.EmailValidator;

public class EmailValidatorTest {

    private EmailValidator validator = new EmailValidator("Error");

    @Test
    public void testEmailValidatorWithNull() {
        Assert.assertTrue(validator.isValid(null));
    }

    @Test
    public void testEmailValidatorWithEmptyString() {
        Assert.assertTrue(validator.isValid(""));
    }

    @Test
    public void testEmailValidatorWithFaultyString() {
        Assert.assertFalse(validator.isValid("not.an.email"));
    }

    @Test
    public void testEmailValidatorWithOkEmail() {
        Assert.assertTrue(validator.isValid("my.name@email.com"));
    }
}
