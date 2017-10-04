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
package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for issue #12726, IE's make text selection when sub windows are
 * dragged(moved).
 *
 * @since
 * @author Vaadin Ltd
 */
public class SubWindowsTextSelectionTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return SubWindows.class;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11);
    }

    @Test
    public void verifyNoTextSelectionOnMove() throws Exception {

        openTestURL();

        WebElement element = driver
                .findElement(By.className("v-window-outerheader"));

        Point location = element.getLocation();

        element.click();

        new Actions(driver).moveToElement(element).perform();
        sleep(100);
        // move pointer bit right from the caption text
        new Actions(driver).moveByOffset(50, 0).clickAndHold()
                .moveByOffset(10, 2).moveByOffset(10, 0).moveByOffset(10, 0)
                .moveByOffset(10, 0).release().perform();

        String selection = ((JavascriptExecutor) getDriver())
                .executeScript("return document.getSelection().toString();")
                .toString();

        assertTrue("Text selection was not empty:" + selection,
                selection.isEmpty());

        // Verify also that window was really moved
        Point location2 = element.getLocation();
        assertEquals(location.getX() + (4 * 10), location2.getX());

    }

}
