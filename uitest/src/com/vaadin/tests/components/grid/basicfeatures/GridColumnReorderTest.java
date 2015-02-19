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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.annotations.TestCategory;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridColumnReorderTest extends GridBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement().getHeaderCell(0,
                firstIndex).getText();
        final String secondHeaderText = getGridElement().getHeaderCell(0,
                secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners",
                "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0,
                firstIndex);
        assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        assertEquals(firstHeaderText, headerCell.getText());

        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(1, eventIndex);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(2, eventIndex);
    }

    @Test
    public void testColumnReorder_onReorder_columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement().getHeaderCell(0,
                firstIndex).getText();
        final String secondHeaderText = getGridElement().getHeaderCell(0,
                secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners",
                "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0,
                firstIndex);
        assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        assertEquals(firstHeaderText, headerCell.getText());

        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(1, eventIndex);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(2, eventIndex);
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
        dragDefaultColumnHeader(0, 2, 10);

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
        dragDefaultColumnHeader(0, 3, 10);

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
        dragDefaultColumnHeader(2, 0, 10);

        // then
        assertFocusedCell(2, 0);
    }

    @Test
    public void testColumnReorder_dragColumnFromRightToLeftOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(1, 3);

        // when
        dragDefaultColumnHeader(4, 1, 10);

        // then
        assertFocusedCell(1, 4);
    }

    @Test
    public void testColumnReorder_dragColumnFromLeftToRightOfFocusedCellColumn_focusIsKept() {
        // given
        toggleColumnReorder();
        focusCell(4, 2);

        // when
        dragDefaultColumnHeader(0, 4, 10);

        // then
        assertFocusedCell(4, 1);
    }

    private void toggleColumnReorder() {
        selectMenuPath("Component", "State", "Column Reordering");
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
