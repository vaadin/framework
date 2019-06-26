package com.vaadin.tests.components.calendar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests opening and closing of calendar notifications.
 *
 * @since
 * @author Vaadin Ltd
 */
public class CalendarNotificationsTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NotificationTestUI.class;
    }

    @Override
    protected DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(
                super.getDesiredCapabilities());
        desiredCapabilities.setCapability("enablePersistentHover", false);
        desiredCapabilities.setCapability("requireWindowFocus", true);

        return desiredCapabilities;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // TODO: IE testing is pending on #14312. For now, IE testing is handled
        // with a logger.
        return getBrowsersExcludingIE();
    }

    @Test
    public void notificationTest() throws Exception {
        openTestURL();

        WebElement day = findElements(By.className("v-calendar-day-number"))
                .get(2);
        // IE8 requires you to click on the text part to fire the event
        new Actions(getDriver()).moveToElement(day, getXOffset(day, 83), getYOffset(day, 11)).click().perform();

        Assert.assertTrue("There should be a notification",
                $(NotificationElement.class).exists());

        // move the mouse around a bit
        new Actions(getDriver()).moveByOffset(5, 5).moveByOffset(100, 100)
                .perform();

        // wait until the notification has animated out
        sleep(1000);

        Assert.assertFalse("There should be no notification on the page",
                $(NotificationElement.class).exists());
    }
}
