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
package com.vaadin.tests.components.ui;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.junit.Assert.assertTrue;

public class UIAccessTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        setPush(false);
        openTestURL();
    }

    @Test
    public void testThreadLocals() {
        setPush(true);
        openTestURL();

        $(ButtonElement.class).get(7).click();

        waitForLogToContainText("0. Current UI matches in beforeResponse? true");
        waitForLogToContainText("1. Current session matches in beforeResponse? true");
    }

    @Test
    public void canBeAccessedFromUIThread() {
        $(ButtonElement.class).first().click();

        assertTrue(logContainsText("0. Access from UI thread future is done? false"));
        assertTrue(logContainsText("1. Access from UI thread is run"));
        assertTrue(logContainsText("2. beforeClientResponse future is done? true"));
    }

    @Test
    public void canBeAccessedFromBackgroundThread() {
        $(ButtonElement.class).get(1).click();

        assertTrue(logContainsText("0. Initial background message"));
        assertTrue(logContainsText("1. Thread has current response? false"));

        waitForLogToContainText("2. Thread got lock, inital future done? true");
    }

    private void waitForLogToContainText(final String text) {
        waitUntil(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver input) {
                return logContainsText(text);
            }
        });
    }

    @Test
    public void exceptionCanBeThrown() {
        $(ButtonElement.class).get(2).click();

        assertTrue(logContainsText("0. Throwing exception in access"));
        assertTrue(logContainsText("1. firstFuture is done? true"));
        assertTrue(logContainsText("2. Got exception from firstFuture: java.lang.RuntimeException: Catch me if you can"));
    }

    @Test
    public void futureIsCancelledBeforeStarted() {
        $(ButtonElement.class).get(3).click();

        assertTrue(logContainsText("0. future was cancelled, should not start"));
    }

    @Test
    public void runningThreadIsCancelled() {
        $(ButtonElement.class).get(4).click();

        waitForLogToContainText("0. Waiting for thread to start");
        waitForLogToContainText("1. Thread started, waiting for interruption");
        waitForLogToContainText("2. I was interrupted");
    }

    @Test
    public void testAccessSynchronously() {
        $(ButtonElement.class).get(5).click();

        assertTrue(logContainsText("0. accessSynchronously has request? true"));
        assertTrue(logContainsText("1. Test value in accessSynchronously: Set before accessSynchronosly"));
        assertTrue(logContainsText("2. has request after accessSynchronously? true"));
        assertTrue(logContainsText("3. Test value after accessSynchornously: Set in accessSynchronosly"));
    }

    @Test
    public void currentInstanceCanAccessValue() {
        $(ButtonElement.class).get(6).click();

        assertTrue(logContainsText("0. access has request? false"));
        assertTrue(logContainsText("1. Test value in access: Set before access"));
        assertTrue(logContainsText("2. has request after access? true"));
        assertTrue(logContainsText("3. Test value after access: Set before run pending"));
    }

}
