package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2411 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        // VerticalLayout l = new VerticalLayout();
        GridLayout l = new GridLayout();
        w.setContent(l);

        l.setHeight("504px");

        for (int i = 1; i <= 5; i++) {
            Button b = new Button("Button " + i
                    + " should be 100px or 101px high");
            b.setHeight("100%");
            l.addComponent(b);
        }
    }

}
