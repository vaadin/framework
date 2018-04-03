package com.vaadin.tests.push;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

public class ExtremelyLongPushTimeWebsocketTest
        extends ExtremelyLongPushTimeTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }
}
