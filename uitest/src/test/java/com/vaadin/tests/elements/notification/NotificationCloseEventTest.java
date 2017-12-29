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
        Assert.assertEquals("1. Notification (error) closed by user",
                getLogRow(0));
    }

    @Test
    public void testCloseByServer() {
        openTestURL();
        ButtonElement warning = $(ButtonElement.class).caption("warning")
                .first();
        warning.click();
        ButtonElement close = $(ButtonElement.class)
                .caption("Hide all notifications").first();
        close.click();

        Assert.assertEquals("1. Notification (warning) closed from server",
                getLogRow(0));
    }
}
