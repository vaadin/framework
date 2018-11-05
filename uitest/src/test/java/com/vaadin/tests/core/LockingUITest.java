package com.vaadin.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class LockingUITest extends SingleBrowserTest {

    @Test
    public void testLockingTheUIFor4HeartBeats() {
        openTestURL();

        clickButtonAndCheckNotification("check", LockingUI.ALL_OK);
        clickButtonAndCheckNotification("lock", LockingUI.LOCKING_ENDED);
        clickButtonAndCheckNotification("check", LockingUI.ALL_OK);
    }

    private void clickButtonAndCheckNotification(String buttonId, String text) {
        checkNoInitialNotification();

        $(ButtonElement.class).id(buttonId).click();
        testBench().waitForVaadin();

        checkNotification(text);
    }

    private void checkNotification(String text) {
        assertTrue("Notification should be displayed",
                $(NotificationElement.class).exists());

        NotificationElement notification = $(NotificationElement.class).first();
        assertEquals("Unexpected text content in Notification", text,
                notification.getText());
        notification.close();
    }

    private void checkNoInitialNotification() {
        assertFalse("Extra notification displayed",
                $(NotificationElement.class).exists());
    }
}
