/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.tests.tb3.WebsocketTest;

public class PushLargeDataStreamingTest extends WebsocketTest {

    @Test
    public void testWebsocketLargeData() {
        openTestURL();

        // Without this there is a large chance that we will wait for all pushes
        // to complete before moving on
        testBench(driver).disableWaitForVaadin();

        push();
        // Push complete. Browser will reconnect now as > 10MB has been sent
        // Push again to ensure push still works
        push();

    }

    private void push() {
        String logRow0Id = "Log_row_0";
        By logRow0 = vaadinLocatorById(logRow0Id);

        vaadinElementById("startButton").click();
        waitUntil(ExpectedConditions.not(ExpectedConditions
                .textToBePresentInElement(logRow0, "Push complete")));

        // Pushes each 2000ms for 40s
        sleep(40000);

        waitUntil(ExpectedConditions.textToBePresentInElement(logRow0,
                "Push complete"));
    }
}
