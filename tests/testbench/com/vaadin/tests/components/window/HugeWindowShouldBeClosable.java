package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Window;

public class HugeWindowShouldBeClosable extends TestBase {

    @Override
    protected void setup() {
        Window w = new Window("Hueg");
        w.setWidth("2000px");
        w.setHeight("2000px");
        w.setPositionY(500);
        getMainWindow().addWindow(w);
    }

    @Override
    protected String getDescription() {
        return "Huge windows should be closable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6784;
    }

}
