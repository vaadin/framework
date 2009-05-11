package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;

public class Ticket2043 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Link l = new Link("IT Mill home (new 200x200 window, no decor, icon)",
                new ExternalResource("http://www.itmill.com"), "_blank", 200,
                200, Link.TARGET_BORDER_NONE);

        layout.addComponent(l);
    }
}
