package com.vaadin.tests.server.validation;

import org.junit.Test;

import com.vaadin.legacy.data.validator.LegacyIntegerValidator;
import com.vaadin.ui.TextField;

public class ReadOnlyValidationTest {

    @Test
    public void testIntegerValidation() {
        TextField field = new TextField();
        field.addValidator(new LegacyIntegerValidator("Enter a Valid Number"));
        field.setValue(String.valueOf(10));
        field.validate();
    }
}
