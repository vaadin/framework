package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldNavigationKeyBoardTest extends MultiBrowserTest {
    @Test
    public void testNavigation() {
        openTestURL();
        // Opening pop-up
        findElement(By.className("v-datefield-button")).click();
        waitForElementVisible(By.className("v-datefield-calendarpanel"));
        waitForElementPresent(
                By.className("v-datefield-calendarpanel-day-focused"));
        // Focused element in the calendarPanel
        WebElement focused = findElement(
                By.className("v-datefield-calendarpanel-day-focused"));
        // Value in it
        String dayValue = focused.getText();
        findElement(By.className("v-datefield-calendarpanel"))
                .sendKeys(Keys.ARROW_LEFT);
        assertNotEquals(dayValue,
                findElement(
                        By.className("v-datefield-calendarpanel-day-focused"))
                                .getText());
    }
}
