package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        assertFalse("Checkbox is selected even though should be unselected.",
                $(CheckBoxElement.class).first().isChecked());

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

        assertFalse("No week numbers found for date field!",
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

        assertTrue("Week numbers still found in calendar popup!",
                findElements(
                        By.className("v-datefield-calendarpanel-weeknumber"))
                                .isEmpty());
        // Close popup
        popupButton.click();
    }
}
