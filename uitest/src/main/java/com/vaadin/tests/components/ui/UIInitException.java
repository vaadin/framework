package com.vaadin.tests.components.ui;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

public class UIInitException extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setErrorHandler(new ErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                addComponent(new Label("An exception occurred: "
                        + event.getThrowable().getMessage()));

            }
        });
        throw new RuntimeException("Catch me if you can");
    }

    @Override
    protected String getTestDescription() {
        return "Throwing an exception in application code during a browser details request should show a sensible message in the client";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8243);
    }

}
