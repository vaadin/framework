package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root.LegacyWindow;

public class Ticket2083 extends Application.LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Panel p = new Panel(
                "This is a panel with a longer caption than it should have");
        p.setWidth("100px");
        p.getContent().addComponent(new Label("Contents"));
        layout.addComponent(p);
    }
}
