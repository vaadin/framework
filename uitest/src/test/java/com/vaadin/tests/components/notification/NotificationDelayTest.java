package com.vaadin.tests.components.notification;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check notification delay.
 *
 * @author Vaadin Ltd
 */
public class NotificationDelayTest extends MultiBrowserTest {

    @Test
    public void testDelay() throws InterruptedException {
        openTestURL();

        assertTrue("No notification found", hasNotification());

        waitUntil(input -> {
            new Actions(getDriver()).moveByOffset(10, 10).perform();

            return !hasNotification();
        });
    }

    private boolean hasNotification() {
        return isElementPresent(By.className("v-Notification"));
    }

}
