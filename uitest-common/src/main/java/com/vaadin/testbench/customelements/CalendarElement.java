package com.vaadin.testbench.customelements;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.Calendar")
public class CalendarElement
        extends com.vaadin.testbench.elements.CalendarElement {
    @Override
    public List<WebElement> getWeekNumbers() {
        return findElements(By.className("v-calendar-week-number"));
    }

    @Override
    public boolean hasMonthView() {
        return isElementPresent(By.className("v-calendar-week-numbers"));
    }

    @Override
    public boolean hasWeekView() {
        return isElementPresent(By.className("v-calendar-header-week"));
    }

    @Override
    public List<WebElement> getDayNumbers() {
        return findElements(By.className("v-calendar-day-number"));
    }

    @Override
    public List<WebElement> getMonthDays() {
        return findElements(By.className("v-calendar-month-day"));
    }

    @Override
    public boolean hasDayView() {
        return getDayHeaders().size() == 1;
    }

    @Override
    public List<WebElement> getDayHeaders() {
        return findElements(By.className("v-calendar-header-day"));
    }

    @Override
    public void back() {
        findElement(By.className("v-calendar-back")).click();
    }

    @Override
    public void next() {
        findElement(By.className("v-calendar-next")).click();
    }
}
