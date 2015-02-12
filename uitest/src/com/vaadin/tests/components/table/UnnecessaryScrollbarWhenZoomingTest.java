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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UnnecessaryScrollbarWhenZoomingTest extends MultiBrowserTest {

    private ZoomLevelSetter zoomSetter;
    private int zoomOutIterations = 3;
    private int zoomInIterations = 3;

    @Before
    public void init() {
        testBench().resizeViewPortTo(995, 400);
        DesiredCapabilities capabilities = getDesiredCapabilities();
        if (BrowserUtil.isChrome(capabilities)
                || BrowserUtil.isPhantomJS(capabilities)) {
            zoomSetter = new ChromeZoomLevelSetter(driver);
        } else {
            zoomSetter = new NonChromeZoomLevelSetter(driver);
        }
        zoomSetter.resetZoom();
        openTestURL();
        // IE sometimes has trouble waiting long enough.
        new WebDriverWait(getDriver(), 30).until(ExpectedConditions
                .presenceOfElementLocated(By
                        .cssSelector(".v-table-body-wrapper")));
    }

    @Test
    public void testInitial() {
        testExtraScrollbarsNotShown();
    }

    @Test
    public void testZoomingIn() {
        for (int i = 0; i < zoomInIterations; i++) {
            zoomSetter.increaseZoom();
            testExtraScrollbarsNotShown();
        }
    }

    @Test
    public void testZoomingOut() throws InterruptedException {
        for (int i = 0; i < zoomOutIterations; i++) {
            zoomSetter.decreaseZoom();
            testExtraScrollbarsNotShown();
        }
    }

    @After
    public void resetZoomLevel() {
        zoomSetter.resetZoom();
    }

    private void testExtraScrollbarsNotShown() {
        // wait a bit for the layout
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        WebElement element = findElement(By
                .cssSelector(".v-table-body-wrapper"));
        assertNotNull("There must be a table", element);
        String overflow = element.getCssValue("overflow");
        // As long as the overflow is hidden, there will not be scroll bars.
        if (!"hidden".equals(overflow)) {
            // compare scroll width to offset width. True if scrolling.
            String detectHorizontalScroll = "return arguments[0].scrollWidth > arguments[0].clientWidth";
            Boolean horizontal = (Boolean) ((TestBenchCommandExecutor) getDriver())
                    .executeScript(detectHorizontalScroll, element);
            assertEquals("there must be no horizontal scrollbar", false,
                    horizontal);

            String detectVerticalScroll = "return arguments[0].scrollHeight > arguments[0].clientHeight";
            Boolean vertical = (Boolean) ((TestBenchCommandExecutor) getDriver())
                    .executeScript(detectVerticalScroll, element);
            assertEquals("there must be no vertical scrollbar", false, vertical);
        }
    }

    interface ZoomLevelSetter {
        public void increaseZoom();

        public void decreaseZoom();

        public void resetZoom();
    }

    /*
     * A class for setting the zoom levels by sending keys such as ctrl and +.
     */
    class NonChromeZoomLevelSetter implements ZoomLevelSetter {
        private WebDriver driver;

        public NonChromeZoomLevelSetter(WebDriver driver) {
            this.driver = driver;
        }

        @Override
        public void increaseZoom() {
            getElement().sendKeys(Keys.chord(Keys.CONTROL, Keys.ADD));
        }

        @Override
        public void decreaseZoom() {
            getElement().sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
        }

        @Override
        public void resetZoom() {
            getElement().sendKeys(Keys.chord(Keys.CONTROL, "0"));
        }

        private WebElement getElement() {
            return driver.findElement(By.tagName("html"));
        }
    }

    /*
     * A class for setting the zoom levels using JavaScript. This setter is used
     * for browsers for which the method of sending the keys ctrl and + does not
     * work.
     */
    class ChromeZoomLevelSetter implements ZoomLevelSetter {
        private JavascriptExecutor js;
        private int currentZoomIndex = 2;
        private int[] zoomLevels = { 70, 80, 90, 100, 110, 120, 130 };

        public ChromeZoomLevelSetter(WebDriver driver) {
            js = (JavascriptExecutor) driver;
        }

        @Override
        public void increaseZoom() {
            currentZoomIndex++;
            if (currentZoomIndex >= zoomLevels.length) {
                currentZoomIndex = zoomLevels.length - 1;
            }
            js.executeScript("document.body.style.zoom='"
                    + zoomLevels[currentZoomIndex] + "%'");
        }

        @Override
        public void decreaseZoom() {
            currentZoomIndex--;
            if (currentZoomIndex < 0) {
                currentZoomIndex = 0;
            }
            js.executeScript("document.body.style.zoom='"
                    + zoomLevels[currentZoomIndex] + "%'");
        }

        @Override
        public void resetZoom() {
            js.executeScript("document.body.style.zoom='100%'");
            currentZoomIndex = Arrays.binarySearch(zoomLevels, 100);
        }
    }

}
