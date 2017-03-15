/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.jcraft.jsch.JSchException;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;

@TestCategory("needs-ssh")
public class ReconnectDialogUITest extends MultiBrowserTestWithProxy {

    @Test
    public void reconnectTogglesBodyStyle() throws JSchException {
        openTestURL();
        getButton().click();
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogPresent();
        WebElement body = findElement(By.xpath("//body"));
        Assert.assertTrue("Body should have a style name when reconnecting",
                hasCssClass(body, "v-reconnecting"));
        connectProxy();
        waitForReconnectDialogToDisappear();
        Assert.assertFalse(
                "Body should no longer have a style name when reconnected",
                hasCssClass(body, "v-reconnecting"));
    }

    @Test
    public void reconnectDialogShownAndDisappears() throws JSchException {
        openTestURL();
        getButton().click();
        Assert.assertEquals("1. Hello from the server", getLogRow(0));
        disconnectProxy();
        getButton().click();
        waitForReconnectDialogWithText(
                "Server connection lost, trying to reconnect...");
        connectProxy();
        waitForReconnectDialogToDisappear();
        Assert.assertEquals("2. Hello from the server", getLogRow(0));
    }

    private void waitForReconnectDialogWithText(final String text) {
        waitForReconnectDialogPresent();
        final WebElement reconnectDialog = findElement(
                ReconnectDialogThemeTest.reconnectDialogBy);
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return reconnectDialog.findElement(By.className("text"))
                        .getText().equals(text);
            }
        }, 10);

    }

    private void waitForReconnectDialogToDisappear() {
        waitForElementNotPresent(ReconnectDialogThemeTest.reconnectDialogBy);

    }

    private void waitForReconnectDialogPresent() {
        waitForElementPresent(ReconnectDialogThemeTest.reconnectDialogBy);
    }

    private WebElement getButton() {
        return $(ButtonElement.class).first();
    }

}
