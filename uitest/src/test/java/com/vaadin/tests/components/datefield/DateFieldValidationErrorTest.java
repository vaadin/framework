package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.gargoylesoftware.htmlunit.javascript.host.geo.Coordinates;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DateFieldValidationErrorTest extends MultiBrowserTest {

    @Test
    public void testComponentErrorShouldBeShownWhenEnteringInvalidDate() throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.getInputElement().sendKeys("01/01/01", Keys.TAB);

        assertHasErrorMessage(dateField);
    }

    @Test
    public void testComponentErrorShouldBeShownWhenSelectingInvalidDate() throws InterruptedException {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());
        dateField.openPopup();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("v-datefield-popup")));

        WebElement popup = findElement(com.vaadin.testbench.By.className("v-datefield-popup"));
        // select day before today
        WebElement popupBody = popup.findElement(By.className("v-datefield-calendarpanel"));
        popupBody.sendKeys(Keys.ARROW_LEFT, Keys.ENTER);

        // move focus away otherwise tooltip is not shown
        dateField.getInputElement().sendKeys(Keys.TAB);

        assertHasErrorMessage(dateField);
    }

    private void assertHasErrorMessage(DateFieldElement dateField) {
        waitForElementPresent(By.className("v-errorindicator"));
        dateField.showTooltip();

        // Should wait some additional time otherwise sometimes
        // in case of date selection tooltip does is not showns
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) { }

        WebElement errorMessage = findElement(By.cssSelector(".v-errormessage .gwt-HTML"));
        Assert.assertEquals("Error message must be present", "Invalid date", errorMessage.getText());
    }

}
