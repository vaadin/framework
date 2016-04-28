package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class Ticket2425 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        w.addComponent(new Label("No scrollbars should be visible anywhere"));
        TabSheet ts = new TabSheet();
        ts.addTab(createPanel(), "Panel 1", null);
        ts.addTab(createPanel(), "Panel 2", null);

        w.addComponent(ts);
    }

    private Panel createPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        return new Panel(layout);
    }

}
