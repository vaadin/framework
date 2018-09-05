package com.vaadin.tests.applicationservlet;

import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.server.ConnectorIdGenerator;
import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinServiceInitListener;

public class TestingServiceInitListener implements VaadinServiceInitListener {

    private static AtomicInteger initCount = new AtomicInteger();
    private static AtomicInteger requestCount = new AtomicInteger();
    private static AtomicInteger connectorIdCount = new AtomicInteger();

    @Override
    public void serviceInit(ServiceInitEvent event) {
        initCount.incrementAndGet();

        event.addRequestHandler((session, request, response) -> {
            requestCount.incrementAndGet();
            return false;
        });

        event.addConnectorIdGenerator(connectorIdGenerationEvent -> {
            connectorIdCount.incrementAndGet();
            return ConnectorIdGenerator
                    .generateDefaultConnectorId(connectorIdGenerationEvent);
        });
    }

    public static int getInitCount() {
        return initCount.get();
    }

    public static int getRequestCount() {
        return requestCount.get();
    }

    public static int getConnectorIdCount() {
        return connectorIdCount.get();
    }

}
