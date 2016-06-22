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

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * For testing that UI scroll does not jump back to up when: 1. UI is in iframe
 * 2. the window scrolled down 3. and table is clicked
 * 
 * @author Vaadin Ltd
 */
public class TableInIframeRowClickScrollJumpTest extends MultiBrowserTest {

    private static final String TEST_URL = "statictestfiles/TableInIframeRowClickScrollJumpTest.html";

    @Test
    public void testRowClicking_WhenScrolledDown_shouldMaintainScrollPosition()
            throws InterruptedException {
        System.out.println(">>>" + getBaseURL() + TEST_URL);

        driver.get(getUrl());

        // using non-standard way because of iframe
        sleep(4000);

        // make sure we are in the "main content"
        driver.switchTo().defaultContent();
        sleep(2000);
        switchIntoIframe();

        // using non-standard way because of iframe
        waitForElementVisible(By.id("scroll-button"));

        ButtonElement scrollbutton = $(ButtonElement.class).id("scroll-button");
        scrollbutton.click();

        // using non-standard way because of iframe
        sleep(1000);

        Long scrollPosition = getWindowsScrollPosition();

        assertThat("Scroll position should be greater than 100 (it was "
                + scrollPosition + ")", scrollPosition > 100);

        TableElement table = $(TableElement.class).first();
        table.getRow(13).getCell(0).click();

        // using non-standard way because of iframe
        sleep(1000);

        Long scrollPosition2 = getWindowsScrollPosition();

        assertThat("Scroll position should stay about the same. Old was "
                + scrollPosition + " and new one " + scrollPosition2,
                Math.abs(scrollPosition - scrollPosition2) < 10);
    }

    private String getUrl() {
        String url;
        // using non-standard way because of iframe
        if (getBaseURL().charAt(getBaseURL().length() - 1) == '/') {
            url = getBaseURL() + TEST_URL;
        } else {
            // this one is for gerrit's teamcity :(
            url = getBaseURL() + '/' + TEST_URL;
        }
        return url;
    }

    public void switchIntoIframe() {
        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        assertThat("No frames was found", frames.size() > 0);
        driver.switchTo().frame(frames.get(0));
    }

    private Long getWindowsScrollPosition() {
        // measure scroll pos in the main window
        driver.switchTo().defaultContent();

        JavascriptExecutor executor = (JavascriptExecutor) driver;
        Long value = (Long) executor
                .executeScript("if (window.pageYOffset) return window.pageYOffset;else if (window.document.documentElement.scrollTop) return window.document.documentElement.scrollTop;else return window.document.body.scrollTop;");

        // back to the iframe
        switchIntoIframe();

        return value;
    }

    @Override
    // using non-standard way because of iframe
    protected void closeApplication() {
        if (driver != null) {
            try {
                driver.get(getUrl() + "?closeApplication");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
