package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class DateTextHandlingTest extends SingleBrowserTest {

    public static final String Y2K_GB_LOCALE = "01-Jan-2000";

    @Test
    public void testSpecialValue() throws InterruptedException {
        openTestURL();
        DateFieldElement dateFieldElement = $(DateFieldElement.class).first();
        ButtonElement validate = $(ButtonElement.class).first();
        WebElement dateTextbox = dateFieldElement.getInputElement();

        dateTextbox.sendKeys("Y2K",Keys.TAB);
        validate.click();
        assertNotification("Y2K Should be converted to " + Y2K_GB_LOCALE, Y2K_GB_LOCALE);

        dateTextbox.clear();
        validate.click();
        assertNotification("Null for empty string","NULL");
    }

    protected void assertNotification(String message, String expected) {
        NotificationElement notification = $(NotificationElement.class).first();
        assertEquals(message, expected, notification.getCaption());
        notification.close();
    }

}
