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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIAccessTest extends MultiBrowserTest {
    @Test
    public void testThreadLocals() {
        setPush(true);
        openTestURL();
        getCurrentInstanceWhenPushingButton().click();
        waitUntil(ExpectedConditions.textToBePresentInElement(
                vaadinLocatorById("Log_row_0"), "1."));
        Assert.assertEquals("0. Current UI matches in beforeResponse? true",
                vaadinElementById("Log_row_1").getText());
        Assert.assertEquals(
                "1. Current session matches in beforeResponse? true",
                vaadinElementById("Log_row_0").getText());

    }

    @Test
    public void testAccessMethod() throws Exception {
        openTestURL();

        vaadinElement(
                "/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[0]/VButton[0]/domChild[0]/domChild[0]")
                .click();
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                .getText()
                .matches(
                        "^0\\. Access from UI thread future is done[\\s\\S] false$"));
        assertEquals(
                "1. Access from UI thread is run",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                        .getText());
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                .getText()
                .matches(
                        "^2\\. beforeClientResponse future is done[\\s\\S] true$"));
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertEquals(
                "0. Initial background message",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                        .getText());
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                .getText()
                .matches("^1\\. Thread has current response[\\s\\S] false$"));
        for (int second = 0;; second++) {
            if (second >= 30) {
                fail("timeout");
            }
            try {
                if ("0. Initial background message"
                        .equals(driver
                                .findElement(
                                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                                .getText())) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                .getText()
                .matches(
                        "^2\\. Thread got lock, inital future done[\\s\\S] true$"));
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertEquals(
                "0. Throwing exception in access",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                        .getText());
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                .getText().matches("^1\\. firstFuture is done[\\s\\S] true$"));
        assertEquals(
                "2. Got exception from firstFuture: java.lang.RuntimeException: Catch me if you can",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                        .getText());
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[3]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertEquals(
                "0. future was cancled, should not start",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                        .getText());
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[4]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertEquals(
                "0. Waiting for thread to start",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                        .getText());
        assertEquals(
                "1. Thread started, waiting for interruption",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                        .getText());
        assertEquals(
                "2. I was interrupted",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                        .getText());
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_3"))
                .getText()
                .matches("^0\\. accessSynchronously has request[\\s\\S] true$"));
        assertEquals(
                "1. Test value in accessSynchronously: Set before accessSynchronosly",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                        .getText());
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                .getText()
                .matches(
                        "^2\\. has request after accessSynchronously[\\s\\S] true$"));
        assertEquals(
                "3. Test value after accessSynchornously: Set in accessSynchronosly",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                        .getText());
        driver.findElement(
                By.vaadin("runcomvaadintestscomponentsuiUIAccess::/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_3"))
                .getText().matches("^0\\. access has request[\\s\\S] false$"));
        assertEquals(
                "1. Test value in access: Set before access",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_2"))
                        .getText());
        assertTrue(driver
                .findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_1"))
                .getText()
                .matches("^2\\. has request after access[\\s\\S] true$"));
        assertEquals(
                "3. Test value after access: Set before run pending",
                driver.findElement(
                        By.vaadin("runcomvaadintestscomponentsuiUIAccess::PID_SLog_row_0"))
                        .getText());

    }

    private WebElement getCurrentInstanceWhenPushingButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot[7]/VButton[0]");
    }
}
