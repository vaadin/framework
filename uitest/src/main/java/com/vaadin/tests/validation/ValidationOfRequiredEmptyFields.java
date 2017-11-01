package com.vaadin.tests.validation;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.IntegerValidator;
import com.vaadin.v7.data.validator.StringLengthValidator;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("deprecation")
public class ValidationOfRequiredEmptyFields extends AbstractReindeerTestUI {

    private TextField tf;
    private CheckBox requiredInput;
    private TextField requiredErrorInput;

    private Validator integerValidator = new IntegerValidator(
            "Must be an integer");
    private Validator stringLengthValidator = new StringLengthValidator(
            "Must be 5-10 chars", 5, 10, false);
    private CheckBox integerValidatorInput;
    private CheckBox stringLengthValidatorInput;

    @Override
    protected void setup(VaadinRequest request) {
        requiredInput = new CheckBox("Field required");
        requiredInput.addValueChangeListener(
                event -> tf.setRequired(requiredInput.getValue()));

        requiredErrorInput = new TextField("Required error message");
        requiredErrorInput.setImmediate(true);
        requiredErrorInput.addValueChangeListener(
                event -> tf.setRequiredError(requiredErrorInput.getValue()));

        integerValidatorInput = new CheckBox("Integer validator");
        integerValidatorInput.addValueChangeListener(event -> {
            if (integerValidatorInput.getValue()) {
                tf.addValidator(integerValidator);
            } else {
                tf.removeValidator(integerValidator);
            }
        });
        stringLengthValidatorInput = new CheckBox("String length validator");
        stringLengthValidatorInput.addValueChangeListener(event -> {
            if (stringLengthValidatorInput.getValue()) {
                tf.addValidator(stringLengthValidator);
            } else {
                tf.removeValidator(stringLengthValidator);
            }
        });

        tf = new TextField();
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
