package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2296 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
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

        w.setContent(cl);
    }

}
