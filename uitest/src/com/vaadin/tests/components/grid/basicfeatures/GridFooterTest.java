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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GridFooterTest extends GridStaticSectionTest {

    @Test
    public void testFooterVisibility() throws Exception {
        openTestURL();

        // Footer should have zero rows by default
        assertEquals(0, getGridFooterRowCells().size());
    }

    @Test
    public void testAddRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        assertFooterCount(1);
        assertFooterTexts(0, 0);

        selectMenuPath("Component", "Footer", "Prepend row");

        assertFooterCount(2);
        assertFooterTexts(1, 0);
        assertFooterTexts(0, 1);

        selectMenuPath("Component", "Footer", "Append row");

        assertFooterCount(3);
        assertFooterTexts(1, 0);
        assertFooterTexts(0, 1);
        assertFooterTexts(2, 2);
    }

    @Test
    public void testRemoveRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Prepend row");
        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Footer", "Remove top row");

        assertFooterCount(1);
        assertFooterTexts(1, 0);

        selectMenuPath("Component", "Footer", "Remove bottom row");
        assertFooterCount(0);
    }

    private void assertFooterCount(int count) {
        assertEquals("footer count", count, getGridElement().getFooterCount());
    }
}
