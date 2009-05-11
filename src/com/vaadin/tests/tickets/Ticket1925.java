package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Window;

public class Ticket1925 extends Application {

    @Override
    public void init() {
        Window mainWindow = new Window("Test åäö");
        setMainWindow(mainWindow);

    }

}
