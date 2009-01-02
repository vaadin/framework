package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2296 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        CustomLayout cl = new CustomLayout("Ticket2296");
        cl.setSizeFull();
        Button b = new Button("100%x100% button");
        b.setSizeFull();
        cl.addComponent(b, "button1");

        b = new Button("100%x100% button");
        b.setSizeFull();
        cl.addComponent(b, "button2");

        b = new Button("50%x50% button");
        b.setWidth("50%");
        b.setHeight("50%");
        cl.addComponent(b, "button3");

        w.setLayout(cl);
    }

}
