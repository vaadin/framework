package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket2001 extends Application {

    @Override
    public void init() {
        final Window w = new Window(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new Label("row 1"));
        l.addComponent(new Label("row 2"));
        w.addComponent(l);

        final Button b = new Button("fixed width: 30px", false);
        b.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (b.booleanValue()) {
                    l.setWidth("30px");
                } else {
                    l.setWidth(null);
                }
            }
        });
        b.setImmediate(true);
        w.addComponent(b);

    }
}
