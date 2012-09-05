package com.vaadin.tests.validation;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class ValidationOfRequiredEmptyFields extends TestBase {

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
    protected void setup() {
        requiredInput = new CheckBox("Field required");
        requiredInput.setImmediate(true);
        requiredInput.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setRequired(requiredInput.getValue());
            }
        });

        requiredErrorInput = new TextField("Required error message");
        requiredErrorInput.setImmediate(true);
        requiredErrorInput.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setRequiredError(requiredErrorInput.getValue());
            }
        });

        integerValidatorInput = new CheckBox("Integer.parseInt validator");
        integerValidatorInput.setImmediate(true);
        integerValidatorInput.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (integerValidatorInput.getValue()) {
                    tf.addValidator(integerValidator);
                } else {
                    tf.removeValidator(integerValidator);
                }
            }
        });
        stringLengthValidatorInput = new CheckBox(
                "stringLength.parseInt validator");
        stringLengthValidatorInput.setImmediate(true);
        stringLengthValidatorInput.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (stringLengthValidatorInput.getValue()) {
                    tf.addValidator(stringLengthValidator);
                } else {
                    tf.removeValidator(stringLengthValidator);
                }
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
    protected String getDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 3851;
    }

}
