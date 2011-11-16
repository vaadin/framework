package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket1939 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.setWidth("400px");
        l.setHeight("100px");
        l.addComponent(new TextField("This one works fine"));
        TextField t = new TextField();
        t.setRequired(true);
        t.setValue("This one bugs");
        l.addComponent(t);
        w.addComponent(l);

        w.addComponent(new Button("show me the bug",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        l.setWidth(null);
                    }
                }));

    }

}
