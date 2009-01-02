package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2415 extends Application {

    @Override
    public void init() {
        final Window main = new Window("");
        setMainWindow(main);

        final TextField tf = new TextField("Try to change me");
        main.addComponent(tf);

        tf.setImmediate(true);
        tf.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                main.showNotification("New value = " + tf);
            }
        });

    }

}
