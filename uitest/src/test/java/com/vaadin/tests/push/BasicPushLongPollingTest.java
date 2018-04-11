package com.vaadin.tests.push;

import org.junit.Test;

public class BasicPushLongPollingTest extends BasicPushTest {

    @Test
    public void pushAfterServerTimeout() throws InterruptedException {
        getDriver().get(getTestUrl().replace("/run/", "/run-push-timeout/")
                + "?debug=push");
        sleep(11000); // Wait for server timeout (10s)

        getServerCounterStartButton().click();
        waitUntilServerCounterChanges();
    }

}
