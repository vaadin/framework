/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.notification;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.shared.ui.ui.NotificationRole;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

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

        type.selectByText(StringToEnumConverter.enumToString(
                NotificationRole.ALERT, null));

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        NotificationElement notification = $(NotificationElement.class).first();

        String text = notification.getAttribute("role");
        Assert.assertTrue("Expected attribute 'role' to equal 'alert', found "
                + text, text.equals("alert"));

        text = getHiddenText(notification.findElements(
                By.className("v-assistive-device-only")).get(0));
        Assert.assertTrue("Expected 'Prefix:', found " + text,
                text.equals("Prefix:"));

        text = getHiddenText(notification.findElements(
                By.className("v-assistive-device-only")).get(1));
        Assert.assertTrue("Expected '- press ESC to close', found " + text,
                text.equals("- press ESC to close"));

        notification.close();

        type.selectByText(StringToEnumConverter.enumToString(
                NotificationRole.STATUS, null));

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        notification = $(NotificationElement.class).first();

        text = notification.getAttribute("role");
        Assert.assertTrue("Expected attribute 'role' to equal 'status', found "
                + text, text.equals("status"));

        notification.close();

        prefix.clear();
        postfix.clear();

        show.click();
        waitForElementPresent(By.className("v-Notification"));

        WebElement element;
        try {
            element = getDriver()
                    .findElement(
                            By.vaadin("Root/VNotification[0]/domChild[0]/domChild[0]/domChild[1]"));
        } catch (Exception e) {
            element = null;
        }
        Assert.assertNull(
                "Notification shouldn't have assistive-device-only spans",
                element);

    }

    private String getHiddenText(WebElement element) {
        return (String) getTestBenchCommandExecutor().executeScript(
                "return arguments[0].innerHTML", element);
    }
}
