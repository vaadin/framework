package com.vaadin.tests.tb3;

public class IntegrationTestWebsocket extends IntegrationTestXHR {

    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.integration.IntegrationTestStreaming.class;
    }
}
