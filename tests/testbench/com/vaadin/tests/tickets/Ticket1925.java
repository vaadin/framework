package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Root;

public class Ticket1925 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root mainWindow = new Root("Test åäö");
        setMainWindow(mainWindow);

    }

}
