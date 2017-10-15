package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldWeekShortNameTest extends MultiBrowserTest {

    @Test
    public void ar() {
        String[] shortWeekDays = { "س", "ح", "ن", "ث", "ر", "خ", "ج" };
        test(0, 30, shortWeekDays);
    }

    @Test
    public void de() {
        String[] shortWeekDays = { "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };
        test(1, 25, shortWeekDays);
    }

    @Test
    public void en() {
        String[] shortWeekDays = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
                "Sat" };
        test(2, 1, shortWeekDays);
    }

    /**
     * @param index
     *            the index of the {@link DateFieldElement} to test
     * @param firstWeekDay
     *            the day of month of the first day shown in the calendar
     * @param shortWeekDays
     *            the names of the short week days
     */
    private void test(int index, int firstWeekDay, String[] shortWeekDays) {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).get(index);
        dateField.openPopup();

        WebElement weekDaysRow = getDriver().findElement(
                By.className("v-datefield-calendarpanel-weekdays"));
        List<WebElement> weekDays = weekDaysRow
                .findElements(By.tagName("strong"));

        for (int i = 0; i < shortWeekDays.length; i++) {
            assertEquals(shortWeekDays[i], weekDays.get(i + 1).getText());
        }

        WebElement firstWeekDayElement = getDriver().findElement(
                By.className("v-datefield-calendarpanel-day-offmonth"));
        assertEquals(String.valueOf(firstWeekDay),
                firstWeekDayElement.getText());
    }
}
