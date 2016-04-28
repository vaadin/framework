package com.vaadin.tests.components.calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CalendarWeekSelectionTest extends MultiBrowserTest {

    @Test
    public void correctYearIsSelected() {
        openTestURL();

        clickOnWeek("1");

        assertThat(getFirstDayOfTheYear().getText(), is("Wednesday 1/1/14"));
    }

    private WebElement getFirstDayOfTheYear() {
        WebElement header = findElement(By.className("v-calendar-header-week"));
        List<WebElement> headerElements = header.findElements(By.tagName("td"));

        // Wednesday is the first day of 2014.
        return headerElements.get(4);
    }

    private void clickOnWeek(String week) {
        for (WebElement e : findElements(By.className("v-calendar-week-number"))) {
            if (e.getText().equals(week)) {
                e.click();
                break;
            }
        }
    }
}