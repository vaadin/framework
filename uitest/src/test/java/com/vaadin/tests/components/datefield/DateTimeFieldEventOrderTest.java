package com.vaadin.tests.components.datefield;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

public class DateTimeFieldEventOrderTest extends SingleBrowserTest {

    @Test
    public void testEventOrderIsCorrect() {
        openTestURL();

        DateTimeFieldElement field = $(DateTimeFieldElement.class).first();

        field.openPopup();

        List<WebElement> timeSelects = findElement(
                By.className("v-datefield-calendarpanel-time"))
                        .findElements(By.tagName("select"));

        new Select(timeSelects.get(0)).selectByValue("09");

        findElement(By.id("test-button")).click();

        assertEquals("The button click event should come second.",
                "2. Button Click Event", getLogRow(0));
        assertEquals("The value change event of DTF should come firstly.",
                "1. DateTimeField value change event", getLogRow(1));

    }
}
