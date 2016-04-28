package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

public class Ticket2271 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
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
