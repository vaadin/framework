package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.LegacyTextField;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket1939 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.setWidth("400px");
        l.setHeight("100px");
        l.addComponent(new LegacyTextField("This one works fine"));
        LegacyTextField t = new LegacyTextField();
        t.setRequired(true);
        t.setValue("This one bugs");
        l.addComponent(t);
        w.addComponent(l);

        w.addComponent(
                new Button("show me the bug", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        l.setWidth(null);
                    }
                }));

    }

}
