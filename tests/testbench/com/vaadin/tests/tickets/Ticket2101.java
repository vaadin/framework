package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Root;

public class Ticket2101 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getSimpleName());
        setMainWindow(w);

        Button b = new Button(
                "Button with a long text which will not fit on 50 pixels");
        b.setWidth("50px");

        w.getContent().addComponent(b);
    }

}
