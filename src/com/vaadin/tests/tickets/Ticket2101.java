package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

public class Ticket2101 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        Button b = new Button(
                "Button with a long text which will not fit on 50 pixels");
        b.setWidth("50px");

        w.getLayout().addComponent(b);
    }

}
