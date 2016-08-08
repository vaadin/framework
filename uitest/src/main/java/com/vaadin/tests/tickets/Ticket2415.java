package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;

public class Ticket2415 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow("");
        setMainWindow(main);

        final LegacyTextField tf = new LegacyTextField("Try to change me");
        main.addComponent(tf);

        tf.setImmediate(true);
        tf.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                main.showNotification("New value = " + tf);
            }
        });

        final LegacyTextField tf2 = new LegacyTextField("Try to change me");
        main.addComponent(tf2);

        tf2.setImmediate(true);
        tf2.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                main.showNotification("New value = " + tf2);
            }
        });

    }

}
