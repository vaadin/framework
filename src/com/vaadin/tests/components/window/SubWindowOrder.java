package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

public class SubWindowOrder extends TestBase {

    @Override
    protected void setup() {
        Window mainWindow = getMainWindow();
        for (int i = 1; i <= 10; i++) {
            Window dialog = new Window("Dialog " + i, new HorizontalLayout());
            mainWindow.addWindow(dialog);
        }
    }

    @Override
    protected String getDescription() {
        return "Subwindows should be rendered in the same order as they are added.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3363;
    }

}
