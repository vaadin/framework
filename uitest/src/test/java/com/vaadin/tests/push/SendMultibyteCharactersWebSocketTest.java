package com.vaadin.tests.push;

public class SendMultibyteCharactersWebSocketTest
        extends SendMultibyteCharactersTest {

    @Override
    protected String getTransport() {
        return "websocket";
    }
}
