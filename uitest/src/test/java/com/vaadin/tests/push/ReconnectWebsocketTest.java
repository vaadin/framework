package com.vaadin.tests.push;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.WebsocketTest;

public class ReconnectWebsocketTest extends ReconnectTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }

    @Override
    protected Class<?> getUIClass() {
        return BasicPushWebsocket.class;
    }

}
