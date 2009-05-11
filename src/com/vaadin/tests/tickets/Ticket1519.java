package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public class Ticket1519 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window("Test app to #1368");
        setMainWindow(mainWin);

        mainWin.setTheme("example");
        TabSheet ts = new TabSheet();

        ts.addTab(new CustomLayout("News"), "News", null);
        ts.addTab(new CustomLayout("Support"), "Support", null);

        mainWin.addComponent(ts);

    }
}