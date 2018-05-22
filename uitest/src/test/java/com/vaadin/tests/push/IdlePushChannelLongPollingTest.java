package com.vaadin.tests.push;

public class IdlePushChannelLongPollingTest extends IdlePushChannelTest {
    @Override
    protected Class<?> getUIClass() {
        return BasicPushLongPolling.class;
    }
}
