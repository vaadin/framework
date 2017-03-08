/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PushStateAndReplaceStateTest extends MultiBrowserTest {

    @Test
    public void testUriFragment() throws Exception {
        driver.get(getTestUrl());
        assertUri(getTestUrl());

        hitButton("test");

        assertUri(getTestUrl() + "/test");

        driver.navigate().back();

        driver.findElement(By.className("v-Notification")).getText()
                .contains("Popstate event");

        assertUri(getTestUrl());

        hitButton("test");
        URI base = new URI(getTestUrl() + "/test");
        hitButton("X");
        URI current = base.resolve("X");
        driver.findElement(By.xpath("//*[@id = 'replace']/input")).click();
        hitButton("root_X");
        current = current.resolve("/X");

        assertUri(current.toString());

        // Now that last change was with replace state, two back calls should go
        // to initial
        driver.navigate().back();
        driver.navigate().back();

        assertUri(getTestUrl());

    }

    private void assertUri(String uri) {
        final String expectedText = "Current Location: " + uri;
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return expectedText.equals(getLocationLabelValue());
            }
        });

        assertEquals(uri, driver.getCurrentUrl());
    }

    private String getLocationLabelValue() {
        String text = vaadinElementById("locationLabel").getText();
        return text;
    }

}
