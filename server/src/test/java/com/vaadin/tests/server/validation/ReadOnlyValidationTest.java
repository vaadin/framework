package com.vaadin.tests.server.validation;

import org.junit.Test;

import com.vaadin.legacy.data.validator.LegacyIntegerValidator;
import com.vaadin.legacy.ui.LegacyTextField;

public class ReadOnlyValidationTest {

    @Test
    public void testIntegerValidation() {
        LegacyTextField field = new LegacyTextField();
        field.addValidator(new LegacyIntegerValidator("Enter a Valid Number"));
        field.setValue(String.valueOf(10));
        field.validate();
    }
}
