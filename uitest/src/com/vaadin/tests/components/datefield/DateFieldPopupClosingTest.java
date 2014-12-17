package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldPopupClosingTest extends MultiBrowserTest {

    @Test
    public void testDateFieldPopupClosingLongClick()
            throws InterruptedException, IOException {
        openTestURL();

        fastClickDateDatePickerButton();

        assertThatPopupIsVisible();

        longClickDateDatePickerButton();

        assertThatPopupIsInvisible();
    }

    private void assertThatPopupIsVisible() {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By
                .className("v-datefield-popup")));
    }

    private void assertThatPopupIsInvisible() {
        // ExpectedConditions.invisibilityOfElementLocated doesn't work
        // with PhantomJS when running with a hub:
        // https://code.google.com/p/selenium/issues/detail?id=5000
        // so we need to make our own.

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                try {
                    return !(findElement(By.className("v-datefield-popup"))
                            .isDisplayed());
                } catch (Exception e) {
                    return true;
                }
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "popup to not be visible";
            }
        });
    }

    private void longClickDateDatePickerButton() {
        WebElement button = getToggleButton();

        new Actions(getDriver()).clickAndHold(button).perform();
        assertThatPopupIsInvisible();

        new Actions(getDriver()).release(button).perform();
    }

    private WebElement getToggleButton() {
        DateFieldElement dateField = $(DateFieldElement.class).first();

        return dateField.findElement(By.tagName("button"));
    }

    private void fastClickDateDatePickerButton() {
        getToggleButton().click();
    }

}