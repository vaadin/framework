package com.vaadin.tests.data.validator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.legacy.data.validator.LegacyEmailValidator;

public class EmailValidatorTest {

    private LegacyEmailValidator validator = new LegacyEmailValidator("Error");

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
