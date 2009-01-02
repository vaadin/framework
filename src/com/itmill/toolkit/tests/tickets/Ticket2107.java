package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Window.Notification;

public class Ticket2107 extends Application {

    @Override
    public void init() {
        final Window w = new Window("Testing for #2107");
        setMainWindow(w);

        final TextField tf = new TextField(
                "Required field that validated the input");
        tf
                .setDescription("Enter someting and click outside the field to activate");
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

        final Button b = new Button(
                "Field should use error message. (!) should be shown when empty.",
                false);
        w.addComponent(b);
        b.setImmediate(true);
        b.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                tf
                        .setRequiredError(b.booleanValue() ? "Field must not be empty"
                                : null);
            }
        });
    }

}
