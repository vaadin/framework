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

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("grid")
public class GridColumnHidingTest extends GridBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testColumnHiding_hidingColumnsFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumn(1);
        toggleHideColumn(2);
        toggleHideColumn(3);
        assertColumnHeaderOrder(4, 5, 6, 7, 8);
    }

    @Test
    public void testColumnHiding_unhidingColumnsFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(1);
        toggleHideColumn(2);
        toggleHideColumn(3);
        assertColumnHeaderOrder(0, 4, 5, 6, 7, 8);

        toggleHideColumn(1);
        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 4, 5, 6);
    }

    @Test
    public void testColumnHiding_hidingUnhidingFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);
    }

    private void toggleHideColumn(int columnIndex) {
        selectMenuPath("Component", "Columns", "Column " + columnIndex,
                "Hidden");
    }
}
