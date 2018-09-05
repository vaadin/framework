package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateFieldChangeResolution.BUTTON_BASE_ID;
import static com.vaadin.tests.components.datefield.DateFieldChangeResolution.DATEFIELD_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldChangeResolutionTest extends MultiBrowserTest {

    private WebElement dateFieldButton, textField;
    private WebElement resolutionSecond, resolutionMinute, resolutionHour,
            resolutionDay, resolutionMonth, resolutionYear;

    @Test
    public void changeResolutionBetweenYearAndMonth() throws Exception {
        initialize();
        click(resolutionMonth);
        checkHeaderAndBody(DateTimeResolution.MONTH, true);
        click(resolutionYear);
        checkHeaderAndBody(DateTimeResolution.YEAR, true);
    }

    @Test
    public void changeResolutionBetweenYearAndSecond() throws Exception {
        initialize();
        click(resolutionSecond);
        checkHeaderAndBody(DateTimeResolution.SECOND, true);
        click(resolutionYear);
        checkHeaderAndBody(DateTimeResolution.YEAR, true);
    }

    @Test
    public void changeResolutionToDayThenMonth() throws Exception {
        initialize();
        // check the initial state
        checkHeaderAndBody(DateTimeResolution.YEAR, true);
        click(resolutionDay);
        checkHeaderAndBody(DateTimeResolution.DAY, true);
        click(resolutionMonth);
        checkHeaderAndBody(DateTimeResolution.MONTH, true);
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
        checkHeaderAndBody(DateTimeResolution.HOUR, false);
        click(resolutionYear);
        checkHeaderAndBody(DateTimeResolution.YEAR, false);
        click(resolutionMinute);
        checkHeaderAndBody(DateTimeResolution.MINUTE, false);
    }

    private void initialize() {
        openTestURL();
        WebElement dateField = driver.findElement(By.id(DATEFIELD_ID));
        dateFieldButton = dateField
                .findElement(By.className("v-datefield-button"));
        textField = dateField
                .findElement(By.className("v-datefield-textfield"));
        resolutionSecond = driver.findElement(By.id(BUTTON_BASE_ID + "second"));
        resolutionMinute = driver.findElement(By.id(BUTTON_BASE_ID + "minute"));
        resolutionHour = driver.findElement(By.id(BUTTON_BASE_ID + "hour"));
        resolutionDay = driver.findElement(By.id(BUTTON_BASE_ID + "day"));
        resolutionMonth = driver.findElement(By.id(BUTTON_BASE_ID + "month"));
        resolutionYear = driver.findElement(By.id(BUTTON_BASE_ID + "year"));
    }

    private void checkHeaderAndBody(DateTimeResolution resolution,
            boolean textFieldIsEmpty) throws Exception {
        // Popup date field has all kinds of strange timers on the
        // client side
        sleep(100);
        // Open the popup calendar, perform checks and close the popup.
        openPopupDateField();
        if (resolution.compareTo(DateTimeResolution.MONTH) <= 0) {
            checkMonthHeader();
        } else {
            checkYearHeader();
        }
        if (resolution.compareTo(DateTimeResolution.DAY) <= 0) {
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
                    isElementPresent(By.cssSelector(
                            ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevmonth .v-button-prevmonth")));
            assertTrue(
                    "The calendar should have a button for switching to the next month",
                    isElementPresent(By.cssSelector(
                            ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextmonth .v-button-nextmonth")));
        } else {
            assertFalse(
                    "The calendar should not have a button for switching to the previous month",
                    isElementPresent(By.cssSelector(
                            ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevmonth .v-button-prevmonth")));
            assertFalse(
                    "The calendar should not have a button for switching to the next month",
                    isElementPresent(By.cssSelector(
                            ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextmonth .v-button-nextmonth")));
        }
    }

    private void checkHeaderForYear() {
        assertTrue(
                "The calendar should have a button for switching to the previous year",
                isElementPresent(By.cssSelector(
                        ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-prevyear .v-button-prevyear")));
        assertTrue("The calendar header should show the selected year",
                isElementPresent(By.cssSelector(
                        ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-month")));
        assertTrue(
                "The calendar should have a button for switching to the next year",
                isElementPresent(By.cssSelector(
                        ".v-datefield-calendarpanel-header .v-datefield-calendarpanel-nextyear .v-button-nextyear")));

    }

    private void click(WebElement element) {
        testBenchElement(element).click(5, 5);
    }

    private void openPopupDateField() {
        click(dateFieldButton);
    }

    private void closePopupDateField() {
        WebElement element = driver
                .findElement(By.cssSelector(".v-datefield-calendarpanel"));
        element.sendKeys(Keys.ESCAPE);
    }
}
