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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientContextMenuEventTest extends GridBasicClientFeaturesTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // PhantomJS doesn't support context click..
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testContextMenuEventIsHandledCorrectly() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Internals", "Listeners",
                "Add context menu listener");

        openDebugLogTab();
        clearDebugMessages();

        new Actions(getDriver())
                .moveToElement(getGridElement().getCell(0, 0), 5, 5)
                .contextClick().perform();

        assertTrue(
                "Debug log was not visible",
                isElementPresent(By
                        .xpath("//span[text() = 'Prevented opening a context menu in grid body']")));

        new Actions(getDriver())
                .moveToElement(getGridElement().getHeaderCell(0, 0), 5, 5)
                .contextClick().perform();

        assertTrue(
                "Debug log was not visible",
                isElementPresent(By
                        .xpath("//span[text() = 'Prevented opening a context menu in grid header']")));

    }
}
