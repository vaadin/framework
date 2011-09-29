package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public class Ticket1519 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window("Test app to #1519");
        setMainWindow(mainWin);

        mainWin.setTheme("tests-tickets");
        TabSheet ts = new TabSheet();

        ts.addTab(new CustomLayout("Ticket1519_News"), "News", null);
        ts.addTab(new CustomLayout("Ticket1519_Support"), "Support", null);

        mainWin.addComponent(ts);

    }
}