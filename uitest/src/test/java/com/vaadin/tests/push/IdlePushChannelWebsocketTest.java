package com.vaadin.tests.push;

public class IdlePushChannelWebsocketTest extends IdlePushChannelTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushWebsocket.class;
    }
}
