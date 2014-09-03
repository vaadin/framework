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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.components.grid.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridActiveCellAdjustmentTest extends GridBasicFeaturesTest {

    @Test
    public void testActiveCellWithAddAndRemoveRows() {
        openTestURL();
        GridElement grid = getGridElement();

        grid.getCell(0, 0).click();

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Active cell was not moved when adding a row",
                grid.getCell(1, 0).isActive());

        selectMenuPath("Component", "Body rows", "Add 18 rows");
        assertTrue("Active cell was not moved when adding multiple rows", grid
                .getCell(19, 0).isActive());

        for (int i = 18; i <= 0; --i) {
            selectMenuPath("Component", "Body rows", "Remove first row");
            assertTrue("Active cell was not moved when removing a row", grid
                    .getCell(i, 0).isActive());
        }
    }

}
