/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.integration.websocket;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.integration.AbstractServletIntegrationTest;

public class ServletIntegrationWebsocketIT
        extends AbstractServletIntegrationTest {
    // Uses the test method declared in the super class

    @Override
    protected String getTestPath() {
        return "/run/ServletIntegrationWebsocketUI";
    }

    @Test
    public void testWebsockedUsed() {
        // Make sure the correct debug window tab is open.
        findElements(By.className("v-debugwindow-tab")).get(1).click();

        try {
            // Wait to make sure correct tab is shown.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        WebElement row = findElements(By.className("v-debugwindow-row")).get(7);
        assertEquals("Communication method",
                row.findElement(By.className("caption")).getText());
        assertEquals("Client to server: websocket, server to client: websocket",
                row.findElement(By.className("value")).getText());
    }

    protected Stream<String> getParameters() {
        return Stream.concat(super.getParameters(), Stream.of("debug"))
                .distinct();
    }
}
