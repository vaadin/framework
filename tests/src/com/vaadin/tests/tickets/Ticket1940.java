package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1940 extends Application {

    @Override
    public void init() {
        final Window w = new Window(getClass().getName());
        setMainWindow(w);

        final OrderedLayout l = new OrderedLayout();
        l.setWidth(200);
        l.setHeight(-1);
        TextField t = new TextField();
        l.addComponent(t);
        t.setRequired(true);
        w.addComponent(l);

    }

}
