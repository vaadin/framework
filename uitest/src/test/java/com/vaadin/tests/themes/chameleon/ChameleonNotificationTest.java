package com.vaadin.tests.themes.chameleon;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ChameleonNotificationTest extends MultiBrowserTest {

    @Test
    public void gradientPathIsCorrect() throws IOException {
        openTestURL();
        $(ButtonElement.class).first().click();

        NotificationElement notificationElement = $(NotificationElement.class)
                .first();

        assertTrue(notificationElement.getCssValue("background-image")
                .contains("chameleon/img/grad"));
    }
}
