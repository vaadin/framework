package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket1940 extends Application {

    @Override
    public void init() {
        final Window w = new Window(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.setWidth("200px");
        l.setHeight(null);
        TextField t = new TextField();
        l.addComponent(t);
        t.setRequired(true);
        w.addComponent(l);

    }

}
