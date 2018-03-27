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
package com.vaadin.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check high resolution time availability in browser (depending on
 * browser).
 *
 * @author Vaadin Ltd
 */
public class CurrentTimeMillisTest extends MultiBrowserTest {

    @Test
    public void testJsonParsing() {
        setDebug(true);
        openTestURL();

        boolean phantomJs1 = BrowserUtil.isPhantomJS(getDesiredCapabilities())
                && "1".equals(getDesiredCapabilities().getVersion());
        boolean highResTimeSupported = !phantomJs1
                && !BrowserUtil.isSafari(getDesiredCapabilities());

        String time = getJsonParsingTime();
        assertNotNull("JSON parsing time is not found", time);
        time = time.trim();
        if (time.endsWith("ms")) {
            time = time.substring(0, time.length() - 2);
        }
        if (highResTimeSupported) {
            if (BrowserUtil.isChrome(getDesiredCapabilities())
                    || BrowserUtil.isFirefox(getDesiredCapabilities())) {
                // Chrome (version 33 at least) sometimes doesn't use high res
                // time for very short times
                assertTrue(
                        "High resolution time is not used in "
                                + "JSON parsing mesurement. Time=" + time,
                        time.equals("0") || time.equals("1")
                                || time.indexOf('.') > 0);
            } else {
                assertTrue(
                        "High resolution time is not used in "
                                + "JSON parsing mesurement. Time=" + time,
                        time.indexOf('.') > 0);
            }
        } else {
            assertFalse("Unexpected dot is detected in browser "
                    + "that doesn't support high resolution time and "
                    + "should report time as integer number. Time=" + time,
                    time.indexOf('.') > 0);
        }
    }

    private String getJsonParsingTime() {
        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.TAB);
        actions.sendKeys(Keys.SPACE).perform();
        findElement(By.className("v-debugwindow-tab")).click();

        List<WebElement> messages = findElements(
                By.className("v-debugwindow-message"));
        for (WebElement message : messages) {
            if (message.getAttribute("innerHTML").startsWith("JSON parsing")) {
                String text = message.getAttribute("innerHTML");
                int index = text.lastIndexOf(' ');
                return text.substring(index);
            }
        }
        return null;
    }

}
