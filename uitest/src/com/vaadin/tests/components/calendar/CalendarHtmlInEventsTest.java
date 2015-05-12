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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CalendarElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CalendarHtmlInEventsTest extends SingleBrowserTest {

    private NativeSelectElement periodSelect;
    private CheckBoxElement htmlAllowed;
    private CalendarElement calendar;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        periodSelect = $(NativeSelectElement.class).first();
        htmlAllowed = $(CheckBoxElement.class).first();
        calendar = $(CalendarElement.class).first();
    }

    @Test
    public void monthViewEventCaptions() {
        Assert.assertEquals(getMonthEvent(0).getText(),
                "12:00 AM <b>Hello</b> <u>world</u>!");

        // Switch to HTML mode
        click(htmlAllowed);
        Assert.assertEquals("1. HTML in event caption: true", getLogRow(0));

        Assert.assertEquals(getMonthEvent(0).getText(), "12:00 AM Hello world!");
    }

    @Test
    public void weekViewEventCaptions() {
        periodSelect.selectByText("Week");
        Assert.assertEquals("4:00 AM\n<b>Hello</b> <u>world</u>!",
                getWeekEvent(1).getText());

        // Switch to HTML mode
        click(htmlAllowed);
        Assert.assertEquals("1. HTML in event caption: true", getLogRow(0));

        Assert.assertEquals("4:00 AM\nHello world!", getWeekEvent(1).getText());
    }

    @Test
    public void dayViewEventCaptions() {
        periodSelect.selectByText("Day");
        Assert.assertEquals("3:00 AM\n<b>Hello</b> <u>world</u>!",
                getWeekEvent(0).getText());

        // Switch to HTML mode
        click(htmlAllowed);
        Assert.assertEquals("1. HTML in event caption: true", getLogRow(0));
        Assert.assertEquals("3:00 AM\nHello world!", getWeekEvent(0).getText());
    }

    private WebElement getMonthEvent(int dayInCalendar) {
        return getMonthDay(dayInCalendar).findElement(
                By.className("v-calendar-event"));
    }

    private WebElement getWeekEvent(int dayInCalendar) {
        return getWeekDay(dayInCalendar).findElement(
                By.className("v-calendar-event"));
    }

    private WebElement getMonthDay(int i) {
        return calendar.findElements(By.className("v-calendar-month-day")).get(
                i);
    }

    private WebElement getWeekDay(int i) {
        return calendar.findElements(By.className("v-calendar-day-times")).get(
                i);
    }
}
