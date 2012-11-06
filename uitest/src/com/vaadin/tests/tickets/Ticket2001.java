package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2001 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);

        final VerticalLayout l = new VerticalLayout();
        l.addComponent(new Label("row 1"));
        l.addComponent(new Label("row 2"));
        w.addComponent(l);

        final CheckBox b = new CheckBox("fixed width: 30px", false);
        b.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (b.getValue()) {
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
