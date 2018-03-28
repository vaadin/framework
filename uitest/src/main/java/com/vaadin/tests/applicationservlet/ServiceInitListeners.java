package com.vaadin.tests.applicationservlet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

public class ServiceInitListeners extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        log("Init count: " + TestingServiceInitListener.getInitCount());
        log("Request count: " + TestingServiceInitListener.getRequestCount());
        log("Connector id count: "
                + TestingServiceInitListener.getConnectorIdCount());
    }
}
