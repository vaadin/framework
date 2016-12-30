package com.vaadin.tests.elements.notification;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NotificationGetTypeAndDescriptionTest extends MultiBrowserTest {
    @Test
    public void testWarning() {
        testNotificationByIndex(0);
    }

    @Test
    public void testError() {
        testNotificationByIndex(1);
    }

    @Test
    public void testHumanized() {
        testNotificationByIndex(2);
    }

    @Test
    public void testTrayNotification() {
        testNotificationByIndex(3);
    }

    // helper method find button by index click and test the notification
    // This method tests caption, description and type of the notification
    private void testNotificationByIndex(int index) {
        openTestURL();
        String id = "button" + index;
        ButtonElement btn = $(ButtonElement.class).id(id);
        btn.click();
        NotificationElement notification = $(NotificationElement.class).get(0);
        String eCaption = NotificationGetTypeAndDescription.captions[index];
        String aCaption = notification.getCaption();
        Assert.assertEquals("Test captions fail", eCaption, aCaption);
        String eDescription = NotificationGetTypeAndDescription.descriptions[index];
        String aDescription = notification.getDescription();
        Assert.assertEquals("Test descriptions fail", eDescription,
                aDescription);
        String eType = NotificationGetTypeAndDescription.type_names[index];
        String aType = notification.getType();
        Assert.assertEquals("Test types fail", eType, aType);

    }
}
