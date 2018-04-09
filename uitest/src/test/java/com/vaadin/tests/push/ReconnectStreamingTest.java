package com.vaadin.tests.push;

import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

public class ReconnectStreamingTest extends ReconnectTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {

        // PhantomJS doesn't seem to detect disconnection on
        // Long-Polling/Streaming:
        // https://github.com/ariya/phantomjs/issues/11938
        return getBrowsersExcludingPhantomJS();
    }

    @Override
    protected Class<?> getUIClass() {
        return BasicPushStreaming.class;
    }

}
