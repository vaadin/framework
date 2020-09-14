package com.vaadin.tests.data.validator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.validator.EmailValidator;

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

    @Test
    public void testEmailValidatorWithBadInput() {
        Assert.assertFalse(validator.isValid("a@a.m5qRt8zLxQG4mMeu9yKZm5qRt8zLxQG4mMeu9yKZm5qRt8zLxQG4mMeu9yKZ&"));
    }

}
