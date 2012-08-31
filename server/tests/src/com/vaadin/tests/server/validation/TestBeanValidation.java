package com.vaadin.tests.server.validation;

import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.tests.data.bean.BeanToValidate;

public class TestBeanValidation {
    @Test(expected = InvalidValueException.class)
    public void testBeanValidationNull() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate(null);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationStringTooShort() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate("aa");
    }

    @Test
    public void testBeanValidationStringOk() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate("aaa");
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationIntegerTooSmall() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class, "age");
        validator.validate(17);
    }

    @Test
    public void testBeanValidationIntegerOk() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class, "age");
        validator.validate(18);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationTooManyDigits() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "decimals");
        validator.validate("1234.567");
    }

    @Test
    public void testBeanValidationDigitsOk() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "decimals");
        validator.validate("123.45");
    }

}
