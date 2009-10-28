package com.vaadin.tests.tickets;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket1868 extends com.vaadin.Application {

    @Override
    public void init() {

        setMainWindow(new Window("#1868"));

        Panel p = new Panel(
                "This is a really long caption for the panel, too long in fact!");
        p.setWidth(300);
        p.setHeight(300);

        getMainWindow().addComponent(p);
    }
}