package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DateFieldValidationErrorTest extends MultiBrowserTest {

    @Test
    public void testComponentErrorShouldBeShownWhenEnteringInvalidDate()
            throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.getInputElement().click();
        dateField.getInputElement().sendKeys("01/01/01", Keys.TAB);

        assertHasErrorMessage(dateField);
    }

    @Test
    public void testComponentErrorShouldBeShownWhenSelectingInvalidDate()
            throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());
        dateField.openPopup();
        waitUntil(ExpectedConditions
                .visibilityOfElementLocated(By.className("v-datefield-popup")));

        WebElement popup = findElement(
                com.vaadin.testbench.By.className("v-datefield-popup"));
        // select day before today
        WebElement popupBody = popup
                .findElement(By.className("v-datefield-calendarpanel"));
        popupBody.sendKeys(Keys.ARROW_LEFT, Keys.ENTER);

        // move focus away otherwise tooltip is not shown
        WebElement inputElement = dateField.getInputElement();
        inputElement.click();
        inputElement.sendKeys(Keys.TAB);

        assertHasErrorMessage(dateField);
    }

    private void assertHasErrorMessage(DateFieldElement dateField) {
        waitForElementPresent(By.className("v-errorindicator"));
        dateField.showTooltip();
        waitUntil(driver -> "Invalid date"
                .equals(getTooltipErrorElement().getText()));
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

}
