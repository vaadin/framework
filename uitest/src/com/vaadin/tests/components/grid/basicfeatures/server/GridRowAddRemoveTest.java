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
import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridRowAddRemoveTest extends GridBasicFeaturesTest {

    @Test
    public void addRows_loadAllAtOnce() {
        openTestURL();

        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Remove all rows");
        selectMenuPath("Component", "Body rows", "Add 18 rows");

        Assert.assertEquals(
                "All added rows should be fetched in the same round trip.",
                "2. Requested items 0 - 18", getLogRow(0));
    }

    @Test
    public void removeRows_loadAllAtOnce() {
        openTestURL();

        selectMenuPath("Settings", "Clear log");
        selectMenuPath("Component", "Body rows", "Remove 18 first rows");
        selectMenuPath("Component", "Body rows", "Remove 18 first rows");

        Assert.assertEquals(
                "All newly required rows should be fetched in the same round trip.",
                "2. Requested items 64 - 100", getLogRow(0));
    }
}
