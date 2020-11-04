/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.integration.websocket;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.integration.AbstractServletIntegrationTest;

public class ServletIntegrationWebsocketIT
        extends AbstractServletIntegrationTest {
    // Uses the test method declared in the super class

    private static final Set<String> nonWebsocketServers = new HashSet<String>();

    static {
        nonWebsocketServers.add("liberty-microprofile");
    }

    @Override
    public void setup() throws Exception {
        Assume.assumeFalse("This server does not support Websockets",
                nonWebsocketServers
                        .contains(System.getProperty("server-name")));

        super.setup();
    }

    @Override
    protected String getTestPath() {
        return "/run/ServletIntegrationWebsocketUI";
    }

    @Test
    public void testWebsockedUsed() {
        List<String> params = new ArrayList<String>();
        for (String param : getParameters()) {
            params.add(param);
        }
        params.add("debug");

        // Reopen the page with debug window
        openTestURL(params.toArray(new String[params.size()]));

        // Make sure the correct debug window tab is open.
        findElements(By.className("v-debugwindow-tab")).get(1).click();

        try {
            // Wait to make sure correct tab is shown.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        WebElement row = findElements(By.className("v-debugwindow-row")).get(7);
        assertEquals("Communication method",
                row.findElement(By.className("caption")).getAttribute("innerText"));
        assertEquals("Client to server: websocket, server to client: websocket",
                row.findElement(By.className("value")).getAttribute("innerText"));
    }
}
