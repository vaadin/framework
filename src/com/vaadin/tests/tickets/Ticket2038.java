package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class Ticket2038 extends Application {

    @Override
    public void init() {
        final Window w = new Window("Testing for #2038");
        setMainWindow(w);

        final TextField tf = new TextField(
                "Test-field, enter someting and click outside the field to activate");
        tf.setRequired(true);
        tf.setImmediate(true);
        tf.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                w.showNotification("TextField is " + (tf.isValid() ? "" : "in")
                        + "valid, with error: " + tf.getErrorMessage(),
                        Notification.TYPE_WARNING_MESSAGE);
            }
        });
        w.addComponent(tf);

        final Button b = new Button(
                "Field should use error message. (!) should be shown when invalid.",
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
