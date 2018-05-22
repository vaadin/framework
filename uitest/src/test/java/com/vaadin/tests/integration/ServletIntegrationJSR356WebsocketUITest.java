package com.vaadin.tests.integration;

public class ServletIntegrationJSR356WebsocketUITest
        extends AbstractServletIntegrationTest {
    // Uses the test method declared in the super class

    @Override
    protected String getDeploymentPath(Class<?> uiClass) {
        return super.getDeploymentPath(uiClass).replace("/run/",
                "/run-jsr356/");
    }

    @Override
    protected Class<?> getUIClass() {
        return ServletIntegrationWebsocketUI.class;
    }
}
