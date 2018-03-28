package com.vaadin.tests.extensions;

import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NotificationCloseListenerTest extends MultiBrowserTest {

    @Test
    public void closeListenerCalled() throws Exception {
        openTestURL();

        $(NotificationElement.class).first().close();

        waitUntil(input -> {
            try {
                return $(CheckBoxElement.class).first().isChecked();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
