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
        WebElement select = driver.findElement(
                By.cssSelector(".v-datefield-popup select:first-child"));
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
