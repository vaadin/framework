package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Window;

public class WindowWithInvalidCloseListener extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window w = new Window("Close me");
        w.addCloseListener(event -> {
            throw new RuntimeException("Close listener intentionally failed");
        });
        addWindow(w);
    }

    @Override
    protected String getTestDescription() {
        return "The window has a close listener which throws an exception. This should not prevent the window from being closed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10779;
    }

}
