package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowControlButtonFocus extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Window window = new Window("Window", new Label());
        window.center();
        addWindow(window);
    }

    @Override
    protected Integer getTicketNumber() {
        return 8918;
    }

    @Override
    protected String getTestDescription() {
        return "Window control buttons should have noticeable focus styles.";
    }
}
