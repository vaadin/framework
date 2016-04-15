package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.LegacyWindow;

public class Ticket2101 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        Button b = new Button(
                "Button with a long text which will not fit on 50 pixels");
        b.setWidth("50px");

        w.addComponent(b);
    }

}
