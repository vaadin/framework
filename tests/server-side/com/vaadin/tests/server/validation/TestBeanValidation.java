package com.vaadin.tests.server.validation;

import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.BeanValidationValidator;
import com.vaadin.tests.data.bean.BeanToValidate;

public class TestBeanValidation {
    @Test(expected = InvalidValueException.class)
    public void testBeanValidationNull() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "firstname");
        validator.validate(null);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationStringTooShort() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "firstname");
        validator.validate("aa");
    }

    @Test
    public void testBeanValidationStringOk() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "firstname");
        validator.validate("aaa");
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationIntegerTooSmall() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "age");
        validator.validate(17);
    }

    @Test
    public void testBeanValidationIntegerOk() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "age");
        validator.validate(18);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationTooManyDigits() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "decimals");
        validator.validate("1234.567");
    }

    @Test
    public void testBeanValidationDigitsOk() {
        BeanValidationValidator validator = new BeanValidationValidator(
                BeanToValidate.class, "decimals");
        validator.validate("123.45");
    }

}
