package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2026 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);

        @SuppressWarnings("unused")
        int nr = 5;
        TextField tf;
        tf = new TextField("TextField (tabIndex 1)");
        tf.setTabIndex(1);
        tf.focus();
        layout.addComponent(tf);
        layout.addComponent(new TextField("TextField without tab index"));
        layout.addComponent(new TextField("TextField without tab index"));
        layout.addComponent(new TextField("TextField without tab index"));
        layout.addComponent(new TextField("TextField without tab index"));
        tf = new TextField("TextField (tabIndex 2)");
        tf.setTabIndex(2);
        layout.addComponent(tf);

        w.setLayout(layout);
    }
}
