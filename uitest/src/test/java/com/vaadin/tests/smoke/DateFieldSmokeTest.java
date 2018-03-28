package com.vaadin.tests.smoke;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DateFieldSmokeTest extends MultiBrowserTest {

    @Test
    public void dateFieldsSmokeTest() {
        openTestURL();

        PopupDateFieldElement popup = $(PopupDateFieldElement.class).first();
        assertEquals("12/28/16", popup.getValue());
        InlineDateFieldElement inline = $(InlineDateFieldElement.class).first();
        assertEquals(String.valueOf(29),
                inline.findElement(By.className(
                        "v-inline-datefield-calendarpanel-day-selected"))
                        .getText());

        popup.findElement(By.tagName("button")).click();
        waitUntil(ExpectedConditions
                .visibilityOfElementLocated(By.className("v-datefield-popup")));
        selectDay(findElement(By.className("v-datefield-popup")), 14, "v-");

        waitUntil(driver -> "1. Popup value is : 2016.12.14"
                .equals(getLogRow(0)));

        selectDay(inline, 13, "v-inline-");
        waitUntil(driver -> "2. Inline value is : 2016.12.13"
                .equals(getLogRow(0)));

        inline.findElement(By.className("v-button-prevmonth")).click();
        WebElement monthTitle = inline.findElement(
                By.className("v-inline-datefield-calendarpanel-month"));
        assertEquals("November 2016", monthTitle.getText());

        inline.findElement(By.className("v-button-nextyear")).click();
        monthTitle = inline.findElement(
                By.className("v-inline-datefield-calendarpanel-month"));
        assertEquals("November 2017", monthTitle.getText());
    }

    private void selectDay(WebElement calendar, int day, String cssPrefix) {
        List<WebElement> days = calendar.findElements(
                By.className(cssPrefix + "datefield-calendarpanel-day"));
        String dayValue = String.valueOf(day);
        days.stream()
                .filter(dayElement -> dayElement.getText().equals(dayValue))
                .findFirst().get().click();
    }
}
