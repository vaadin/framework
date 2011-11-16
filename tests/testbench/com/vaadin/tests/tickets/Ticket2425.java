package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.TabSheet;

public class Ticket2425 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getSimpleName());
        setMainWindow(w);

        w.addComponent(new Label("No scrollbars should be visible anywhere"));
        TabSheet ts = new TabSheet();
        ts.addTab(new Panel(), "Panel 1", null);
        ts.addTab(new Panel(), "Panel 2", null);

        w.addComponent(ts);
    }

}
