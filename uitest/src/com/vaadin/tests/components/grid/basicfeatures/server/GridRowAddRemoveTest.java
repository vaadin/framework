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
package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridRowAddRemoveTest extends GridBasicFeaturesTest {

    @Test
    public void addRows_loadAllAtOnce() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Remove all rows");
        selectMenuPath("Component", "Body rows", "Add 18 rows");

        Assert.assertTrue(
                "All added rows should be fetched in the same round trip.",
                logContainsText("Requested items 0 - 18"));
    }

    @Test
    @Ignore("This test checks the parameters passed to Container. Has nothing to do with what's sent to client.")
    public void removeRows_loadAllAtOnce() {
        openTestURL();

        selectMenuPath("Component", "Size", "HeightMode Row");
        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Remove 18 first rows");

        Assert.assertTrue(
                "All newly required rows should be fetched in the same round trip.",
                logContainsText("Requested items 37 - 55"));

        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Remove 18 first rows");

        Assert.assertTrue(
                "All newly required rows should be fetched in the same round trip.",
                logContainsText("Requested items 37 - 55"));
    }

    @Test
    public void testAdd18Rows() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Add 18 rows");

        Assert.assertFalse("An error notification is present.",
                isElementPresent(NotificationElement.class));
    }
}
