package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

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
