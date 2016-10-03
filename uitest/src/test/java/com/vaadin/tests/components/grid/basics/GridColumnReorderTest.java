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
package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide;

/**
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridColumnReorderTest extends GridBasicsTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testColumnReorder_onReorder_columnReorderEventTriggered() {
        selectMenuPath("Component", "Header", "Prepend header row");
        selectMenuPath("Component", "State", "Column reorder listener");
        selectMenuPath("Component", "Columns", "Column " + 3, "Move left");

        assertEquals("1. Registered a column reorder listener.", getLogRow(2));
        assertEquals("2. Columns reordered, userOriginated: false",
                getLogRow(1));
        assertColumnHeaderOrder(0, 1, 3, 2);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + 3, "Move left");
        assertColumnHeaderOrder(0, 1, 2, 3);

        // test drag and drop is user originated
        toggleColumnReorder();
        dragAndDropColumnHeader(0, 0, 1, CellSide.RIGHT);
        assertEquals("6. Columns reordered, userOriginated: true",
                getLogRow(1));
        assertColumnHeaderOrder(1, 0, 2, 3);
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortIndicatorShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);

        // when
        startDragButDontDropOnDefaultColumnHeader(0);

        // then
        WebElement draggedElement = getDraggedHeaderElement();
        assertTrue(draggedElement.getAttribute("class").contains("sort"));
    }

    @Test
    public void testColumnReorder_draggingSortedColumn_sortStays() {
        // given
        toggleColumnReorder();
        toggleSortableColumn(0);
        sortColumn(0);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertColumnIsSorted(1);
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusShownOnDraggedElement() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);

        // when
        startDragButDontDropOnDefaultColumnHeader(0);

        // then
        WebElement draggedElement = getDraggedHeaderElement();
        assertTrue(draggedElement.getAttribute("class").contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedHeader_focusIsKeptOnHeader() {
        // given
        toggleColumnReorder();
        focusDefaultHeader(0);

        // when
        dragAndDropDefaultColumnHeader(0, 3, CellSide.LEFT);

        // then
        WebElement defaultColumnHeader = getDefaultColumnHeader(2);
        String attribute = defaultColumnHeader.getAttribute("class");
        assertTrue(attribute.contains("focused"));
    }

    @Test
    public void testColumnReorder_draggingFocusedCellColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        focusCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        assertFocusedCell(2, 0);
    }

    @Test
    public void testColumnReorderWithHiddenColumn_draggingFocusedCellColumnOverHiddenColumn_focusIsKeptOnCell() {
        // given
        toggleColumnReorder();
        selectMenuPath("Component", "Columns", "Column 1", "Hidden");
        focusCell(2, 2);
        assertFocusedCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(1, 0, CellSide.LEFT);

        // then
        assertFocusedCell(2, 2);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertFocusedCell(2, 2);
    }

    @Test
    public void testColumnReorder_dragColumnFromRightToLeftOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(1, 3);

        // when
        dragAndDropDefaultColumnHeader(4, 1, CellSide.LEFT);

        // then
        assertFocusedCell(1, 4);
    }

    @Test
    public void testColumnReorder_dragColumnFromLeftToRightOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(4, 2);

        // when
        dragAndDropDefaultColumnHeader(0, 4, CellSide.LEFT);

        // then
        assertFocusedCell(4, 1);
    }

    private void toggleSortableColumn(int index) {
        selectMenuPath("Component", "Columns", "Column " + index, "Sortable");
    }

    private void startDragButDontDropOnDefaultColumnHeader(int index) {
        new Actions(getDriver())
                .clickAndHold(getGridHeaderRowCells().get(index))
                .moveByOffset(100, 0).perform();
    }

    private void sortColumn(int index) {
        getGridHeaderRowCells().get(index).click();
    }

    private void focusDefaultHeader(int index) {
        getGridHeaderRowCells().get(index).click();
    }

    private WebElement getDraggedHeaderElement() {
        return findElement(By.className("dragged-column-header"));
    }
}
