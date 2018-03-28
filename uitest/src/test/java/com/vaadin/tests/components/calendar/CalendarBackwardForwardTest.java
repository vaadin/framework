package com.vaadin.tests.components.calendar;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests: Vaadin Calendar: Navigation to invisible days of week (#12243)
 *
 * @author Vaadin Ltd
 */
public class CalendarBackwardForwardTest extends MultiBrowserTest {

    @Test
    public void testCalendar() throws InterruptedException, IOException {
        openTestURL();

        openWeekView();
        openDayView();
        clickCalendarNext();

        WebElement headerDayElement = getDriver()
                .findElement(By.className("v-calendar-header-day"));

        assertThat("This day should be Monday 9/9/13",
                headerDayElement.getText().equals("Monday 9/9/13"));

        for (int i = 0; i < 6; i++) {
            clickCalendarBack();
        }

        headerDayElement = getDriver()
                .findElement(By.className("v-calendar-header-day"));

        assertThat("This day should be Friday 8/30/13",
                headerDayElement.getText().equals("Friday 8/30/13"));
    }

    private void openWeekView() {
        List<WebElement> elements = getDriver()
                .findElements(By.className("v-calendar-week-number"));

        for (WebElement webElement : elements) {
            if (webElement.getText().equals("36")) {
                webElement.click();
                break;
            }
        }
    }

    private void openDayView() {
        List<WebElement> elements = getDriver()
                .findElements(By.className("v-calendar-header-day"));

        for (WebElement webElement : elements) {
            if (webElement.getText().contains("Friday 9/6/13")) {
                webElement.click();
                break;
            }
        }
    }

    private void clickCalendarNext() {
        List<WebElement> elements = getDriver()
                .findElements(By.className("v-calendar-next"));

        elements.get(0).click();
    }

    private void clickCalendarBack() {
        List<WebElement> elements = getDriver()
                .findElements(By.className("v-calendar-back"));

        elements.get(0).click();
    }

}
