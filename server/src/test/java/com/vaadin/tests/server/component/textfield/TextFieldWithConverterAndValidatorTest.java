package com.vaadin.tests.server.component.textfield;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.RangeValidator;
import com.vaadin.tests.data.converter.ConverterFactoryTest.ConvertTo42;
import com.vaadin.ui.TextField;

public class TextFieldWithConverterAndValidatorTest {

    private TextField field;
    private ObjectProperty<Integer> property;

    @Before
    public void setUp() {
        field = new TextField();
        field.setInvalidAllowed(false);
    }

    @Test
    public void testConvert42AndValidator() {
        property = new ObjectProperty<Integer>(123);
        field.setConverter(new ConvertTo42());
        field.setPropertyDataSource(property);

        field.addValidator(new RangeValidator<Integer>("Incorrect value",
                Integer.class, 42, 42));

        // succeeds
        field.setValue("a");
        // succeeds
        field.setValue("42");
        // succeeds - no validation
        property.setValue(42);

        // nulls

        // succeeds - validate() converts field value back to property type
        // before validation
        property.setValue(null);
        field.validate();
        // succeeds
        field.setValue(null);
    }

    // TODO test converter changing value to null with validator
}
