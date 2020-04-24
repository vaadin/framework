package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CloseWindowAsyncTest extends MultiBrowserTest {

    @Test
    public void testOpeningAndClosing() throws Exception {
        openTestURL();
        List<ButtonElement> buttons = $(ButtonElement.class).all();
        int index = 1;
        for (ButtonElement button : buttons) {
            button.click();
            List<NotificationElement> notifications = $(
                    NotificationElement.class).all();
            if (!notifications.isEmpty()) {
                notifications.get(0).close();
            }
            assertEquals("Unexpected log contents,",
                    index + ". closed " + index, getLogRow(0));
            ++index;
        }
    }
}
