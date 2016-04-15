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
package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ResynchronizeAfterAsyncRemovalTest extends SingleBrowserTest {
    @Test
    public void noResyncAfterAsyncRemoval() {
        openTestURL();

        $(ButtonElement.class).first().click();

        Assert.assertEquals("Timing issue in the test?",
                "1. Window removed: true", getLogRow(1));

        Assert.assertEquals(
                "Removing window should not cause button to be marked as dirty",
                "2. Dirty: false", getLogRow(0));

        ButtonElement logCountButton = $(ButtonElement.class).all().get(1);
        logCountButton.click();

        Assert.assertEquals("Sanity check", "3. syncId: 2", getLogRow(1));
        Assert.assertEquals("Sanity check",
                "4. Unregistered connector count: 1", getLogRow(0));

        logCountButton.click();

        Assert.assertEquals("Sanity check", "5. syncId: 3", getLogRow(1));
        Assert.assertEquals(
                "Unregistered connector map should have been cleared",
                "6. Unregistered connector count: 0", getLogRow(0));
    }
}
