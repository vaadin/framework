package com.vaadin.tests.push;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class SendMultibyteCharactersWebSocketTest extends
        SendMultibyteCharactersTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }

    @Override
    protected String getTransport() {
        return "websocket";
    }
}
