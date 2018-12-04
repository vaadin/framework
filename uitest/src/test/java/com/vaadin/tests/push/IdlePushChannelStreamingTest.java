package com.vaadin.tests.push;

public class IdlePushChannelStreamingTest extends IdlePushChannelTest {
    @Override
    protected Class<?> getUIClass() {
        return BasicPushStreaming.class;
    }
}
