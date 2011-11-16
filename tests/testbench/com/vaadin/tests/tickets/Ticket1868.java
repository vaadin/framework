package com.vaadin.tests.tickets;

import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;

public class Ticket1868 extends com.vaadin.Application.LegacyApplication {

    @Override
    public void init() {

        setMainWindow(new Root("#1868"));

        Panel p = new Panel(
                "This is a really long caption for the panel, too long in fact!");
        p.setWidth("300px");
        p.setHeight("300px");

        getMainWindow().addComponent(p);
    }
}