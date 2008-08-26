package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket1972 extends Application {

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        setTheme("tests-ticket");
        GridLayout layout = new GridLayout(3, 3);
        layout.setStyleName("borders");
        layout.addComponent(new Label("1-1"));
        layout.space();
        layout.space();
        layout.addComponent(new Label("2-1"));
        layout.space();
        layout.space();
        layout.addComponent(new Label("3-1"));
        layout.space();
        layout.addComponent(new Label("3-3"));

        w.setLayout(layout);
    }

}
