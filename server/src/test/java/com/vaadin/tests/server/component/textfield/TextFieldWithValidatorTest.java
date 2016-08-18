package com.vaadin.tests.server.component.textfield;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.validator.LegacyEmailValidator;
import com.vaadin.v7.data.validator.LegacyRegexpValidator;
import com.vaadin.v7.data.validator.LegacyStringLengthValidator;
import com.vaadin.v7.ui.LegacyTextField;

public class TextFieldWithValidatorTest {

    private LegacyTextField field;
    private ObjectProperty<String> property;

    @Before
    public void setUp() {

        field = new LegacyTextField();
        field.setInvalidAllowed(false);
        property = new ObjectProperty<String>("original");
        field.setPropertyDataSource(property);
    }

    @Test
    public void testMultipleValidators() {
        field.addValidator(new LegacyStringLengthValidator(
                "Length not between 1 and 3", 1, 3, false));
        field.addValidator(new LegacyStringLengthValidator(
                "Length not between 2 and 4", 2, 4, false));

        // fails
        try {
            field.setValue("a");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
        // succeeds
        field.setValue("ab");
        // fails
        try {
            field.setValue("abcd");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
    }

    @Test
    public void testRemoveValidator() {
        Validator validator1 = new LegacyStringLengthValidator(
                "Length not between 1 and 3", 1, 3, false);
        Validator validator2 = new LegacyStringLengthValidator(
                "Length not between 2 and 4", 2, 4, false);

        field.addValidator(validator1);
        field.addValidator(validator2);
        field.removeValidator(validator1);

        // fails
        try {
            field.setValue("a");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
        // succeeds
        field.setValue("ab");
        // succeeds
        field.setValue("abcd");
    }

    @Test
    public void testRemoveAllValidators() {
        Validator validator1 = new LegacyStringLengthValidator(
                "Length not between 1 and 3", 1, 3, false);
        Validator validator2 = new LegacyStringLengthValidator(
                "Length not between 2 and 4", 2, 4, false);

        field.addValidator(validator1);
        field.addValidator(validator2);
        field.removeAllValidators();

        // all should succeed now
        field.setValue("a");
        field.setValue("ab");
        field.setValue("abcd");
    }

    @Test
    public void testEmailValidator() {
        field.addValidator(new LegacyEmailValidator("Invalid e-mail address"));

        // not required

        field.setRequired(false);
        // succeeds
        field.setValue("");
        // needed as required flag not checked by setValue()
        field.validate();
        // succeeds
        field.setValue(null);
        // needed as required flag not checked by setValue()
        field.validate();
        // succeeds
        field.setValue("test@example.com");
        // fails
        try {
            field.setValue("invalid e-mail");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }

        // required

        field.setRequired(true);
        // fails
        try {
            field.setValue("");
            // needed as required flag not checked by setValue()
            field.validate();
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
        // fails
        try {
            field.setValue(null);
            // needed as required flag not checked by setValue()
            field.validate();
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
        // succeeds
        field.setValue("test@example.com");
        // fails
        try {
            field.setValue("invalid e-mail");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
    }

    @Test
    public void testRegexpValidator() {
        field.addValidator(new LegacyRegexpValidator("pattern", true,
                "Validation failed"));
        field.setRequired(false);

        // succeeds
        field.setValue("");
        // needed as required flag not checked by setValue()
        field.validate();
        // succeeds
        field.setValue(null);
        // needed as required flag not checked by setValue()
        field.validate();
        // succeeds
        field.setValue("pattern");

        // fails
        try {
            field.setValue("mismatch");
            fail();
        } catch (InvalidValueException e) {
            // should fail
        }
    }

}
