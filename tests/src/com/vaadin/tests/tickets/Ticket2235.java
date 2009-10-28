package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2235 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        layout.setSizeFull();

        TextField tf = new TextField();
        tf.setCaption("A text field");
        tf.setSizeFull();
        tf.setRows(2);

        layout.addComponent(tf);
    }
}
