package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.LegacyWindow;

public class Ticket1925 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Test åäö");
        setMainWindow(mainWindow);

    }

}
