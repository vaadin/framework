package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldEventOrderTest extends SingleBrowserTest {

    @Test
    public void testEventOrderIsCorrect() {
        openTestURL();

        DateTimeFieldElement field = $(DateTimeFieldElement.class).first();

        field.findElement(By.className("v-datefield-button")).click();

        List<WebElement> timeSelects = findElement(
                By.className("v-datefield-calendarpanel-time"))
                        .findElements(By.tagName("select"));

        Select select = new Select(timeSelects.get(0));
        select.selectByValue("09");
        // selecting is flaky, try index selection too to ensure selection
        // actually happens
        select.selectByIndex(9);

        findElement(By.id("test-button")).click();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver arg0) {
                return !" ".equals(getLogRow(1));
            }

            @Override
            public String toString() {
                // waiting for ...
                return "log row 1 to get content";
            }
        });

        assertEquals("The button click event should come second.",
                "2. Button Click Event", getLogRow(0));
        assertEquals("The value change event of DTF should come firstly.",
                "1. DateTimeField value change event", getLogRow(1));

    }
}
