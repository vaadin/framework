package com.vaadin.tests.integration;


public class IntegrationTestWebsocketTB3 extends IntegrationTestXhrTB3 {

    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.integration.IntegrationTestStreaming.class;
    }
}
