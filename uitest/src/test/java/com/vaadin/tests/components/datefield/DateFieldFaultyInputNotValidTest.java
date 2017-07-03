package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldFaultyInputNotValidTest extends SingleBrowserTest {

    @Test
    public void testEmptyDateFieldOK() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Empty DateField should be ok", "OK",
                $(NotificationElement.class).first().getText());
    }

    @Test
    public void testFaultyUserInput() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Current date should be ok", "OK",
                $(NotificationElement.class).first().getText());
        $(NotificationElement.class).first().close();

        dateField.findElement(By.tagName("input")).click();
        new Actions(getDriver()).sendKeys("asd").perform();

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Added 'asd' should make date not parse correctly.",
                "Fail", $(NotificationElement.class).first().getText());
    }

    @Test
    public void testDateOutOfRange() {
        openTestURL();
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Current date should be ok", "OK",
                $(NotificationElement.class).first().getText());
        $(NotificationElement.class).first().close();

        dateField.setDate(LocalDate.now().minusDays(7));

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Last week should not be ok", "Fail",
                $(NotificationElement.class).first().getText());
    }

    @Test
    public void testParseErrorClearedOnValidInput() {
        testFaultyUserInput();
        $(NotificationElement.class).first().close();

        $(DateFieldElement.class).first().setDate(LocalDate.now());
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Current date should be ok", "OK",
                $(NotificationElement.class).first().getText());
    }
}
