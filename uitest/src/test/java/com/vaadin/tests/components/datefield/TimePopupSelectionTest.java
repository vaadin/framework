package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TimePopupSelectionTest extends MultiBrowserTest {

    @Test
    public void selectDateAndTimeFromPopup() {
        openTestURL();

        DateTimeFieldElement field = $(DateTimeFieldElement.class).first();
        assertEquals("1/13/17 01:00:00 AM", field.getValue());

        field.openPopup();

        List<WebElement> timeSelects = findElement(
                By.className("v-datefield-calendarpanel-time"))
                        .findElements(By.tagName("select"));

        new Select(timeSelects.get(0)).selectByValue("09");
        assertEquals("1/13/17 09:00:00 AM", field.getValue());

        new Select(timeSelects.get(1)).selectByValue("35");
        assertEquals("1/13/17 09:35:00 AM", field.getValue());

        new Select(timeSelects.get(2)).selectByValue("41");
        assertEquals("1/13/17 09:35:41 AM", field.getValue());

        closePopup();

        waitUntil(driver -> getLogRow(0).equals("1. 13/01/2017 09:35:41"));
    }

    private void closePopup() {
        findElement(By.className("v-datefield-calendarpanel"))
                .sendKeys(Keys.ENTER);
    }
}
