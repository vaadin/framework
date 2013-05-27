package com.vaadin.tests.tb3;

public class PushIntegrationTest extends IntegrationTest {
    @Override
    protected String getPath() {
        return super.getPath().replace("/run/", "/run-push/");
    }
}
