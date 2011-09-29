package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public class Ticket2425 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        w.addComponent(new Label("No scrollbars should be visible anywhere"));
        TabSheet ts = new TabSheet();
        ts.addTab(new Panel(), "Panel 1", null);
        ts.addTab(new Panel(), "Panel 2", null);

        w.addComponent(ts);
    }

}
