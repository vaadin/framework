package com.vaadin.tests.push;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class PushConfigurationStreamingTest extends PushConfigurationTest {

    @Test
    public void testStreaming() throws InterruptedException {
        openDebugLogTab();

        getTransportSelect().selectByText("Streaming");
        assertThat(getStatusText(),
                containsString("fallbackTransport: long-polling"));
        assertThat(getStatusText(), containsString("transport: streaming"));

        clearDebugMessages();
        getPushModeSelect().selectByText("Automatic");

        waitForDebugMessage("Push connection established using streaming", 10);
        waitForServerCounterToUpdate();
    }
}
