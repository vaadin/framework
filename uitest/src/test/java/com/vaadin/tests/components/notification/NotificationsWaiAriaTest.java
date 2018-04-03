package com.vaadin.tests.components.notification;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.data.util.converter.StringToEnumConverter;

/**
 * Unit test class for Notification ARIA (Accessible Rich Internet Applications)
 * roles.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class NotificationsWaiAriaTest extends MultiBrowserTest {

    /**
     * Checks if the ARIA roles are correctly applied to Notification.
     *
     * @since 7.2
     * @throws Exception
     */
    @Test
    public void notificationTest() throws Exception {
        openTestURL();

        TextFieldElement prefix = $(TextFieldElement.class).first();
        TextFieldElement postfix = $(TextFieldElement.class).get(1);
        NativeSelectElement type = $(NativeSelectElement.class).first();
        ButtonElement show = $(ButtonElement.class).first();

        prefix.clear();
        prefix.sendKeys("Prefix:");

        postfix.clear();
        postfix.sendKeys("- press ESC to close");

        type.selectByText(StringToEnumConverter
                .enumToString(NotificationRole.ALERT, null));

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        NotificationElement notification = $(NotificationElement.class).first();

        String text = notification.getAttribute("role");
        assertTrue("Expected attribute 'role' to equal 'alert', found " + text,
                text.equals("alert"));

        text = getHiddenText(notification
                .findElements(By.className("v-assistive-device-only")).get(0));
        assertTrue("Expected 'Prefix:', found " + text, text.equals("Prefix:"));

        text = getHiddenText(notification
                .findElements(By.className("v-assistive-device-only")).get(1));
        assertTrue("Expected '- press ESC to close', found " + text,
                text.equals("- press ESC to close"));

        notification.close();

        type.selectByText(StringToEnumConverter
                .enumToString(NotificationRole.STATUS, null));

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        notification = $(NotificationElement.class).first();

        text = notification.getAttribute("role");
        assertTrue("Expected attribute 'role' to equal 'status', found " + text,
                text.equals("status"));

        notification.close();

        prefix.clear();
        postfix.clear();

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        WebElement element;
        try {
            element = getDriver().findElement(By.vaadin(
                    "Root/VNotification[0]/domChild[0]/domChild[0]/domChild[1]"));
        } catch (Exception e) {
            element = null;
        }
        assertNull("Notification shouldn't have assistive-device-only spans",
                element);

    }

    private String getHiddenText(WebElement element) {
        return (String) getCommandExecutor()
                .executeScript("return arguments[0].innerHTML", element);
    }
}
