package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1919 extends com.itmill.toolkit.Application {

    private GridLayout lo;
    private boolean on = true;

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