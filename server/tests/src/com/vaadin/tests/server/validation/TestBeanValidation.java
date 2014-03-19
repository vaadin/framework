package com.vaadin.tests.server.validation;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.tests.data.bean.BeanToValidate;
import com.vaadin.ui.Field;

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

        Assert.assertEquals(1, causes.length);
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

        Assert.assertEquals(2, causes.length);
    }

    public void testBeanValidationNotAddedTwice() {
        // See ticket #11045
        BeanFieldGroup<BeanToValidate> fieldGroup = new BeanFieldGroup<BeanToValidate>(
                BeanToValidate.class);

        BeanToValidate beanToValidate = new BeanToValidate();
        beanToValidate.setFirstname("a");
        fieldGroup.setItemDataSource(beanToValidate);

        Field<?> nameField = fieldGroup.buildAndBind("firstname");
        Assert.assertEquals(1, nameField.getValidators().size());

        try {
            nameField.validate();
        } catch (InvalidValueException e) {
            // NOTE: causes are empty if only one validation fails
            Assert.assertEquals(0, e.getCauses().length);
        }

        // Create new, identical bean to cause duplicate validator unless #11045
        // is fixed
        beanToValidate = new BeanToValidate();
        beanToValidate.setFirstname("a");
        fieldGroup.setItemDataSource(beanToValidate);

        Assert.assertEquals(1, nameField.getValidators().size());

        try {
            nameField.validate();
        } catch (InvalidValueException e) {
            // NOTE: if more than one validation fails, we get the number of
            // failed validations
            Assert.assertEquals(0, e.getCauses().length);
        }

    }

}
