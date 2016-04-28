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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupClosingWithEscTest extends MultiBrowserTest {

    @Test
    public void testPopupClosingFromTimeSelect() {
        openTestURL();

        openPopup("minute");
        assertTrue(isPopupVisible());

        // Send ESC to the select element to simulate user being
        // focused on the select while hitting the ESC key.
        WebElement select = driver.findElement(By
                .cssSelector(".v-datefield-popup select:first-child"));
        select.sendKeys(Keys.ESCAPE);
        assertFalse(isPopupVisible());
    }

    @Test
    public void testPopupClosingDayResolution() {
        testPopupClosing("day");
    }

    @Test
    public void testPopupClosingMonthResolution() {
        testPopupClosing("month");
    }

    @Test
    public void testPopupClosingYearResolution() {
        testPopupClosing("year");
    }

    private void testPopupClosing(String dateFieldId) {
        openTestURL();

        openPopup(dateFieldId);
        assertTrue(isPopupVisible());
        sendEscToCalendarPanel();
        assertFalse(isPopupVisible());
    }

    private void openPopup(String dateFieldId) {
        driver.findElement(
                vaadinLocator("PID_S" + dateFieldId + "#popupButton")).click();
    }

    private boolean isPopupVisible() {
        return !(driver.findElements(By.cssSelector(".v-datefield-popup"))
                .isEmpty());
    }

    private void sendEscToCalendarPanel() {
        driver.findElement(By.cssSelector(".v-datefield-calendarpanel"))
                .sendKeys(Keys.ESCAPE);
    }

}
