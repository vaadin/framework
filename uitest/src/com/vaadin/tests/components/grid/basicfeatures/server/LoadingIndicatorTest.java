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
package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class LoadingIndicatorTest extends GridBasicFeaturesTest {
    @Test
    public void testLoadingIndicator() throws InterruptedException {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "State", "Container delay", "2000");

        GridElement gridElement = $(GridElement.class).first();

        Assert.assertFalse(
                "Loading indicator should not be visible before disabling waitForVaadin",
                isLoadingIndicatorVisible());

        testBench().disableWaitForVaadin();

        // Scroll to a completely new location
        gridElement.getCell(200, 1);

        // Wait for loading indicator delay
        Thread.sleep(500);

        Assert.assertTrue(
                "Loading indicator should be visible when fetching rows that are visible",
                isLoadingIndicatorVisible());

        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-loading-indicator")));

        // Scroll so much that more data gets fetched, but not so much that
        // missing rows are shown
        gridElement.getCell(230, 1);

        // Wait for potentially triggered loading indicator to become visible
        Thread.sleep(500);

        Assert.assertFalse(
                "Loading indicator should not be visible when fetching rows that are not visible",
                isLoadingIndicatorVisible());

        // Finally verify that there was actually a request going on
        Thread.sleep(2000);

        String firstLogRow = getLogRow(0);
        Assert.assertTrue("Last log message was not the fourth message: "
                + firstLogRow, firstLogRow.startsWith("4. Requested items"));
    }

    private boolean isLoadingIndicatorVisible() {
        WebElement loadingIndicator = findElement(By
                .className("v-loading-indicator"));
        if (loadingIndicator == null) {
            return false;
        } else {
            return loadingIndicator.isDisplayed();
        }

    }
}
