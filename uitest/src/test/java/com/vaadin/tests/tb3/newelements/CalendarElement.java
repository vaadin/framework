package com.vaadin.tests.tb3.newelements;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Calendar")
public class CalendarElement extends
        com.vaadin.testbench.elements.CalendarElement {
    public List<WebElement> getWeekNumbers() {
        return findElements(By.className("v-calendar-week-number"));
    }

    public boolean hasMonthView() {
        return isElementPresent(By.className("v-calendar-week-numbers"));
    }

    public boolean hasWeekView() {
        return isElementPresent(By.className("v-calendar-header-week"));
    }

    public List<WebElement> getDayNumbers() {
        return findElements(By.className("v-calendar-day-number"));
    }

    public List<WebElement> getMonthDays() {
        return findElements(By.className("v-calendar-month-day"));
    }

    public boolean hasDayView() {
        return getDayHeaders().size() == 1;
    }

    public List<WebElement> getDayHeaders() {
        return findElements(By.className("v-calendar-header-day"));
    }

    public void back() {
        findElement(By.className("v-calendar-back")).click();
    }

    public void next() {
        findElement(By.className("v-calendar-next")).click();
    }
}
