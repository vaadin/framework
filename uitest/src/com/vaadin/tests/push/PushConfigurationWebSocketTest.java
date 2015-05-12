/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.push;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PushConfigurationWebSocketTest extends PushConfigurationTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }

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
        assertThat(
                driver.getPageSource(),
                not(containsString("Push connection established using streaming")));
    }
}
