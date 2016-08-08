package com.vaadin.tests.validation;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.data.validator.LegacyIntegerValidator;
import com.vaadin.legacy.data.validator.LegacyStringLengthValidator;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;

@SuppressWarnings("deprecation")
public class ValidationOfRequiredEmptyFields extends AbstractTestUI {

    private LegacyTextField tf;
    private CheckBox requiredInput;
    private LegacyTextField requiredErrorInput;

    private Validator integerValidator = new LegacyIntegerValidator(
            "Must be an integer");
    private Validator stringLengthValidator = new LegacyStringLengthValidator(
            "Must be 5-10 chars", 5, 10, false);
    private CheckBox integerValidatorInput;
    private CheckBox stringLengthValidatorInput;

    @Override
    protected void setup(VaadinRequest request) {
        requiredInput = new CheckBox("Field required");
        requiredInput.setImmediate(true);
        requiredInput.addValueChangeListener(
                event -> tf.setRequired(requiredInput.getValue()));

        requiredErrorInput = new LegacyTextField("Required error message");
        requiredErrorInput.setImmediate(true);
        requiredErrorInput.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setRequiredError(requiredErrorInput.getValue());
            }
        });

        integerValidatorInput = new CheckBox("Integer validator");
        integerValidatorInput.setImmediate(true);
        integerValidatorInput.addValueChangeListener(event -> {
            if (integerValidatorInput.getValue()) {
                tf.addValidator(integerValidator);
            } else {
                tf.removeValidator(integerValidator);
            }
        });
        stringLengthValidatorInput = new CheckBox("String length validator");
        stringLengthValidatorInput.setImmediate(true);
        stringLengthValidatorInput.addValueChangeListener(event -> {
            if (stringLengthValidatorInput.getValue()) {
                tf.addValidator(stringLengthValidator);
            } else {
                tf.removeValidator(stringLengthValidator);
            }
        });

        tf = new LegacyTextField();
        tf.setImmediate(true);

        requiredInput.setValue(false);
        requiredErrorInput.setValue("");
        integerValidatorInput.setValue(false);
        stringLengthValidatorInput.setValue(false);

        addComponent(requiredInput);
        addComponent(requiredErrorInput);
        addComponent(integerValidatorInput);
        addComponent(stringLengthValidatorInput);
        addComponent(tf);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that the lower textfield's tooltip displays validation error messages correctly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3851;
    }

}
