package com.vaadin.tests.push;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class ReconnectLongPollingTest extends ReconnectTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushLongPolling.class;
    }

}
