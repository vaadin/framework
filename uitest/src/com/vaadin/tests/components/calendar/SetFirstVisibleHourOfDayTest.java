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
package com.vaadin.tests.components.calendar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that calendar week and day views are correct when using
 * setFirstVisibleHourOfDay()
 */
public class SetFirstVisibleHourOfDayTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testDayView() {
        openTestURL();
        waitForElementPresent(By.className("v-calendar"));

        // open day view
        clickElement("v-calendar-day-number", "5");

        // first of all check if event is present in calendar view
        waitForElementPresent(By.className("v-calendar-event-content"));

        WebElement event = getDriver().findElement(
                By.className("v-calendar-event-content"));
        WebElement dateSlot = getDriver().findElement(
                By.className("v-datecellslot"));

        Assert.assertEquals(
                "The height of shown part of calendar event should be equal to 12 datecell slots",
                dateSlot.getSize().getHeight() * 12, event.getSize()
                        .getHeight());
    }

    @Test
    public void testWeekView() {
        openTestURL();
        waitForElementPresent(By.className("v-calendar"));

        // open week view
        clickElement("v-calendar-week-number", "36");

        // first of all check if event is present in calendar view
        waitForElementPresent(By.className("v-calendar-event-content"));

        WebElement event = getDriver().findElement(
                By.className("v-calendar-event-content"));
        WebElement dateSlot = getDriver().findElement(
                By.className("v-datecellslot"));

        Assert.assertEquals(
                "The height of shown part of calendar event should be equal to 12 datecell slots",
                dateSlot.getSize().getHeight() * 12, event.getSize()
                        .getHeight());
    }

    private void clickElement(String className, String text) {
        List<WebElement> elements = findElements(By.className(className));

        boolean found = false;
        for (WebElement webElement : elements) {
            if (webElement.getText().equals(text)) {
                webElement.click();
                if (BrowserUtil.isIE8(getDesiredCapabilities())) {
                    try {
                        // sometimes the element only gets focus from click and
                        // we need to click the text, which is in the right edge
                        // of the element
                        testBenchElement(webElement).click(
                                webElement.getSize().getWidth() - 5, 9);
                    } catch (StaleElementReferenceException e) {
                        // the first click succeeded after all
                    }
                }
                found = true;
                break;
            }
        }

        if (!found) {
            Assert.fail("Element " + className + " with text " + text
                    + " not found.");
        }
    }

}
