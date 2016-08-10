package com.vaadin.tests.server.validation;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.legacy.data.Validator.InvalidValueException;
import com.vaadin.legacy.data.validator.LegacyBeanValidator;
import com.vaadin.legacy.ui.LegacyField;
import com.vaadin.tests.data.bean.BeanToValidate;

public class BeanValidationTest {
    @Test(expected = InvalidValueException.class)
    public void testBeanValidationNull() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate(null);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationStringTooShort() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate("aa");
    }

    @Test
    public void testBeanValidationStringOk() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "firstname");
        validator.validate("aaa");
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationIntegerTooSmall() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class, "age");
        validator.validate(17);
    }

    @Test
    public void testBeanValidationIntegerOk() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class, "age");
        validator.validate(18);
    }

    @Test(expected = InvalidValueException.class)
    public void testBeanValidationTooManyDigits() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "decimals");
        validator.validate("1234.567");
    }

    @Test
    public void testBeanValidationDigitsOk() {
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "decimals");
        validator.validate("123.45");
    }

    @Test
    public void testBeanValidationException_OneValidationError() {
        InvalidValueException[] causes = null;
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
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
        LegacyBeanValidator validator = new LegacyBeanValidator(BeanToValidate.class,
                "nickname");
        try {
            validator.validate("A");
        } catch (InvalidValueException e) {
            causes = e.getCauses();
        }

        Assert.assertEquals(2, causes.length);
    }

    @Test
    public void testBeanValidationNotAddedTwice() {
        // See ticket #11045
        BeanFieldGroup<BeanToValidate> fieldGroup = new BeanFieldGroup<BeanToValidate>(
                BeanToValidate.class);

        BeanToValidate beanToValidate = new BeanToValidate();
        beanToValidate.setFirstname("a");
        fieldGroup.setItemDataSource(beanToValidate);

        LegacyField<?> nameField = fieldGroup.buildAndBind("firstname");
        Assert.assertEquals(1, nameField.getValidators().size());

        try {
            nameField.validate();
        } catch (InvalidValueException e) {
            // The 1 cause is from BeanValidator, where it tells what failed
            // 1 validation exception never gets wrapped.
            Assert.assertEquals(1, e.getCauses().length);
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
            // The 1 cause is from BeanValidator, where it tells what failed
            // 1 validation exception never gets wrapped.
            Assert.assertEquals(1, e.getCauses().length);
        }

    }

}
