package com.vaadin.tests.elements.notification;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NotificationCloseEventTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NotificationGetTypeAndDescription.class;
    }

    @Test
    public void testCloseByUser() {
        openTestURL();
        ButtonElement error = $(ButtonElement.class).caption("error").first();
        error.click();
        $(NotificationElement.class).get(0).close();
        Assert.assertEquals("1. Notification (error) closed", getLogRow(0));
    }

    @Test
    public void notificationsStayAwayAfterRefresh() {
        openTestURL();
        Assert.assertFalse(isElementPresent(NotificationElement.class));
        ButtonElement warning = $(ButtonElement.class).caption("warning")
                .first();
        warning.click();
        Assert.assertTrue("Notification should open",
                isElementPresent(NotificationElement.class));
        openTestURL();
        Assert.assertTrue("Notification should still be present.",
                isElementPresent(NotificationElement.class));
        $(NotificationElement.class).first().close();
        Assert.assertEquals("1. Notification (warning) closed", getLogRow(0));
        Assert.assertFalse("No notification should be present",
                isElementPresent(NotificationElement.class));
        openTestURL();
        Assert.assertFalse("Reloading should not open notifications",
                isElementPresent(NotificationElement.class));
    }
}
