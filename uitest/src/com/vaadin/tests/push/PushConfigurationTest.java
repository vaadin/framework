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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
abstract class PushConfigurationTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
        disablePush();
    }

    @Override
    protected String getDeploymentPath() {
        return "/run/" + PushConfiguration.class.getCanonicalName()
                + "?restartApplication&debug";
    }

    protected String getStatusText() {
        WebElement statusLabel = vaadinElementById("status");

        return statusLabel.getText();
    }

    protected void disablePush() throws InterruptedException {
        new Select(getPushModeSelect()).selectByVisibleText("DISABLED");

        int counter = getServerCounter();
        sleep(2000);
        assertEquals("Server count changed without push enabled", counter,
                getServerCounter());
    }

    protected WebElement getPushModeSelect() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VNativeSelect[0]/domChild[0]");
    }

    protected WebElement getTransportSelect() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[1]/VNativeSelect[0]/domChild[0]");
    }

    protected int getServerCounter() {
        return Integer.parseInt(getServerCounterElement().getText());
    }

    protected WebElement getServerCounterElement() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VLabel[0]");
    }

    protected void waitForServerCounterToUpdate() {
        int counter = getServerCounter();
        final int waitCounter = counter + 2;
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return (getServerCounter() >= waitCounter);
            }
        });
    }
}
