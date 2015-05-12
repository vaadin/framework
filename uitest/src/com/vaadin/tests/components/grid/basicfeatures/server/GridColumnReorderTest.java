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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

/**
 * Tests that Grid columns can be reordered by user with drag and drop #16643.
 * 
 * @author Vaadin Ltd
 */
public class GridColumnReorderTest extends GridBasicFeaturesTest {

    private static final String[] COLUMN_REORDERING_PATH = { "Component",
            "State", "Column Reordering Allowed" };
    private static final String[] COLUMN_REORDER_LISTENER_PATH = { "Component",
            "State", "ColumnReorderListener" };

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void testColumnReordering_firstColumnDroppedOnThird_dropOnLeftSide() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);
        toggleColumnReordering();

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(1, 0, 2);
    }

    @Test
    public void testColumnReordering_firstColumnDroppedOnThird_dropOnRightSide() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);
        toggleColumnReordering();

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(1, 2, 0);
    }

    @Test
    public void testColumnReordering_reorderingTwiceBackForth_reordered() {
        // given
        openTestURL();
        selectMenuPath("Component", "Size", "Width", "800px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        toggleColumnReordering();

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(2, 0, 1, 3, 4);

        // when
        dragAndDropDefaultColumnHeader(1, 3, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(2, 1, 3, 0);
    }

    @Test
    public void testColumnReordering_notEnabled_noReordering() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(0, 1, 2);
    }

    @Test
    public void testColumnReordering_userChangesRevertedByServer_columnsAreUpdated() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);
        toggleColumnReordering();

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);
        assertColumnHeaderOrder(1, 0, 2);
        moveColumnManuallyLeftByOne(0);

        // then
        assertColumnHeaderOrder(0, 1, 2);
    }

    @Test
    public void testColumnReordering_concurrentUpdatesFromServer_columnOrderFromServerUsed() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);
        toggleColumnReordering();

        // when
        selectMenuPath(new String[] { "Component", "Internals",
                "Update column order without updating client" });
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(1, 0, 2);
    }

    @Test
    public void testColumnReordering_triggersReorderEvent_isUserInitiated() {
        // given
        openTestURL();
        toggleColumnReordering();

        // when
        toggleColumnReorderListener();
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertColumnReorderEvent(true);
    }

    @Test
    public void testColumnReordering_addAndRemoveListener_registerUnRegisterWorks() {
        // given
        openTestURL();
        toggleColumnReordering();
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);
        assertNoColumnReorderEvent();

        // when
        toggleColumnReorderListener();
        dragAndDropDefaultColumnHeader(0, 2, CellSide.RIGHT);

        // then
        assertColumnReorderEvent(true);

        // when
        toggleColumnReorderListener();
        dragAndDropDefaultColumnHeader(0, 3, CellSide.LEFT);

        // then
        assertNoColumnReorderEvent();
    }

    @Test
    public void testColumnReorderingEvent_serverSideReorder_triggersReorderEvent() {
        openTestURL();

        // when
        toggleColumnReorderListener();
        moveColumnManuallyLeftByOne(3);

        // then
        assertColumnReorderEvent(false);
    }

    @Test
    public void testColumnReorder_draggingFrozenColumns_impossible() {
        // given
        openTestURL();
        toggleColumnReordering();
        setFrozenColumns(2);
        assertColumnHeaderOrder(0, 1, 2, 3);

        // when
        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(0, 1, 2, 3);
        assertTrue(getGridElement().getHeaderCell(0, 0).isFrozen());
        assertTrue(getGridElement().getHeaderCell(0, 1).isFrozen());
        assertFalse(getGridElement().getHeaderCell(0, 2).isFrozen());
    }

    @Test
    public void testColumnReorder_draggingColumnOnTopOfFrozenColumn_columnDroppedRightOfFrozenColumns() {
        // given
        openTestURL();
        toggleColumnReordering();
        setFrozenColumns(1);
        assertColumnHeaderOrder(0, 1, 2, 3);

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        assertColumnHeaderOrder(0, 2, 1, 3);
    }

    @Test
    public void testColumnReorder_draggingColumnLeftOfMultiSelectionColumn_columnDroppedRight() {
        // given
        openTestURL();
        toggleColumnReordering();
        selectMenuPath("Component", "State", "Selection mode", "multi");
        List<TestBenchElement> gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(3));

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(3));
    }

    @Test
    public void testColumnReorder_multiSelectionAndFrozenColumns_columnDroppedRight() {
        // given
        openTestURL();
        toggleColumnReordering();
        selectMenuPath("Component", "State", "Selection mode", "multi");
        setFrozenColumns(1);
        List<TestBenchElement> gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(3));

        // when
        dragAndDropDefaultColumnHeader(3, 0, CellSide.LEFT);

        // then
        gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(3));
    }

    @Test
    public void testColumnReordering_multiSelectionColumnNotFrozen_stillCantDropLeftSide() {
        // given
        openTestURL();
        toggleColumnReordering();
        selectMenuPath("Component", "State", "Selection mode", "multi");
        setFrozenColumns(-1);
        List<TestBenchElement> gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(3));

        // when
        dragAndDropDefaultColumnHeader(2, 0, CellSide.LEFT);

        // then
        gridHeaderRowCells = getGridHeaderRowCells();
        assertTrue(gridHeaderRowCells.get(0).getText().equals(""));
        assertColumnHeader("Column 1", gridHeaderRowCells.get(1));
        assertColumnHeader("Column 0", gridHeaderRowCells.get(2));
        assertColumnHeader("Column 2", gridHeaderRowCells.get(3));
    }

    @Test
    public void testColumnReordering_twoHeaderRows_dndReorderingPossibleFromFirstRow() {
        // given
        openTestURL();
        toggleColumnReordering();
        selectMenuPath("Component", "Header", "Append row");
        assertColumnHeaderOrder(0, 1, 2, 3);

        // when
        dragAndDropColumnHeader(0, 0, 2, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(1, 2, 0, 3);
    }

    @Test
    public void testColumnReordering_twoHeaderRows_dndReorderingPossibleFromSecondRow() {
        // given
        openTestURL();
        toggleColumnReordering();
        selectMenuPath("Component", "Header", "Append row");
        assertColumnHeaderOrder(0, 1, 2, 3);

        // when
        dragAndDropColumnHeader(1, 0, 2, CellSide.RIGHT);

        // then
        assertColumnHeaderOrder(1, 2, 0, 3);
    }

    private void toggleColumnReordering() {
        selectMenuPath(COLUMN_REORDERING_PATH);
    }

    private void toggleColumnReorderListener() {
        selectMenuPath(COLUMN_REORDER_LISTENER_PATH);
    }

    private void moveColumnManuallyLeftByOne(int index) {
        selectMenuPath(new String[] { "Component", "Columns",
                "Column " + index, "Move left" });
    }

    private void assertColumnReorderEvent(boolean userOriginated) {
        final String logRow = getLogRow(0);
        assertTrue(logRow.contains("Columns reordered, userOriginated: "
                + userOriginated));
    }

    private void assertNoColumnReorderEvent() {
        final String logRow = getLogRow(0);
        assertFalse(logRow.contains("Columns reordered"));
    }

}
