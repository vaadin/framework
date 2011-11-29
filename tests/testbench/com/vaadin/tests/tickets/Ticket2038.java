package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

public class Ticket2038 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root w = new Root("Testing for #2038");
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

        final CheckBox b = new CheckBox(
                "Field should use error message. (!) should be shown when invalid.",
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
