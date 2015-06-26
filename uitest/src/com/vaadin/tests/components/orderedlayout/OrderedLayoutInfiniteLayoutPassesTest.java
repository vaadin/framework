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
package com.vaadin.tests.components.orderedlayout;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OrderedLayoutInfiniteLayoutPassesTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> b = super.getBrowsersToTest();
        // Chrome and PhantomJS do not support browser zoom changes
        b.remove(Browser.CHROME.getDesiredCapabilities());
        b.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return b;
    }

    @Test
    public void ensureFiniteLayoutPhase() throws Exception {
        openTestURL("debug");
        zoomBrowserIn();
        try {
            $(ButtonElement.class).first().click();
            assertNoErrorNotifications();
            resetZoom();
            assertNoErrorNotifications();
        } finally {
            // Reopen test to ensure that modal window does not prevent zoom
            // reset from taking place
            openTestURL();
            resetZoom();
        }
    }

    private void zoomBrowserIn() {
        WebElement html = driver.findElement(By.tagName("html"));
        html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
    }

    private void resetZoom() {
        WebElement html = driver.findElement(By.tagName("html"));
        html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
    }
}
