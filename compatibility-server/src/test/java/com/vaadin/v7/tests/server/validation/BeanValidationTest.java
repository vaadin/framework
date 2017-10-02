package com.vaadin.v7.tests.server.validation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.validator.BeanValidator;
import com.vaadin.v7.ui.Field;

public class BeanValidationTest {
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
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "age");
        validator.validate(17);
    }

    @Test
    public void testBeanValidationIntegerOk() {
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "age");
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

    @Test
    public void testBeanValidationException_OneValidationError() {
        InvalidValueException[] causes = null;
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "lastname");
        try {
            validator.validate(null);
        } catch (InvalidValueException e) {
            causes = e.getCauses();
        }

        assertEquals(1, causes.length);
    }

    @Test
    public void testBeanValidationsException_TwoValidationErrors() {
        InvalidValueException[] causes = null;
        BeanValidator validator = new BeanValidator(BeanToValidate.class,
                "nickname");
        try {
            validator.validate("A");
        } catch (InvalidValueException e) {
            causes = e.getCauses();
        }

        assertEquals(2, causes.length);
    }

    @Test
    public void testBeanValidationNotAddedTwice() {
        // See ticket #11045
        BeanFieldGroup<BeanToValidate> fieldGroup = new BeanFieldGroup<BeanToValidate>(
                BeanToValidate.class);

        BeanToValidate beanToValidate = new BeanToValidate();
        beanToValidate.setFirstname("a");
        fieldGroup.setItemDataSource(beanToValidate);

        Field<?> nameField = fieldGroup.buildAndBind("firstname");
        assertEquals(1, nameField.getValidators().size());

        try {
            nameField.validate();
        } catch (InvalidValueException e) {
            // The 1 cause is from BeanValidator, where it tells what failed
            // 1 validation exception never gets wrapped.
            assertEquals(1, e.getCauses().length);
        }

        // Create new, identical bean to cause duplicate validator unless #11045
        // is fixed
        beanToValidate = new BeanToValidate();
        beanToValidate.setFirstname("a");
        fieldGroup.setItemDataSource(beanToValidate);

        assertEquals(1, nameField.getValidators().size());

        try {
            nameField.validate();
        } catch (InvalidValueException e) {
            // The 1 cause is from BeanValidator, where it tells what failed
            // 1 validation exception never gets wrapped.
            assertEquals(1, e.getCauses().length);
        }

    }

}
