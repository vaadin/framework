package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.google.common.base.Joiner;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        assertGreaterOrEqual("Cursor not placed in the end", getCursorPosition(inputElement), inputElement.getText().length());
    }

    private int getCursorPosition(WebElement element) {
        return (int) ((JavascriptExecutor) driver).executeScript(
                Joiner.on("\n").join(
                  "try {",
                  "  var selectRange = document.selection.createRange().duplicate();",
                  "  var elementRange = arguments[0].createTextRange();",
                  "  selectRange.move('character', 0)",
                  "  elementRange.move('character', 0);",
                  "  var inRange1 = selectRange.inRange(elementRange);",
                  "  var inRange2 = elementRange.inRange(selectRange);",
                  "  elementRange.setEndPoint('EndToEnd', selectRange);",
                  "} catch (e) {",
                  "  throw Error('There is no cursor on this page!');",
                  "}",
                  "return String(elementRange.text).replace(/\r/g,' ').length;"),
                element);        
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
