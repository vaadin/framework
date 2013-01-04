package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowWithLabel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setContent(new Label("UI"));
        Window window = new Window("A window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Resize the window. It should work.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10375;
    }

}
