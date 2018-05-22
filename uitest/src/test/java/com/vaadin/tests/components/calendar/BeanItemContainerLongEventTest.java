package com.vaadin.tests.components.calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests if long event which began before the view period is shown (#15242)
 */
public class BeanItemContainerLongEventTest extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/run/BeanItemContainerTestUI?restartApplication";
    }

    @Override
    protected void openTestURL(String... parameters) {
        driver.get(getTestUrl());
    }

    @Test
    public void testEventDisplayedInWeekView() {
        openTestURL();
        WebElement target = driver
                .findElements(By.className("v-calendar-week-number")).get(1);
        target.click();
        target = driver.findElement(By.className("v-calendar-event"));
        Assert.assertEquals("Wrong event name", "Long event", target.getText());
    }

    @Test
    public void testEventDisplayedInDayView() {
        openTestURL();
        WebElement target = driver
                .findElements(By.className("v-calendar-day-number")).get(5);
        target.click();
        target = driver.findElement(By.className("v-calendar-event"));
        Assert.assertEquals("Wrong event name", "Long event", target.getText());
    }

}
