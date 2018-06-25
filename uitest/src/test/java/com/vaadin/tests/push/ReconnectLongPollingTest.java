package com.vaadin.tests.push;

public class ReconnectLongPollingTest extends ReconnectTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushLongPolling.class;
    }

}
