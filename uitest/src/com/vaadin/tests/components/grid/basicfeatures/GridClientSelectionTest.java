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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GridClientSelectionTest extends GridBasicClientFeaturesTest {

    @Test
    public void testChangeSelectionMode() {
        openTestURL();

        selectMenuPath("Component", "State", "Selection mode", "none");
        assertTrue("First column was selection column", getGridElement()
                .getCell(0, 0).getText().equals("(0, 0)"));
        selectMenuPath("Component", "State", "Selection mode", "multi");
        assertTrue("First column was not selection column", getGridElement()
                .getCell(0, 1).getText().equals("(0, 0)"));
    }
}
