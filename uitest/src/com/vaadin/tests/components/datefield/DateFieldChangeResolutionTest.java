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

import static com.vaadin.tests.components.datefield.DateFieldChangeResolution.BUTTON_BASE_ID;
import static com.vaadin.tests.components.datefield.DateFieldChangeResolution.DATEFIELD_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldChangeResolutionTest extends MultiBrowserTest {

    private WebElement dateFieldButton, textField;
    private WebElement resolutionSecond, resolutionMinute, resolutionHour,
            resolutionDay, resolutionMonth, resolutionYear;

    @Test
    public void changeResolutionBetweenYearAndMonth() throws Exception {
        initialize();
        click(resolutionMonth);
        checkHeaderAndBody(Resolution.MONTH, true);
        click(resolutionYear);
        checkHeaderAndBody(Resolution.YEAR, true);
    }

    @Test
    public void changeResolutionBetweenYearAndSecond() throws Exception {
        initialize();
        click(resolutionSecond);
        checkHeaderAndBody(Resolution.SECOND, true);
        click(resolutionYear);
        checkHeaderAndBody(Resolution.YEAR, true);
    }

    @Test
    public void changeResolutionToDayThenMonth() throws Exception {
        initialize();
        checkHeaderAndBody(Resolution.YEAR, true); // check the initial state
        click(resolutionDay);
        checkHeaderAndBody(Resolution.DAY, true);
        click(resolutionMonth);
        checkHeaderAndBody(Resolution.MONTH, true);
    }

    @Test
    public void setDateAndChangeResolution() throws Exception {
        initialize();
        // Set the date to previous month.
        click(resolutionMonth);
        openPopupDateField();
        click(driver.findElement(By.className("v-button-prevmonth")));
        closePopupDateField();
        assertFalse(
                "The text field of the calendar should not be empty after selecting a date",
                textField.getAttribute("value").isEmpty());
        // Change resolutions and check that the selected date is not lost and
        // that the calendar has the correct resolution.
        click(resolutionHour);
        checkHeaderAndBody(Resolution.HOUR, false);
        click(resolutionYear);
        checkHeaderAndBody(Resolution.YEAR, false);
        click(resolutionMinute);
        checkHeaderAndBody(Resolution.MINUTE, false);
    }

    private void initialize() {
        openTestURL();
        WebElement dateField = driver.findElement(By.id(DATEFIELD_ID));
        dateFieldButton = dateField.findElement(By
                .className("v-datefield-button"));
        textField = dateField
                .findElement(By.className("v-datefield-textfield"));
        resolutionSecond = driver.findElement(By.id(BUTTON_BASE_ID + "second"));
        resolutionMinute = driver.findElement(By.id(BUTTON_BASE_ID + "minute"));
        resolutionHour = driver.findElement(By.id(BUTTON_BASE_ID + "hour"));
        resolutionDay = driver.findElement(By.id(BUTTON_BASE_ID + "day"));
        resolutionMonth = driver.findElement(By.id(BUTTON_BASE_ID + "month"));
        resolutionYear = driver.findElement(By.id(BUTTON_BASE_ID + "year"));
    }

    private void checkHeaderAndBody(Resolution resolution,
            boolean textFieldIsEmpty) throws Exception {
        // Popup date field has all kinds of strange timers on the
        // client side
        sleep(100);
        // Open the popup calendar, perform checks and close the popup.
        openPopupDateField();
        if (resolution.getCalendarField() >= Resolution.MONTH
                .getCalendarField()) {
            checkMonthHeader();
        } else {
            checkYearHeader();
        }
        if (resolution.getCalendarField() >= Resolution.DAY.getCalendarField()) {
            assertTrue(
                    "A calendar with the chosen resolution should have a body",
                    calendarHasBody());
        } else {
            assertFalse(
                    "A calendar with the chosen resolution should not have a body",
                    calendarHasBody());
        }
        if (textFieldIsEmpty) {
            assertTrue("The text field of the calendar should be empty",
                    textField.getAttribute("value").isEmpty());
        } else {
            assertFalse("The text field of the calendar should not be empty",
                    textField.getAttribute("value").isEmpty());
        }
        closePopupDateField();
    }

    private void checkMonthHeader() {
        checkHeaderForYear();
        checkHeaderForMonth(true);
    }

    private void checkYearHeader() {
        checkHeaderForYear();
        checkHeaderForMonth(false);
    }

    private boolean calendarHasBody() {
        return isElementPresent(By.className("v-datefield-calendarpanel-body"));
    }

    private void checkHeaderForMonth(boolean buttonsExpected) {
        // If buttonsExpected is true, check that there are buttons for changing
        // the month. Otherwise check that there are no such buttons.
        if (buttonsExpected) {
            assertTrue(
                    "The calendar should have a button for switching to the previous month",
                    isElementPresent(By
                            .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevmonth .v-button-prevmonth")));
            assertTrue(
                    "The calendar should have a button for switching to the next month",
                    isElementPresent(By
                            .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextmonth .v-button-nextmonth")));
        } else {
            assertFalse(
                    "The calendar should not have a button for switching to the previous month",
                    isElementPresent(By
                            .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevmonth .v-button-prevmonth")));
            assertFalse(
                    "The calendar should not have a button for switching to the next month",
                    isElementPresent(By
                            .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextmonth .v-button-nextmonth")));
        }
    }

    private void checkHeaderForYear() {
        assertTrue(
                "The calendar should have a button for switching to the previous year",
                isElementPresent(By
                        .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevyear .v-button-prevyear")));
        assertTrue(
                "The calendar header should show the selected year",
                isElementPresent(By
                        .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-month")));
        assertTrue(
                "The calendar should have a button for switching to the next year",
                isElementPresent(By
                        .cssSelector(".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextyear .v-button-nextyear")));

    }

    private void click(WebElement element) {
        testBenchElement(element).click(5, 5);
    }

    private void openPopupDateField() {
        click(dateFieldButton);
    }

    private void closePopupDateField() {
        WebElement element = driver.findElement(By
                .cssSelector(".v-datefield-calendarpanel"));
        element.sendKeys(Keys.ESCAPE);
    }
}
