package com.vaadin.tests.push;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.WebsocketTest;

public class ExtremelyLongPushTimeWebsocketTest
        extends ExtremelyLongPushTimeTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }
}
