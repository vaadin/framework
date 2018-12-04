package com.vaadin.tests.applicationservlet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

public class SessionExpiration extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        getSession().getSession().setMaxInactiveInterval(2);
        addButton("Click to avoid expiration", event -> log("Clicked"));
    }

    @Override
    protected String getTestDescription() {
        return "Test for what happens when the session expires (2 second expiration time).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12139;
    }
}
