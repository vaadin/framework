package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2024 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(2, 2);
        layout.setHeight("100%");
        layout.setWidth("700");
        w.getContent().setSizeFull();
        w.getContent().setHeight("2000");
        w.addComponent(layout);

        layout.addComponent(new Label(
                "This should NOT get stuck when scrolling down"));
        layout.addComponent(new TextField("This should not get stuck either..."));

        VerticalLayout ol = new VerticalLayout();
        ol.setHeight("1000");
        ol.setWidth("200");
        w.addComponent(ol);
        ol.addComponent(new Label("Just a label to enable the scrollbar"));

    }
}
