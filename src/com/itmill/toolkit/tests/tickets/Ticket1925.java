package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Window;

public class Ticket1925 extends Application {

    public void init() {
        Window mainWindow = new Window("Test åäö");
        setMainWindow(mainWindow);

    }

}
