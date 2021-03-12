package com.vaadin.v7.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.EmailValidator;

public class EmailValidatorTest {

    private EmailValidator validator = new EmailValidator("Error");

    @Test
    public void testEmailValidatorWithNull() {
        assertTrue(validator.isValid(null));
    }

    @Test
    public void testEmailValidatorWithEmptyString() {
        assertTrue(validator.isValid(""));
    }

    @Test
    public void testEmailValidatorWithFaultyString() {
        assertFalse(validator.isValid("not.an.email"));
    }

    @Test
    public void testEmailValidatorWithOkEmail() {
        assertTrue(validator.isValid("my.name@email.com"));
    }

    @Test
    public void testEmailValidatorWithBadInput() {
        Assert.assertFalse(validator.isValid("a@a.m5qRt8zLxQG4mMeu9yKZm5qRt8zLxQG4mMeu9yKZm5qRt8zLxQG4mMeu9yKZ&"));
    }
}
