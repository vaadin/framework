package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class Ticket2271 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {

        VerticalLayout ol = new VerticalLayout();
        ol.setWidth(null);

        ComboBox cb = new ComboBox("Asiakas");
        cb.setWidth("100%");

        Button b = new Button("View CSV-tiedostoon");

        ol.addComponent(cb);
        ol.addComponent(b);

        layout.addComponent(ol);
    }
}
