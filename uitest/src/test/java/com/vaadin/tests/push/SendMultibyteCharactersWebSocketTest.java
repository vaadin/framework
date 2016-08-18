package com.vaadin.tests.push;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

public class SendMultibyteCharactersWebSocketTest
        extends SendMultibyteCharactersTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }

    @Override
    protected String getTransport() {
        return "websocket";
    }
}
