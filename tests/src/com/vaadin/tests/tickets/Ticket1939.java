package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class Ticket1939 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);

        final OrderedLayout l = new OrderedLayout();
        l.setWidth(400);
        l.setHeight(100);
        l.addComponent(new TextField("This one works fine"));
        TextField t = new TextField();
        t.setRequired(true);
        t.setValue("This one bugs");
        l.addComponent(t);
        w.addComponent(l);

        w.addComponent(new Button("show me the bug",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        l.setWidth(-1);
                    }
                }));

    }

}
