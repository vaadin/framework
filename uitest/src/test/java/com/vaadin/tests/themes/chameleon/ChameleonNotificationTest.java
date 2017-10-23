package com.vaadin.tests.themes.chameleon;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

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

        assertThat(notificationElement.getCssValue("background-image"),
                containsString("chameleon/img/grad"));
    }
}
