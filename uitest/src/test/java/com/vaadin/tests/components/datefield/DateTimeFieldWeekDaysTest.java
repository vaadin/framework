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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldWeekDaysTest extends SingleBrowserTest {

    @Test
    public void testFiLocale_weekNumbersVisible() {
        openTestURL();

        openPopupAndValidateWeekNumbers();
    }

    @Test
    public void testToggleWeekNumbers_renderedCorrectly() {
        openTestURL();

        openPopupAndValidateWeekNumbers();

        $(CheckBoxElement.class).first().click();

        Assert.assertFalse("Checkbox is selected even though should be unselected.", $(CheckBoxElement.class).first().isChecked());

        openPopupAndValidateNoWeeknumbers();
    }

    @Test
    public void testLocaleChangeToEnglish_removesWeekNumbers() {
        openTestURL();

        openPopupAndValidateWeekNumbers();

        $(ButtonElement.class).id("english").click();

        openPopupAndValidateNoWeeknumbers();
    }

    @Test
    public void testChangeBackToFinnish_weekNumbersVisible() {
        openTestURL();

        $(ButtonElement.class).id("english").click();

        openPopupAndValidateNoWeeknumbers();

        $(ButtonElement.class).id("finnish").click();

        openPopupAndValidateWeekNumbers();
    }

    private void openPopupAndValidateWeekNumbers() {
        WebElement popupButton = $(DateTimeFieldElement.class).first()
                .findElement(By.className("v-datefield-button"));
        // Open date popup
        popupButton.click();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(
                org.openqa.selenium.By.className("v-datefield-popup")));

        Assert.assertFalse("No week numbers found for date field!",
                findElements(
                        By.className("v-datefield-calendarpanel-weeknumber"))
                        .isEmpty());
        // Close popup
        popupButton.click();
    }

    private void openPopupAndValidateNoWeeknumbers() {
        WebElement popupButton = $(DateTimeFieldElement.class).first()
                .findElement(By.className("v-datefield-button"));
        // Open date popup
        popupButton.click();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(
                org.openqa.selenium.By.className("v-datefield-popup")));

        Assert.assertTrue("Week numbers still found in calendar popup!",
                findElements(
                        By.className("v-datefield-calendarpanel-weeknumber"))
                        .isEmpty());
        // Close popup
        popupButton.click();
    }
}
