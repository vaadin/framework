package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket2083 extends Application {

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
        Panel p = new Panel(
                "This is a panel with a longer caption than it should have");
        p.setWidth("100px");
        p.getLayout().addComponent(new Label("Contents"));
        layout.addComponent(p);
    }
}
