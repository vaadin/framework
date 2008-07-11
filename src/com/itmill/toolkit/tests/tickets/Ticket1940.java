package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket1940 extends Application {

    public void init() {
        final Window w = new Window(getClass().getName());
        setMainWindow(w);

        final OrderedLayout l = new OrderedLayout();
        l.setWidth(200);
        l.setHeight(-1);
        TextField t = new TextField();
        l.addComponent(t);
        t.setRequired(true);
        w.addComponent(l);

    }

}
