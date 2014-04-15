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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;

public abstract class PushReconnectTest extends MultiBrowserTestWithProxy {

    @Test
    public void testShortDisconnect() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        disconnectProxy();
        Thread.sleep(1000);
        connectProxy();
        waitUntilServerCounterChanges();
    }

    @Test
    public void testUserActionWhileDisconnectedWithDelay() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        disconnectProxy();
        Assert.assertEquals(0, getClientCounter());
        getIncrementClientCounterButton().click();
        // No change while disconnected
        Assert.assertEquals(0, getClientCounter());
        // Firefox sends extra onopen calls after a while, which breaks
        // everything
        Thread.sleep(10000);
        connectProxy();
        waitUntilServerCounterChanges();
        // The change should have appeared when reconnected
        Assert.assertEquals(1, getClientCounter());
    }

    @Test
    public void testUserActionWhileDisconnected() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        disconnectProxy();
        Assert.assertEquals(0, getClientCounter());
        getIncrementClientCounterButton().click();
        // No change while disconnected
        Assert.assertEquals(0, getClientCounter());
        Thread.sleep(1000);
        connectProxy();
        waitUntilServerCounterChanges();
        // The change should have appeared when reconnected
        Assert.assertEquals(1, getClientCounter());

        // IE has problems with another reconnect
        disconnectProxy();
        getIncrementClientCounterButton().click();
        Assert.assertEquals(1, getClientCounter());
        Thread.sleep(1000);
        connectProxy();
        waitUntilServerCounterChanges();
        Assert.assertEquals(2, getClientCounter());
    }

    @Test
    public void testLongDisconnect() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        disconnectProxy();
        Thread.sleep(12000);
        connectProxy();
        waitUntilServerCounterChanges();
    }

    @Test
    public void testReallyLongDisconnect() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        disconnectProxy();
        Thread.sleep(120000);
        connectProxy();
        waitUntilServerCounterChanges();
    }

    @Test
    public void testMultipleDisconnects() throws Exception {
        setDebug(true);
        openTestURL();
        startTimer();
        waitUntilServerCounterChanges();
        for (int i = 0; i < 5; i++) {
            disconnectProxy();
            Thread.sleep(1000);
            connectProxy();
            waitUntilServerCounterChanges();
        }
    }

    private int getClientCounter() {
        return BasicPushTest.getClientCounter(this);
    }

    private WebElement getIncrementClientCounterButton() {
        return BasicPushTest.getIncrementButton(this);
    }

    private void waitUntilServerCounterChanges() {
        final int counter = BasicPushTest.getServerCounter(this);
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest.getServerCounter(PushReconnectTest.this) > counter;
            }
        }, 30);
    }

    private void startTimer() {
        BasicPushTest.getServerCounterStartButton(this).click();
    }

}
