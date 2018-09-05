package com.vaadin.tests.push;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class ReconnectStreamingTest extends ReconnectTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushStreaming.class;
    }

}
