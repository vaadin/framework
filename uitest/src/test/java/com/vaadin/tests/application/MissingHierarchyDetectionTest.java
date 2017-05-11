/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MissingHierarchyDetectionTest extends SingleBrowserTest {
    @Test
    public void testMissingHierarchyDetection() {
        openTestURL();

        Assert.assertTrue(isElementPresent(By.id("label")));

        ButtonElement toggleProperly = $(ButtonElement.class)
                .caption("Toggle properly").first();

        toggleProperly.click();
        assertNoSystemNotifications();
        Assert.assertFalse(isElementPresent(By.id("label")));

        toggleProperly.click();
        assertNoSystemNotifications();
        Assert.assertTrue(isElementPresent(LabelElement.class));

        ButtonElement toggleImproperly = $(ButtonElement.class)
                .caption("Toggle improperly").first();
        toggleImproperly.click();

        $(ButtonElement.class).caption("Check for errors").first().click();
        Assert.assertTrue(
                "No error was logged for the missing hierarchy change event",
                getLogRow(0).contains(
                        "is no longer visible to the client, but no corresponding hierarchy change was sent."));
    }
}
