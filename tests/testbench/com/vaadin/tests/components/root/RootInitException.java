package com.vaadin.tests.components.root;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;

public class RootInitException extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
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
