package com.vaadin.tests.push;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

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
