package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Window;

public class Ticket1519 extends Application {

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