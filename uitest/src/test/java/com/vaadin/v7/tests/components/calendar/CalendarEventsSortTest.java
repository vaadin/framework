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
package com.vaadin.v7.tests.components.calendar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Check how event sorting works in calendar month and week views.
 * 
 * @author Vaadin Ltd
 */
public class CalendarEventsSortTest extends MultiBrowserTest {

    @Test
    public void testByDuration() {
        openTestURL();

        checkSortByDuration(true);
    }

    @Test
    public void testByStartDate() {
        openTestURL();

        checkSortByStartDate(true);
    }

    @Test
    public void testByProvider() {
        openTestURL();

        List<WebElement> events = findElements(
                By.className("v-calendar-event-month"));
        checkProviderOrder(events);
    }

    @Test
    public void testWeekByDuration() {
        openTestURL();

        findElement(By.className("view")).click();

        checkSortByDuration(false);
    }

    @Test
    public void testWeekByStartDate() {
        openTestURL();

        findElement(By.className("view")).click();

        checkSortByStartDate(false);
    }

    @Test
    public void testWeekByProvider() {
        openTestURL();

        findElement(By.className("view")).click();

        List<WebElement> events = findElements(
                By.className("v-calendar-event-caption"));
        checkProviderOrder(events);
    }

    private void checkSortByStartDate(boolean month) {
        sort("by-start-date", false);

        String style = month ? "v-calendar-event-month"
                : "v-calendar-event-caption";
        List<WebElement> events = findElements(By.className(style));
        checkStartDateOrderDesc(events);

        sort("by-start-date", true);

        events = findElements(By.className(style));
        checkStartDateOrderAsc(events);
    }

    private void sort(String style, boolean ascending) {
        findElement(By.className(style)).click();

        if (!isElementPresent(
                By.cssSelector('.' + style + (ascending ? ".asc" : ".desc")))) {
            findElement(By.className(style)).click();
        }
    }

    private void checkSortByDuration(boolean month) {
        sort("by-duration", false);

        String style = month ? "v-calendar-event-month"
                : "v-calendar-event-caption";

        List<WebElement> events = findElements(By.className(style));
        checkDurationOrderDesc(events);

        sort("by-duration", true);
        events = findElements(By.className(style));
        checkDurationOrderAsc(events);
    }

    private void checkDurationOrderDesc(List<WebElement> events) {
        Assert.assertTrue(
                "'Second' event should be the first when sorted by duration",
                events.get(0).getText().endsWith("second"));
        Assert.assertTrue(
                "'Third' event should be the second when sorted by duration",
                events.get(1).getText().endsWith("third"));
        Assert.assertTrue(
                "'First' event should be the third when sorted by duration",
                events.get(2).getText().endsWith("first"));
    }

    private void checkDurationOrderAsc(List<WebElement> events) {
        Assert.assertTrue(
                "'First' event should be the first when sorted by duration",
                events.get(0).getText().endsWith("first"));
        Assert.assertTrue(
                "'Third' event should be the second when sorted by duration",
                events.get(1).getText().endsWith("third"));
        Assert.assertTrue(
                "'Second' event should be the third when sorted by duration",
                events.get(2).getText().endsWith("second"));
    }

    private void checkStartDateOrderDesc(List<WebElement> events) {
        Assert.assertTrue(
                "'Third' event should be the first when sorted by start date",
                events.get(0).getText().endsWith("third"));
        Assert.assertTrue(
                "'Second' event should be the second when sorted by start date",
                events.get(1).getText().endsWith("second"));
        Assert.assertTrue(
                "'First' event should be the third when sorted by start date",
                events.get(2).getText().endsWith("first"));
    }

    private void checkStartDateOrderAsc(List<WebElement> events) {
        Assert.assertTrue(
                "'First' event should be the first when sorted by start date",
                events.get(0).getText().endsWith("first"));
        Assert.assertTrue(
                "'Second' event should be the second when sorted by start date",
                events.get(1).getText().endsWith("second"));
        Assert.assertTrue(
                "'Third' event should be the third when sorted by start date",
                events.get(2).getText().endsWith("third"));
    }

    private void checkProviderOrder(List<WebElement> events) {
        Assert.assertTrue(
                "'First' event should be the first when sorted by provider",
                events.get(0).getText().endsWith("first"));
        Assert.assertTrue(
                "'Second' event should be the second when sorted by provider",
                events.get(1).getText().endsWith("second"));
        Assert.assertTrue(
                "'Third' event should be the third when sorted by provider",
                events.get(2).getText().endsWith("third"));
    }

}
