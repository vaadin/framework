package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.UI.LegacyWindow;

public class Ticket1925 extends Application.LegacyApplication {

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("Test åäö");
        setMainWindow(mainWindow);

    }

}
