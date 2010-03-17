package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Window;

public class PositionedSubWindows extends TestBase {

    @Override
    protected String getDescription() {
        return "Subwindows should obey setPositionX/Y methods also if only one is called";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4362;
    }

    @Override
    protected void setup() {
        Window smallWindow = getSmallWindow("Top:200");

        smallWindow.setPositionY(200);
        getMainWindow().addWindow(smallWindow);
        smallWindow = getSmallWindow("Left:200");
        smallWindow.setPositionX(200);
        getMainWindow().addWindow(smallWindow);

        smallWindow = getSmallWindow("50/50");
        smallWindow.setPositionX(50);
        smallWindow.setPositionY(50);
        getMainWindow().addWindow(smallWindow);

    }

    private Window getSmallWindow(String caption) {
        Window window2 = new Window(caption);
        window2.setWidth("100px");
        window2.setHeight("50px");
        return window2;
    }
}
