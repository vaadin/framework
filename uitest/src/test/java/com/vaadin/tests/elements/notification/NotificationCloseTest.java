package com.vaadin.tests.elements.notification;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NotificationCloseTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NotificationGetTypeAndDescription.class;
    }

    @Test
    public void testWarning() {
        testClose(0);
    }

    @Test
    public void testError() {
        testClose(1);
    }

    @Test
    public void testHumanized() {
        testClose(2);
    }

    @Test
    public void testTrayNotification() {
        testClose(3);
    }

    private void testClose(int index) {
        openTestURL();
        String id = "button" + index;
        ButtonElement btn = $(ButtonElement.class).id(id);
        // show notification
        btn.click();
        $(NotificationElement.class).get(0).close();
        List<NotificationElement> notifications = $(NotificationElement.class)
                .all();
        // check that all notifications are closed
        Assert.assertTrue("There are open notifications",
                notifications.isEmpty());
    }
}
