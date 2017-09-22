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
package com.vaadin.tests.components.datefield;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TimePopupSelectionTest extends MultiBrowserTest {

    @Test
    public void selectDateAndTimeFromPopup() {
        openTestURL();

        DateTimeFieldElement field = $(DateTimeFieldElement.class).first();
        Assert.assertEquals("1/13/17 01:00:00 AM", field.getValue());

        field.openPopup();

        List<WebElement> timeSelects = findElement(
                By.className("v-datefield-calendarpanel-time"))
                        .findElements(By.tagName("select"));

        new Select(timeSelects.get(0)).selectByValue("09");
        Assert.assertEquals("1/13/17 09:00:00 AM", field.getValue());

        new Select(timeSelects.get(1)).selectByValue("35");
        Assert.assertEquals("1/13/17 09:35:00 AM", field.getValue());

        new Select(timeSelects.get(2)).selectByValue("41");
        Assert.assertEquals("1/13/17 09:35:41 AM", field.getValue());

        closePopup();

        waitUntil(driver -> getLogRow(0).equals("1. 13/01/2017 09:35:41"));
    }

    private void closePopup() {
        findElement(By.className("v-datefield-calendarpanel"))
                .sendKeys(Keys.ENTER);
    }
}
