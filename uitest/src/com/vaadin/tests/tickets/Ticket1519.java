package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;

public class Ticket1519 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app to #1519");
        setMainWindow(mainWin);

        setTheme("tests-tickets");
        TabSheet ts = new TabSheet();

        ts.addTab(new CustomLayout("Ticket1519_News"), "News", null);
        ts.addTab(new CustomLayout("Ticket1519_Support"), "Support", null);

        mainWin.addComponent(ts);

    }
}
