package com.vaadin.tests.components.datefield;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldPopupTest extends MultiBrowserTest {

    @Test
    public void testOpenCloseOpen_popupShouldBeOpen() throws Exception {
        openTestURL();

        WebElement toggleButton = $(DateTimeFieldElement.class).first()
                .findElement(By.className("v-datefield-button"));

        toggleButton.click();

        assertThatPopupIsVisible();

        toggleButton.click();

        assertThatPopupIsInvisible();

        // We should be able to immediately open the popup from the popup after
        // clicking the button to close it. (#8446)
        toggleButton.click();

        assertThatPopupIsVisible();
    }

    private void assertThatPopupIsVisible() {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(
                org.openqa.selenium.By.className("v-datefield-popup")));
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
                    return !(findElement(org.openqa.selenium.By
                            .className("v-datefield-popup")).isDisplayed());
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
}
