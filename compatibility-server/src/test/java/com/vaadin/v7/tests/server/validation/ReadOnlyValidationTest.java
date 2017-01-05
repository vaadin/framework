package com.vaadin.v7.tests.server.validation;

import org.junit.Test;

import com.vaadin.v7.data.validator.IntegerValidator;
import com.vaadin.v7.ui.TextField;

public class ReadOnlyValidationTest {

    @Test
    public void testIntegerValidation() {
        TextField field = new TextField();
        field.addValidator(new IntegerValidator("Enter a Valid Number"));
        field.setValue(String.valueOf(10));
        field.validate();
    }
}
