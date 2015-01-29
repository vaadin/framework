package com.vaadin.tests.validation;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

@SuppressWarnings("deprecation")
public class ValidationOfRequiredEmptyFields extends AbstractTestUI {

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
        requiredInput.setImmediate(true);
        requiredInput.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setRequired(requiredInput.getValue());
            }
        });

        requiredErrorInput = new TextField("Required error message");
        requiredErrorInput.setImmediate(true);
        requiredErrorInput.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tf.setRequiredError(requiredErrorInput.getValue());
            }
        });

        integerValidatorInput = new CheckBox("Integer validator");
        integerValidatorInput.setImmediate(true);
        integerValidatorInput.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (integerValidatorInput.getValue()) {
                    tf.addValidator(integerValidator);
                } else {
                    tf.removeValidator(integerValidator);
                }
            }
        });
        stringLengthValidatorInput = new CheckBox("String length validator");
        stringLengthValidatorInput.setImmediate(true);
        stringLengthValidatorInput
                .addValueChangeListener(new ValueChangeListener() {

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
    protected String getTestDescription() {
        return "Tests that the lower textfield's tooltip displays validation error messages correctly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3851;
    }

}
