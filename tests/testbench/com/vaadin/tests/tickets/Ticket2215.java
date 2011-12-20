package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class Ticket2215 extends Application.LegacyApplication {

    @Override
    public void init() {
        setMainWindow(new LegacyWindow());

        VerticalLayout ol = new VerticalLayout();
        Panel p = new Panel("Test");
        p.addComponent(new Label("Panel1"));
        p.setHeight("500px");
        p.setWidth("500px");
        p.setStyleName(Reindeer.PANEL_LIGHT);
        ol.addComponent(p);
        ol.addComponent(new Label("NextComponent"));

        getMainWindow().addComponent(ol);

    }

}
