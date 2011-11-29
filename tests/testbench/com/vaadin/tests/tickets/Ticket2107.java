package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class Ticket2107 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root w = new Root("Testing for #2107");
        setMainWindow(w);

        final TextField tf = new TextField(
                "Required field that validated the input");
        tf.setDescription("Enter someting and click outside the field to activate");
        tf.setRequired(true);
        tf.setImmediate(true);
        tf.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                w.showNotification("TextField is " + (tf.isValid() ? "" : "in")
                        + "valid, with error: " + tf.getErrorMessage(),
                        Notification.TYPE_WARNING_MESSAGE);
            }
        });
        tf.addValidator(new Validator() {

            public boolean isValid(Object value) {
                return value != null && value.toString().length() > 3;
            }

            public void validate(Object value) throws InvalidValueException {
                if (!isValid(value)) {
                    throw new InvalidValueException(
                            "Text length must exceed 3 characters");
                }
            }
        });
        w.addComponent(tf);

        final CheckBox b = new CheckBox(
                "Field should use error message. (!) should be shown when empty.",
                false);
        w.addComponent(b);
        b.setImmediate(true);
        b.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                tf.setRequiredError((Boolean) b.getValue() ? "Field must not be empty"
                        : null);
            }
        });
    }

}
