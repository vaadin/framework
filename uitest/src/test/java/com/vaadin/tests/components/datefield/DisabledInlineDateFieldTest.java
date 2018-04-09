package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledInlineDateFieldTest extends MultiBrowserTest {

    @Test
    public void testDisabled() {
        openTestURL();
        testNextMonthControls(".v-disabled");
        testDaySelection(".v-disabled");
    }

    @Test
    public void testReadOnly() {
        openTestURL();
        testNextMonthControls(".v-readonly");
        testDaySelection(".v-readonly");
    }

    private void testNextMonthControls(String cssClass) {
        // Get the currently selected month.
        String expectedMonth = getSelectedMonth(cssClass);

        // Attempt to click the next month button.
        driver.findElement(By.cssSelector(cssClass + " .v-button-nextmonth"))
                .click();

        // Assert that we did not navigate to next month.
        String actualMonth = getSelectedMonth(cssClass);
        assertEquals(expectedMonth, actualMonth);
    }

    private void testDaySelection(String cssClass) {
        // We know that the first day element is not selected, because of the
        // fixed date in the test.
        WebElement nonSelectedDay = driver.findElement(By.cssSelector(
                cssClass + " .v-inline-datefield-calendarpanel-day"));

        // Assert it is not selected before click.
        assertFalse(nonSelectedDay.getAttribute("class").contains("selected"));

        // Click on the non-selected day.
        nonSelectedDay.click();

        // Assert that clicking did not select the day.
        assertFalse(nonSelectedDay.getAttribute("class").contains("selected"));
    }

    private String getSelectedMonth(String selectorPrefix) {
        return driver
                .findElement(By.cssSelector(selectorPrefix
                        + " .v-inline-datefield-calendarpanel-month"))
                .getText();
    }

}
