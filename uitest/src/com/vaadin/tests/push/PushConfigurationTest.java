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
package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.tests.tb3.WebsocketTest;

public class PushConfigurationTest extends WebsocketTest {

    @Test
    public void testWebsocketAndStreaming() throws InterruptedException {
        setDebug(true);
        openTestURL();
        // Websocket
        verifyPushDisabled();
        new Select(getTransportSelect()).selectByVisibleText("WEBSOCKET");
        new Select(getPushModeSelect()).selectByVisibleText("AUTOMATIC");
        Assert.assertTrue(vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[5]/VLabel[0]/domChild[0]")
                .getText()
                .matches(
                        "^[\\s\\S]*fallbackTransport: streaming[\\s\\S]*transport: websocket[\\s\\S]*$"));
        int counter = getServerCounter();
        final int waitCounter = counter + 2;
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return (getServerCounter() >= waitCounter);
            }
        });

        // Use debug console to verify we used the correct transport type
        Assert.assertTrue(driver.getPageSource().contains(
                "Push connection established using websocket"));
        Assert.assertFalse(driver.getPageSource().contains(
                "Push connection established using streaming"));

        new Select(getPushModeSelect()).selectByVisibleText("DISABLED");

        // Streaming
        driver.get(getTestUrl());
        verifyPushDisabled();

        new Select(getTransportSelect()).selectByVisibleText("STREAMING");
        new Select(getPushModeSelect()).selectByVisibleText("AUTOMATIC");
        Assert.assertTrue(vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[5]/VLabel[0]/domChild[0]")
                .getText()
                .matches(
                        "^[\\s\\S]*fallbackTransport: streaming[\\s\\S]*transport: streaming[\\s\\S]*$"));

        counter = getServerCounter();
        for (int second = 0;; second++) {
            if (second >= 5) {
                Assert.fail("timeout");
            }
            if (getServerCounter() >= (counter + 2)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        // Use debug console to verify we used the correct transport type
        Assert.assertFalse(driver.getPageSource().contains(
                "Push connection established using websocket"));
        Assert.assertTrue(driver.getPageSource().contains(
                "Push connection established using streaming"));

    }

    /**
     * Verifies that push is currently not enabled.
     * 
     * @throws InterruptedException
     */
    private void verifyPushDisabled() throws InterruptedException {
        int counter = getServerCounter();
        sleep(2000);
        assertEquals("Server count changed without push enabled", counter,
                getServerCounter());
    }

    private WebElement getPushModeSelect() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VNativeSelect[0]/domChild[0]");
    }

    private WebElement getTransportSelect() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[1]/VNativeSelect[0]/domChild[0]");
    }

    private int getServerCounter() {
        return Integer.parseInt(getServerCounterElement().getText());
    }

    private WebElement getServerCounterElement() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VLabel[0]");
    }
}
