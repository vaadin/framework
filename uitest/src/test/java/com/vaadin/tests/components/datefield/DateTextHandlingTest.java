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
    @Test
    public void testSpecialValue() throws InterruptedException {
        openTestURL();
        DateFieldElement dateFieldElement = $(DateFieldElement.class).first();
        ButtonElement validate = $(ButtonElement.class).first();
        LabelElement validateResult = $(LabelElement.class).first();
        WebElement dateTextbox = dateFieldElement
                .findElement(com.vaadin.testbench.By.className("v-textfield"));

        dateTextbox.sendKeys("Y2K",Keys.TAB);
        validate.click();
        assertNotification("Y2K Sould be converted to 1-JAN-2000", "01-Jan-2000");

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
