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
package com.vaadin.testbench.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Calendar")
@Deprecated
public class CalendarElement extends AbstractComponentElement {
    public List<WebElement> getWeekNumbers() {
        return findElements(By.className("v-calendar-week-number"));
    }

    public boolean hasMonthView() {
        return isElementPresent(By.className("v-calendar-week-numbers"));
    }

    public boolean hasWeekView() {
        return isElementPresent(By.className("v-calendar-header-week"));
    }

    public List<WebElement> getDayNumbers() {
        return findElements(By.className("v-calendar-day-number"));
    }

    public List<WebElement> getMonthDays() {
        return findElements(By.className("v-calendar-month-day"));
    }

    public boolean hasDayView() {
        return getDayHeaders().size() == 1;
    }

    public List<WebElement> getDayHeaders() {
        return findElements(By.className("v-calendar-header-day"));
    }

    public void back() {
        if (hasWeekView() || hasDayView()) {
            findElement(By.className("v-calendar-back")).click();
        } else {
            throw new IllegalStateException(
                    "Navigation only available in week or day view");
        }
    }

    public void next() {
        if (hasWeekView() || hasDayView()) {
            findElement(By.className("v-calendar-next")).click();
        } else {
            throw new IllegalStateException(
                    "Navigation only available in week or day view");
        }
    }

}
