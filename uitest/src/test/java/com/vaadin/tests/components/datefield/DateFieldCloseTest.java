package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateFieldClose.DATEFIELD_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldCloseTest extends MultiBrowserTest {
    private WebElement dateField;

    @Test
    public void closeByClickingCalendarButton() throws Exception {
        openTestURL();
        dateField = driver.findElement(By.id(DATEFIELD_ID));
        clickButton();
        checkForCalendarHeader(true);
        closePopup();
        checkForCalendarHeader(false);
    }

    private void checkForCalendarHeader(boolean headerShouldExist) {
        boolean headerExists = isElementPresent(
                By.className("v-datefield-calendarpanel-header"));
        if (headerShouldExist) {
            assertTrue("The calendar should be visible", headerExists);
        } else {
            assertFalse("The calendar should not be visible", headerExists);
        }
    }

    private void clickButton() {
        WebElement dateFieldButton = dateField
                .findElement(By.className("v-datefield-button"));
        testBenchElement(dateFieldButton).click(5, 5);
    }

    private void closePopup() {
        WebElement dateFieldButton = dateField
                .findElement(By.className("v-datefield-button"));
        // To work reliably with IE, need to click and hold instead of just
        // clicking the button.
        Actions actions = new Actions(driver);
        actions.clickAndHold(dateFieldButton).perform();
    }
}
