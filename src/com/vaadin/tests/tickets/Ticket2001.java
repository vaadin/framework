package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;

public class Ticket2001 extends Application {

    @Override
    public void init() {
        final Window w = new Window(getClass().getName());
        setMainWindow(w);

        final OrderedLayout l = new OrderedLayout();
        l.addComponent(new Label("row 1"));
        l.addComponent(new Label("row 2"));
        w.addComponent(l);

        final Button b = new Button("fixed width: 30px", false);
        b.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                l.setWidth(b.booleanValue() ? 30 : -1);
            }
        });
        b.setImmediate(true);
        w.addComponent(b);

    }
}
