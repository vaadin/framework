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
package com.vaadin.tests.smoke;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DateFieldSmokeTest extends MultiBrowserTest {

    @Test
    public void dateFieldsSmokeTest() {
        openTestURL();

        PopupDateFieldElement popup = $(PopupDateFieldElement.class).first();
        Assert.assertEquals("12/28/16", popup.getValue());
        InlineDateFieldElement inline = $(InlineDateFieldElement.class).first();
        Assert.assertEquals(String.valueOf(29),
                inline.findElement(By.className(
                        "v-inline-datefield-calendarpanel-day-selected"))
                        .getText());

        popup.findElement(By.tagName("button")).click();
        waitUntil(ExpectedConditions
                .visibilityOfElementLocated(By.className("v-datefield-popup")));
        selectDay(findElement(By.className("v-datefield-popup")), 14, "v-");

        waitUntil(driver -> "1. Popup value is : 2016.12.14"
                .equals(getLogRow(0)));

        selectDay(inline, 13, "v-inline-");
        waitUntil(driver -> "2. Inline value is : 2016.12.13"
                .equals(getLogRow(0)));

        inline.findElement(By.className("v-button-prevmonth")).click();
        WebElement monthTitle = inline.findElement(
                By.className("v-inline-datefield-calendarpanel-month"));
        Assert.assertEquals("November 2016", monthTitle.getText());

        inline.findElement(By.className("v-button-nextyear")).click();
        monthTitle = inline.findElement(
                By.className("v-inline-datefield-calendarpanel-month"));
        Assert.assertEquals("November 2017", monthTitle.getText());
    }

    private void selectDay(WebElement calendar, int day, String cssPrefix) {
        List<WebElement> days = calendar.findElements(
                By.className(cssPrefix + "datefield-calendarpanel-day"));
        String dayValue = String.valueOf(day);
        days.stream()
                .filter(dayElement -> dayElement.getText().equals(dayValue))
                .findFirst().get().click();
    }
}
