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

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class LocaleChangeTest extends MultiBrowserTest {

    @Test
    public void testLocaleChange() {
        openTestURL();

        // Check the initial value and that popup can be opened.
        assertEquals("22/05/14 20:00:00", getDateValue());
        toggleDatePopup();
        assertPopupOpen(true);

        // Close the popup and change the locale.
        toggleDatePopupWorkaroundClosePopupIE();
        assertPopupOpen(false);
        driver.findElement(By.className("v-button")).click(); // Locale change.

        // Check that the value has changed and the popup can still be opened
        // without problems.
        assertEquals("5/22/14 08:00:00 PM", getDateValue());
        toggleDatePopup();
        assertPopupOpen(true);
    }

    private void assertPopupOpen(boolean open) {
        assertEquals("Date popup was not " + (open ? "open" : "closed") + ".",
                (open ? 1 : 0),
                driver.findElements(By.className("v-datefield-popup")).size());
    }

    private void toggleDatePopup() {
        driver.findElement(By.className("v-datefield-button")).click();
    }

    /*
     * Work around bug reported in ticket #14086. Delete this method once fixed
     * andd use toggleDatePopup() instead.
     */
    private void toggleDatePopupWorkaroundClosePopupIE() {
        if (!BrowserUtil.isIE(getDesiredCapabilities())) {
            driver.findElement(By.className("v-datefield-button")).click();
        } else {
            boolean popupOpen = driver.findElements(
                    By.className("v-datefield-popup")).size() == 1;
            if (popupOpen) {
                driver.findElement(
                        By.className("v-datefield-calendarpanel-day-selected"))
                        .click();
            } else {
                driver.findElement(By.className("v-datefield-button")).click();
            }
        }
    }

    private String getDateValue() {
        return driver.findElement(By.className("v-datefield-textfield"))
                .getAttribute("value");
    }
}
