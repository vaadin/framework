package com.vaadin.tests.application;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class TerminalErrorNotification extends TestBase {

    @Override
    protected void setup() {
        Button button = new Button("Throw exception", event -> {
            throw new RuntimeException("You asked for it");
        });

        addComponent(button);
    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        event.getThrowable().printStackTrace();

        UI mainWindow = getMainWindow();
        if (mainWindow != null) {
            Throwable throwable = event.getThrowable();

            // Find the root cause
            while (throwable.getCause() != null) {
                throwable = throwable.getCause();
            }

            Notification.show("Got an exception: " + throwable.getMessage(),
                    Notification.TYPE_ERROR_MESSAGE);
        } else {
            System.out.println("No main window found");
        }
    }

    @Override
    protected String getDescription() {
        return "Showing a notification in the terminalError method should make the notification appear in the browser.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8778);
    }

}
