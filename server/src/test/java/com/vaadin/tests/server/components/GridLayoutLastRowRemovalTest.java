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
package com.vaadin.tests.server.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class GridLayoutLastRowRemovalTest {

    @Test
    public void testRemovingLastRow() {
        GridLayout grid = new GridLayout(2, 1);
        grid.addComponent(new Label("Col1"));
        grid.addComponent(new Label("Col2"));

        try {
            // Removing the last row in the grid
            grid.removeRow(0);
        } catch (IllegalArgumentException iae) {
            // Removing the last row should not throw an
            // IllegalArgumentException
            fail("removeRow(0) threw an IllegalArgumentExcetion when removing the last row");
        }

        // The column amount should be preserved
        assertEquals(2, grid.getColumns());

        // There should be one row left
        assertEquals(1, grid.getRows());

        // There should be no component left in the grid layout
        assertNull("A component should not be left in the layout",
                grid.getComponent(0, 0));
        assertNull("A component should not be left in the layout",
                grid.getComponent(1, 0));

        // The cursor should be in the first cell
        assertEquals(0, grid.getCursorX());
        assertEquals(0, grid.getCursorY());
    }
}
