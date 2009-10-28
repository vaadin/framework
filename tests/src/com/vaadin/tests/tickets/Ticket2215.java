package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket2215 extends Application {

    @Override
    public void init() {
        setMainWindow(new Window());

        OrderedLayout ol = new OrderedLayout();
        Panel p = new Panel("Test");
        p.addComponent(new Label("Panel1"));
        p.setHeight("500px");
        p.setWidth("500px");
        p.setStyleName(Panel.STYLE_LIGHT);
        ol.addComponent(p);
        ol.addComponent(new Label("NextComponent"));

        getMainWindow().addComponent(ol);

    }

}
