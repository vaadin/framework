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
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

public class PushConfigurationLongPollingTest extends PushConfigurationTest {

    @Test
    public void testLongPolling() throws InterruptedException {
        openDebugLogTab();

        new Select(getTransportSelect()).selectByVisibleText("LONG_POLLING");
        assertThat(getStatusText(),
                containsString("fallbackTransport: long-polling"));
        assertThat(getStatusText(), containsString("transport: long-polling"));

        clearDebugMessages();
        new Select(getPushModeSelect()).selectByVisibleText("AUTOMATIC");
        waitForDebugMessage("Push connection established using long-polling",
                10);
        waitForServerCounterToUpdate();
    }

}
