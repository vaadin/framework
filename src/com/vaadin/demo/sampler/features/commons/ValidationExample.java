package com.vaadin.demo.sampler.features.commons;

import java.util.HashSet;

import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.CompositeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ValidationExample extends VerticalLayout {

    HashSet usernames = new HashSet();

    public ValidationExample() {
        setSpacing(true);

        TextField pin = new TextField("PIN");
        pin.setWidth("50px");
        // optional; validate at once instead of when clicking 'save' (e.g)
        pin.setImmediate(true);
        addComponent(pin);
        // add the validator
        pin.addValidator(new StringLengthValidator("Must be 4-6 characters", 4,
                6, false));

        TextField username = new TextField("Username");
        // optional; validate at once instead of when clicking 'save' (e.g)
        username.setImmediate(true);
        addComponent(username);
        CompositeValidator usernameValidator = new CompositeValidator();
        username.addValidator(usernameValidator);
        usernameValidator.addValidator(new StringLengthValidator(
                "Username must be at least 4 characters", 4, 255, false));
        usernameValidator.addValidator(new Validator() {

            public boolean isValid(Object value) {
                return !usernames.contains(value);
            }

            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new Validator.InvalidValueException("Username "
                            + value + " already in use");
                }
            }
        });
        username.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                TextField tf = (TextField) event.getProperty();
                tf.validate();
                usernames.add(tf.getValue());
                addComponent(new Label("Added " + tf.getValue()
                        + " to usernames"));
            }
        });

    }
}
