package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class Ticket2001 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root w = new Root(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new Label("row 1"));
        l.addComponent(new Label("row 2"));
        w.addComponent(l);

        final CheckBox b = new CheckBox("fixed width: 30px", false);
        b.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if ((Boolean) b.getValue()) {
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
