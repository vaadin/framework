package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;

public class Ticket2011 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-ticket");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Select s = new Select("Select");
        s.addItem("Item 1");
        s.addItem("Item 2");
        layout.addComponent(s);
    }
}
