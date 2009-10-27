package com.vaadin.tests.tickets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket1919 extends com.vaadin.Application {

    private GridLayout lo;
    private boolean on = true;

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        main.setTheme("tests-tickets");

        lo = new GridLayout(2, 2);
        lo.setSizeFull();
        lo.setMargin(true);
        lo.setSpacing(true);

        lo.addComponent(getTestComponent());
        lo.addComponent(getTestComponent());
        lo.addComponent(getTestComponent());
        lo.addComponent(getTestComponent());

        main.setLayout(lo);

    }

    public void toggleStyleName() {
        if (on) {
            lo.setStyleName("test");
        } else {
            lo.setStyleName("");
        }
        on = !on;
    }

    private Component getTestComponent() {
        Panel p = new Panel();
        p.setSizeFull();

        Button b = new Button("toggle Values", this, "toggleStyleName");
        p.addComponent(b);
        return p;
    }
}