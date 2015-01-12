package com.vaadin.tests.push;

public class SendMultibyteCharactersStreamingTest extends
        SendMultibyteCharactersTest {

    @Override
    protected String getTransport() {
        return "streaming";
    }
}
