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
    public void testFaultyUserInput() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Empty DateField should be ok", "OK",
                $(NotificationElement.class).first().getText());

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now());

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Current date should be ok", "OK",
                $(NotificationElement.class).first().getText());

        dateField.findElement(By.tagName("input")).click();
        new Actions(getDriver()).sendKeys("asd").perform();

        $(ButtonElement.class).first().click();
        Assert.assertEquals("Added 'asd' should make date not parse correctly.",
                "Fail", $(NotificationElement.class).first().getText());
    }

}
