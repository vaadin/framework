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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public abstract class BasicPushTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testPush() throws InterruptedException {
        openTestURL();

        getIncrementButton().click();
        testBench().disableWaitForVaadin();

        waitUntilClientCounterChanges(1);

        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        waitUntilClientCounterChanges(4);

        // Test server initiated push
        getServerCounterStartButton().click();
        waitUntilServerCounterChanges();
    }

    public static int getClientCounter(AbstractTB3Test t) {
        WebElement clientCounterElem = t
                .vaadinElementById(BasicPush.CLIENT_COUNTER_ID);
        return Integer.parseInt(clientCounterElem.getText());
    }

    private WebElement getIncrementButton() {
        return getIncrementButton(this);
    }

    private WebElement getServerCounterStartButton() {
        return getServerCounterStartButton(this);
    }

    public static int getServerCounter(AbstractTB3Test t) {
        WebElement serverCounterElem = t
                .vaadinElementById(BasicPush.SERVER_COUNTER_ID);
        return Integer.parseInt(serverCounterElem.getText());
    }

    public static WebElement getServerCounterStartButton(AbstractTB3Test t) {
        return t.vaadinElementById(BasicPush.START_TIMER_ID);
    }

    public static WebElement getServerCounterStopButton(AbstractTB3Test t) {
        return t.vaadinElementById(BasicPush.STOP_TIMER_ID);
    }

    public static WebElement getIncrementButton(AbstractTB3Test t) {
        return t.vaadinElementById(BasicPush.INCREMENT_BUTTON_ID);
    }

    private void waitUntilClientCounterChanges(final int expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest.getClientCounter(BasicPushTest.this) == expectedValue;
            }
        }, 10);
    }

    private void waitUntilServerCounterChanges() {
        final int counter = BasicPushTest.getServerCounter(this);
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest.getServerCounter(BasicPushTest.this) > counter;
            }
        }, 10);
    }

}
