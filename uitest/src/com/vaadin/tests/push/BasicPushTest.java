/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import org.openqa.selenium.WebElement;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public abstract class BasicPushTest extends MultiBrowserTest {

    @Test
    public void testPush() throws InterruptedException {
        openTestURL();

        // Test client initiated push
        Assert.assertEquals(0, getClientCounter());
        getIncrementButton().click();
        Assert.assertEquals("Client counter not incremented by button click",
                1, getClientCounter());
        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        Assert.assertEquals("Four clicks should have incremented counter to 4",
                4, getClientCounter());

        // Test server initiated push
        getServerCounterStartButton().click();
        try {
            Assert.assertEquals(0, getServerCounter());
            sleep(3000);
            int serverCounter = getServerCounter();
            if (serverCounter < 1) {
                // No push has happened
                Assert.fail("No push has occured within 3s");
            }
            sleep(3000);
            if (getServerCounter() <= serverCounter) {
                // No push has happened
                Assert.fail("Only one push took place within 6s");

            }
        } finally {
            // Avoid triggering push assertions
            getServerCounterStopButton().click();
        }
    }

    private int getServerCounter() {
        return getServerCounter(this);
    }

    private int getClientCounter() {
        return getClientCounter(this);
    }

    public static int getClientCounter(AbstractTB3Test t) {
        WebElement clientCounterElem = t
                .vaadinElementById(BasicPush.CLIENT_COUNTER_ID);
        return Integer.parseInt(clientCounterElem.getText());
    }

    private WebElement getIncrementButton() {
        return getIncrementButton(this);
    }

    private WebElement getServerCounterStopButton() {
        return getServerCounterStopButton(this);
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

}