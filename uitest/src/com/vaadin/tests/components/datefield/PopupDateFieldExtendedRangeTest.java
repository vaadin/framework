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
package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests contents and functionality of PopupDateField's popup.
 * 
 * @author Vaadin Ltd
 */
public class PopupDateFieldExtendedRangeTest extends MultiBrowserTest {

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testFirstDateField() {
        List<DateFieldElement> dateFields = $(DateFieldElement.class).all();
        assertEquals("unexpected amount of datefields", 3, dateFields.size());

        DateFieldElement dateField = dateFields.get(0);

        // open the popup
        dateField.findElement(By.tagName("button")).click();

        assertTrue("popup not found when there should be one",
                isElementPresent(By.className("v-datefield-popup")));

        // verify contents
        WebElement popup = findElement(By.className("v-datefield-popup"));
        assertEquals(
                "unexpected month",
                "tammikuu 2011",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        List<WebElement> headerElements = popup.findElement(
                By.className("v-datefield-calendarpanel-weekdays"))
                .findElements(By.tagName("td"));
        List<WebElement> weekdays = new ArrayList<WebElement>();
        for (WebElement headerElement : headerElements) {
            if ("columnheader".equals(headerElement.getAttribute("role"))) {
                weekdays.add(headerElement);
            }
        }
        assertEquals("unexpected weekday count", 7, weekdays.size());
        assertEquals("unexpected first day of week", "MA", weekdays.get(0)
                .getText());
        assertEquals(
                "unexpected weeknumber count",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-weeknumber"))
                        .size());
        assertEquals(
                "unexpected selection",
                "1",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .getText());
        assertEquals(
                "unexpected focus",
                "1",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());
        List<WebElement> days = popup.findElements(By
                .className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "27", days.get(0).getText());
        assertEquals("unexpected day content", "4", days.get(8).getText());
        assertEquals("unexpected day content", "21", days.get(25).getText());
        assertEquals("unexpected day content", "6", days.get(41).getText());

        // move to the previous month
        popup.findElement(By.className("v-datefield-calendarpanel-prevmonth"))
                .findElement(By.tagName("button")).click();

        // verify contents
        assertEquals(
                "unexpected month",
                "joulukuu 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        assertEquals(
                "unexpected selection",
                "1",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .getText());
        assertEquals(
                "unexpected focus",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .size());
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "29", days.get(0).getText());
        assertEquals("unexpected day content", "7", days.get(8).getText());
        assertEquals("unexpected day content", "24", days.get(25).getText());
        assertEquals("unexpected day content", "9", days.get(41).getText());

        // move to the previous year
        popup.findElement(By.className("v-datefield-calendarpanel-prevyear"))
                .findElement(By.tagName("button")).click();

        // verify contents
        assertEquals(
                "unexpected month",
                "joulukuu 2009",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        assertEquals(
                "unexpected selection",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .size());
        assertEquals(
                "unexpected focus",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .size());
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "30", days.get(0).getText());
        assertEquals("unexpected day content", "8", days.get(8).getText());
        assertEquals("unexpected day content", "25", days.get(25).getText());
        assertEquals("unexpected day content", "10", days.get(41).getText());

        // close the popup by clicking the button again
        dateField.findElement(By.tagName("button")).click();

        // TODO: remove this once #14405 has been fixed
        if (!getBrowsersExcludingIE().contains(getDesiredCapabilities())) {
            // click something else outside the popup to close it
            dateField.findElement(By.tagName("input")).click();
        }

        assertFalse("popup found when there should be none",
                isElementPresent(By.className("v-datefield-popup")));
    }

    @Test
    public void testSecondDateField() throws InterruptedException {
        DateFieldElement dateField = $(DateFieldElement.class).all().get(1);
        ButtonElement button = $(ButtonElement.class).first();

        // change the date
        button.click();
        sleep(100);

        // open the popup
        dateField.findElement(By.tagName("button")).click();

        assertTrue("popup not found when there should be one",
                isElementPresent(By.className("v-datefield-popup")));

        // verify contents
        WebElement popup = findElement(By.className("v-datefield-popup"));
        assertEquals(
                "unexpected month",
                "February 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        List<WebElement> headerElements = popup.findElement(
                By.className("v-datefield-calendarpanel-weekdays"))
                .findElements(By.tagName("td"));
        List<WebElement> weekdays = new ArrayList<WebElement>();
        for (WebElement headerElement : headerElements) {
            if ("columnheader".equals(headerElement.getAttribute("role"))) {
                weekdays.add(headerElement);
            }
        }
        assertEquals("unexpected weekday count", 7, weekdays.size());
        assertEquals("unexpected first day of week", "SUN", weekdays.get(0)
                .getText());
        assertEquals(
                "unexpected weeknumber count",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-weeknumber"))
                        .size());
        assertEquals(
                "unexpected selection",
                "16",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .getText());
        assertEquals(
                "unexpected focus",
                "16",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());
        List<WebElement> days = popup.findElements(By
                .className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "31", days.get(0).getText());
        assertEquals("unexpected day content", "8", days.get(8).getText());
        assertEquals("unexpected day content", "25", days.get(25).getText());
        assertEquals("unexpected day content", "13", days.get(41).getText());

        // navigate down
        WebElement popupBody = popup.findElement(By
                .className("v-datefield-calendarpanel"));
        popupBody.sendKeys(Keys.ARROW_DOWN);

        // ensure the focus changed
        assertEquals(
                "unexpected focus",
                "23",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());

        // navigate down
        popupBody.sendKeys(Keys.ARROW_DOWN);

        // verify contents
        assertEquals(
                "unexpected month",
                "March 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        assertEquals(
                "unexpected selection",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .size());
        assertEquals(
                "unexpected focus",
                "2",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "28", days.get(0).getText());
        assertEquals("unexpected day content", "8", days.get(8).getText());
        assertEquals("unexpected day content", "25", days.get(25).getText());
        assertEquals("unexpected day content", "10", days.get(41).getText());

        // navigate left
        popupBody = popup
                .findElement(By.className("v-datefield-calendarpanel"));
        popupBody.sendKeys(Keys.ARROW_LEFT);

        // ensure the focus changed
        assertEquals(
                "unexpected focus",
                "1",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());

        // navigate left
        popupBody.sendKeys(Keys.ARROW_LEFT);

        // verify contents
        assertEquals(
                "unexpected month",
                "February 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        assertEquals(
                "unexpected selection",
                "16",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .getText());
        assertEquals(
                "unexpected focus",
                "28",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "31", days.get(0).getText());
        assertEquals("unexpected day content", "8", days.get(8).getText());
        assertEquals("unexpected day content", "25", days.get(25).getText());
        assertEquals("unexpected day content", "13", days.get(41).getText());

        // close the popup by clicking the input field
        dateField.findElement(By.tagName("input")).click();

        assertFalse("popup found when there should be none",
                isElementPresent(By.className("v-datefield-popup")));
    }

    @Test
    public void testThirdDateField() throws InterruptedException {
        DateFieldElement dateField = $(DateFieldElement.class).all().get(2);
        ButtonElement button = $(ButtonElement.class).first();

        // change the date
        button.click();
        sleep(100);

        // open the popup
        dateField.findElement(By.tagName("button")).click();

        assertTrue("popup not found when there should be one",
                isElementPresent(By.className("v-datefield-popup")));

        // verify contents
        WebElement popup = findElement(By.className("v-datefield-popup"));
        assertEquals(
                "unexpected month",
                "helmikuu 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        List<WebElement> headerElements = popup.findElement(
                By.className("v-datefield-calendarpanel-weekdays"))
                .findElements(By.tagName("td"));
        List<WebElement> weekdays = new ArrayList<WebElement>();
        for (WebElement headerElement : headerElements) {
            if ("columnheader".equals(headerElement.getAttribute("role"))) {
                weekdays.add(headerElement);
            }
        }
        assertEquals("unexpected weekday count", 7, weekdays.size());
        assertEquals("unexpected first day of week", "MA", weekdays.get(0)
                .getText());
        List<WebElement> weeknumbers = popup.findElements(By
                .className("v-datefield-calendarpanel-weeknumber"));
        assertEquals("unexpected weeknumber count", 6, weeknumbers.size());
        assertEquals("unexpected weeknumber content", "5", weeknumbers.get(0)
                .getText());
        assertEquals("unexpected weeknumber content", "10", weeknumbers.get(5)
                .getText());
        assertEquals(
                "unexpected selection",
                "16",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .getText());
        assertEquals(
                "unexpected focus",
                "16",
                popup.findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                        .getText());
        List<WebElement> days = popup.findElements(By
                .className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "1", days.get(0).getText());
        assertEquals("unexpected day content", "9", days.get(8).getText());
        assertEquals("unexpected day content", "26", days.get(25).getText());
        assertEquals("unexpected day content", "14", days.get(41).getText());

        // navigate to previous month
        WebElement popupBody = popup.findElement(By
                .className("v-datefield-calendarpanel"));
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        popupBody.sendKeys(Keys.ARROW_LEFT);
        new Actions(driver).keyUp(Keys.SHIFT).perform();

        // TODO: remove this once #14406 has been fixed
        if (!getBrowsersExcludingIE().contains(getDesiredCapabilities())
                && !Browser.IE8.getDesiredCapabilities().equals(
                        getDesiredCapabilities())) {
            popup.findElement(
                    By.className("v-datefield-calendarpanel-prevmonth"))
                    .findElement(By.tagName("button")).click();
        }

        // verify contents
        assertEquals(
                "unexpected month",
                "tammikuu 2010",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        weeknumbers = popup.findElements(By
                .className("v-datefield-calendarpanel-weeknumber"));
        assertEquals("unexpected weeknumber count", 6, weeknumbers.size());
        assertEquals("unexpected weeknumber content", "53", weeknumbers.get(0)
                .getText());
        assertEquals("unexpected weeknumber content", "5", weeknumbers.get(5)
                .getText());
        assertEquals(
                "unexpected selection",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .size());
        // TODO: remove this check once #14406 has been fixed -- clicking the
        // button instead of navigating with arrow keys steals the focus
        if (getBrowsersExcludingIE().contains(getDesiredCapabilities())
                || Browser.IE8.getDesiredCapabilities().equals(
                        getDesiredCapabilities())) {
            assertEquals(
                    "unexpected focus",
                    "16",
                    popup.findElement(
                            By.className("v-datefield-calendarpanel-day-focused"))
                            .getText());
        }
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "28", days.get(0).getText());
        assertEquals("unexpected day content", "5", days.get(8).getText());
        assertEquals("unexpected day content", "22", days.get(25).getText());
        assertEquals("unexpected day content", "7", days.get(41).getText());

        // navigate to previous year
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        popupBody.sendKeys(Keys.ARROW_DOWN);
        new Actions(driver).keyUp(Keys.SHIFT).perform();

        // TODO: remove this once #14406 has been fixed
        popup.findElement(By.className("v-datefield-calendarpanel-prevyear"))
                .findElement(By.tagName("button")).click();

        // verify contents
        assertEquals(
                "unexpected month",
                "tammikuu 2009",
                popup.findElements(
                        By.className("v-datefield-calendarpanel-month")).get(1)
                        .getText());
        weeknumbers = popup.findElements(By
                .className("v-datefield-calendarpanel-weeknumber"));
        assertEquals("unexpected weeknumber count", 6, weeknumbers.size());
        assertEquals("unexpected weeknumber content", "1", weeknumbers.get(0)
                .getText());
        assertEquals("unexpected weeknumber content", "6", weeknumbers.get(5)
                .getText());
        assertEquals(
                "unexpected selection",
                0,
                popup.findElements(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .size());
        // TODO: remove this check once #14406 has been fixed -- clicking the
        // button instead of navigating with arrow keys steals the focus
        if (false) {
            assertEquals(
                    "unexpected focus",
                    "16",
                    popup.findElement(
                            By.className("v-datefield-calendarpanel-day-focused"))
                            .getText());
        }
        days = popup
                .findElements(By.className("v-datefield-calendarpanel-day"));
        assertEquals("unexpected day count", 42, days.size());
        assertEquals("unexpected day content", "29", days.get(0).getText());
        assertEquals("unexpected day content", "6", days.get(8).getText());
        assertEquals("unexpected day content", "23", days.get(25).getText());
        assertEquals("unexpected day content", "8", days.get(41).getText());

        // close the popup by clicking an unrelated element
        button.click();

        assertFalse("popup found when there should be none",
                isElementPresent(By.className("v-datefield-popup")));
    }

}
