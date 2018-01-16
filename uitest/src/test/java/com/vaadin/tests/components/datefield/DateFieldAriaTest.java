package com.vaadin.tests.components.datefield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldAriaTest extends SingleBrowserTest {

    @Test
    public void changeAssistiveLabel() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.openPopup();
        WebElement prevMonthButton = driver
                .findElement(By.className("v-datefield-popup"))
                .findElement(By.className("v-button-prevmonth"));

        Assert.assertEquals("Previous month",
                prevMonthButton.getAttribute("aria-label"));

        dateField.openPopup();  // This actually closes the calendar popup

        ButtonElement changeLabelsButton = $(ButtonElement.class).first();
        changeLabelsButton.click();

        dateField.openPopup();
        prevMonthButton = driver.findElement(By.className("v-datefield-popup"))
                .findElement(By.className("v-button-prevmonth"));

        Assert.assertEquals("Navigate to previous month",
                prevMonthButton.getAttribute("aria-label"));
    }

    @Test
    public void changeAssistiveLabelInline() {
        openTestURL();

        InlineDateFieldElement dateField = $(InlineDateFieldElement.class)
                .first();
        WebElement nextMonthElement = dateField
                .findElement(By.className("v-button-nextmonth"));

        Assert.assertEquals("Next month",
                nextMonthElement.getAttribute("aria-label"));

        ButtonElement changeLabelsButton = $(ButtonElement.class).first();
        changeLabelsButton.click();

        Assert.assertEquals("Navigate to next month",
                nextMonthElement.getAttribute("aria-label"));
    }
}
