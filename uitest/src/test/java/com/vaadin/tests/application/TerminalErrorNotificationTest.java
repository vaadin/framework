package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TerminalErrorNotificationTest extends MultiBrowserTest {
    @Test
    public void tb2test() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertTrue(isElementPresent(NotificationElement.class));
        assertEquals("Got an exception: You asked for it",
                $(NotificationElement.class).first().getCaption());
    }
}
