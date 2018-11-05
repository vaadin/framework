package com.vaadin.tests.push;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class PushConfigurationWebSocketTest extends PushConfigurationTest {

    @Test
    public void testWebsocket() throws InterruptedException {
        getTransportSelect().selectByText("Websocket");
        getPushModeSelect().selectByText("Automatic");

        assertThat(getStatusText(),
                containsString("fallbackTransport: long-polling"));
        assertThat(getStatusText(), containsString("transport: websocket"));

        waitForServerCounterToUpdate();

        // Use debug console to verify we used the correct transport type
        assertThat(driver.getPageSource(),
                containsString("Push connection established using websocket"));
        assertThat(driver.getPageSource(), not(
                containsString("Push connection established using streaming")));
    }
}
