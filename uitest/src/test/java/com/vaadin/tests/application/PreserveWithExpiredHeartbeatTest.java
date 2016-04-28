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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class PreserveWithExpiredHeartbeatTest extends SingleBrowserTest {
    @Test
    public void testNavigateBackAfterMissingHeartbeats()
            throws InterruptedException {
        final int heartbeatInterval = 5000;

        openTestURL();
        String originalId = getUiIdentification();

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < heartbeatInterval * 3.1) {
            // "Close" the tab
            driver.get("about:blank");

            sleep(heartbeatInterval / 2);

            // "Reopen" tab
            openTestURL();

            // Verify that that we still get the same UI
            Assert.assertEquals("Original UI has been closed", originalId,
                    getUiIdentification());
        }
    }

    private String getUiIdentification() {
        return $(LabelElement.class).id("idLabel").getText();
    }
}
