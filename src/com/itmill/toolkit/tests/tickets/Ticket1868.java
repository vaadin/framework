package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;

public class Ticket1868 extends com.itmill.toolkit.Application {

    public void init() {

        setMainWindow(new Window("#1868"));

        Panel p = new Panel(
                "This is a really long caption for the panel, too long in fact!");
        p.setWidth(300);
        p.setHeight(300);

        getMainWindow().addComponent(p);
    }
}