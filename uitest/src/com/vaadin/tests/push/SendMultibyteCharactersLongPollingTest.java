package com.vaadin.tests.push;

public class SendMultibyteCharactersLongPollingTest extends
        SendMultibyteCharactersTest {

    @Override
    protected String getTransport() {
        return "long-polling";
    }
}
