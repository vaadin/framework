package com.vaadin.tests.push;

public class ReconnectWebsocketTest extends ReconnectTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushWebsocket.class;
    }

}
