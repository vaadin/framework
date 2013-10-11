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

import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class BasicPushTest extends MultiBrowserTest {

    @Test
    public void testPush() {
        openTestURL();

        // Test client initiated push
        Assert.assertEquals(0, getClientCounter());
        getIncrementButton().click();
        Assert.assertEquals(
                "Client counter not incremented by button click", 1,
                getClientCounter());
        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        Assert.assertEquals(
                "Four clicks should have incremented counter to 4", 4,
                getClientCounter());

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
        return Integer.parseInt(getServerCounterElement().getText());
    }

    private int getClientCounter() {
        return Integer.parseInt(getClientCounterElement().getText());
    }

    private WebElement getServerCounterElement() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[4]/VLabel[0]");
    }

    private WebElement getServerCounterStartButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getServerCounterStopButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[6]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getIncrementButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getClientCounterElement() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]");
    }
}