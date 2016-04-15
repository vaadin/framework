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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.jcraft.jsch.JSchException;
import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;

public abstract class ReconnectTest extends MultiBrowserTestWithProxy {

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
        openTestURL();
        openDebugLogTab();

        startTimer();
        waitUntilServerCounterChanges();

        testBench().disableWaitForVaadin();
    }

    @Test
    public void messageIsQueuedOnDisconnect() throws JSchException {
        disconnectProxy();

        clickButtonAndWaitForTwoReconnectAttempts();

        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }

    @Test
    public void messageIsNotSentBeforeConnectionIsEstablished()
            throws JSchException, InterruptedException {
        disconnectProxy();

        waitForNextReconnectionAttempt();
        clickButtonAndWaitForTwoReconnectAttempts();

        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }

    private void clickButtonAndWaitForTwoReconnectAttempts() {
        clickClientButton();

        // Reconnection attempt is where pending messages can
        // falsely be sent to server.
        waitForNextReconnectionAttempt();

        // Waiting for the second reconnection attempt makes sure that the
        // first attempt has been completed or aborted.
        waitForNextReconnectionAttempt();
    }

    private void clickClientButton() {
        getIncrementClientCounterButton().click();
    }

    private void waitForNextReconnectionAttempt() {
        clearDebugMessages();
        waitForDebugMessage("Reopening push connection");
    }

    private void connectAndVerifyConnectionEstablished() throws JSchException {
        connectProxy();
        waitUntilServerCounterChanges();
    }

    private WebElement getIncrementClientCounterButton() {
        return BasicPushTest.getIncrementButton(this);
    }

    private void waitUntilServerCounterChanges() {
        final int counter = BasicPushTest.getServerCounter(this);
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest.getServerCounter(ReconnectTest.this) > counter;
            }
        }, 30);
    }

    private void waitUntilClientCounterChanges(final int expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest.getClientCounter(ReconnectTest.this) == expectedValue;
            }
        }, 5);
    }

    private void startTimer() {
        BasicPushTest.getServerCounterStartButton(this).click();
    }

}
