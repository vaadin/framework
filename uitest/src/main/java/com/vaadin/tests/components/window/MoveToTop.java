package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Window;

public class MoveToTop extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window window = new Window("one");
        window.addStyleName("first-window");
        window.setWidth(200, Unit.PIXELS);
        window.setHeight(100, Unit.PIXELS);
        window.setPositionX(100);
        window.setPositionY(100);
        addWindow(window);

        window = new Window("two");
        window.setWidth(200, Unit.PIXELS);
        window.setHeight(100, Unit.PIXELS);
        window.setPositionX(150);
        window.setPositionY(150);
        window.addStyleName("second-window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Bring to front window on click it's header";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13445;
    }

}
