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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeaturesWidget;

public class GridClientColumnPropertiesTest extends GridBasicClientFeaturesTest {

    @Test
    public void initialColumnWidths() {
        openTestURL();

        for (int col = 0; col < GridBasicClientFeaturesWidget.COLUMNS; col++) {
            int width = getGridElement().getCell(0, col).getSize().getWidth();
            if (col <= 6) {
                // Growing column widths
                int expectedWidth = 50 + col * 25;
                assertEquals("column " + col + " has incorrect width",
                        expectedWidth, width);
            }
        }
    }

    @Test
    public void testChangingColumnWidth() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Width", "50px");
        int width = getGridElement().getCell(0, 0).getSize().getWidth();
        assertEquals(50, width);

        selectMenuPath("Component", "Columns", "Column 0", "Width", "200px");
        width = getGridElement().getCell(0, 0).getSize().getWidth();
        assertEquals(200, width);

        selectMenuPath("Component", "Columns", "Column 0", "Width", "auto");
        int autoWidth = getGridElement().getCell(0, 0).getSize().getWidth();
        assertLessThan("Automatic sizing should've shrunk the column",
                autoWidth, width);
    }

    @Test
    public void testFrozenColumns() {
        openTestURL();

        assertFalse(cellIsFrozen(0, 0));
        assertFalse(cellIsFrozen(0, 1));

        selectMenuPath("Component", "State", "Frozen column count", "1 columns");

        assertTrue(cellIsFrozen(1, 0));
        assertFalse(cellIsFrozen(1, 1));

        selectMenuPath("Component", "State", "Selection mode", "multi");

        assertTrue(cellIsFrozen(1, 1));
        assertFalse(cellIsFrozen(1, 2));

        selectMenuPath("Component", "State", "Frozen column count", "0 columns");

        assertTrue(cellIsFrozen(1, 0));
        assertFalse(cellIsFrozen(1, 1));

        selectMenuPath("Component", "State", "Frozen column count",
                "-1 columns");

        assertFalse(cellIsFrozen(1, 0));
    }

    @Test
    public void testFrozenColumns_columnsReordered_frozenColumnsKept() {
        openTestURL();

        selectMenuPath("Component", "State", "Frozen column count", "2 columns");

        assertTrue(cellIsFrozen(1, 0));
        assertTrue(cellIsFrozen(1, 1));
        assertFalse(cellIsFrozen(1, 2));

        selectMenuPath("Component", "State", "Reverse grid columns");

        assertTrue(cellIsFrozen(1, 0));
        assertTrue(cellIsFrozen(1, 1));
        assertFalse(cellIsFrozen(1, 2));
    }

    @Test
    public void testBrokenRenderer() {
        setDebug(true);
        openTestURL();

        GridElement gridElement = getGridElement();

        // Scroll first row out of view
        gridElement.getRow(50);

        // Enable broken renderer for the first row
        selectMenuPath("Component", "Columns", "Column 0", "Broken renderer");

        // Shouldn't have an error notification yet
        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        // Scroll broken row into view and enjoy the chaos
        gridElement.getRow(0);

        assertTrue("Notification was not present",
                isElementPresent(NotificationElement.class));

        assertFalse("Text in broken cell should have old value",
                "(0, 0)".equals(gridElement.getCell(0, 0).getText()));

        assertEquals("Neighbour cell should be updated", "(0, 1)", gridElement
                .getCell(0, 1).getText());

        assertEquals("Neighbour cell should be updated", "(1, 0)", gridElement
                .getCell(1, 0).getText());
    }

    @Test
    public void testColumnWidths_onColumnReorder_columnWidthsKeptTheSame() {
        // given
        openTestURL();
        GridElement gridElement = getGridElement();
        List<GridCellElement> headerCells = gridElement.getHeaderCells(0);

        final List<Integer> columnWidths = new ArrayList<Integer>();
        for (GridCellElement cell : headerCells) {
            columnWidths.add(cell.getSize().getWidth());
        }

        // when
        selectMenuPath("Component", "State", "Reverse grid columns");

        // then
        gridElement = getGridElement();
        headerCells = gridElement.getHeaderCells(0);
        final int size = headerCells.size();
        for (int i = 0; i < size; i++) {
            // Avoid issues with inaccuracies regarding subpixels.
            assertEquals(
                    "Column widths don't match after reset, index after flip "
                            + i, columnWidths.get(i),
                    headerCells.get(size - 1 - i).getSize().getWidth(), 1.0d);
        }

    }

    private boolean cellIsFrozen(int row, int col) {
        return getGridElement().getCell(row, col).isFrozen();
    }
}
