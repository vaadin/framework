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
package com.vaadin.tests.dd;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for interrupting drag-and-drop.
 * 
 * @author Vaadin Ltd
 */
public class DDInterruptTest extends MultiBrowserTest {

    private UIElement ui;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL("debug");
        ui = $(UIElement.class).first();
    }

    @Test
    public void testRegularDragging() {
        dragElement();
        assertNoNotifications();
        assertDragged(true);
    }

    @Test
    public void testTriggeredDragging() {
        $(ButtonElement.class).first().click();
        waitUntilTriggered(true);

        dragElement();

        waitUntilTriggered(false);
        assertNoNotifications();
        assertDragged(false);
    }

    private void dragElement() {
        LabelElement label = $(LabelElement.class).id("draggable");
        new Actions(driver).moveToElement(label).clickAndHold()
                .moveByOffset(400, 400).perform();
    }

    private void assertNoNotifications() {
        Assert.assertEquals(
                "Notification found when there should have been none,", 0,
                findElements(By.className("v-Notification")).size());
    }

    private void assertDragged(boolean dragged) {
        Assert.assertEquals("Unexpected drag state,", dragged ? 1 : 0,
                findElements(By.className("v-ddwrapper-over-bottom")).size());
    }

    private void waitUntilTriggered(final boolean triggered) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                return triggered == hasCssClass(ui, "triggered");
            }

            @Override
            public String toString() {
                return String.format("UI to %s stylename 'triggered'",
                        (triggered ? "get" : "lose"));
            }
        });
    }
}
