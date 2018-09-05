package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupTimeClosingWithEscTest extends MultiBrowserTest {

    @Test
    public void testPopupClosing() {
        openTestURL();

        testPopupClosing("second");
        testPopupClosing("minute");
        testPopupClosing("hour");
        testPopupClosing("month");
    }

    private void testPopupClosing(String dateFieldId) {
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
