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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
        dragDefaultColumnHeader(0, 2, 10);

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
        dragDefaultColumnHeader(0, 2, 110);

        // then
        assertColumnHeaderOrder(1, 2, 0);
    }

    @Test
    public void testColumnReordering_reorderingTwiceBackForth_reordered() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);
        toggleColumnReordering();

        // when
        dragDefaultColumnHeader(2, 0, 10);

        // then
        assertColumnHeaderOrder(2, 0, 1, 3);

        // when
        dragDefaultColumnHeader(1, 3, 110);

        // then
        assertColumnHeaderOrder(2, 1, 3, 0);
    }

    @Test
    public void testColumnReordering_notEnabled_noReordering() {
        // given
        openTestURL();
        assertColumnHeaderOrder(0, 1, 2);

        // when
        dragDefaultColumnHeader(0, 2, 110);

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
        dragDefaultColumnHeader(0, 2, 10);
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
        dragDefaultColumnHeader(2, 0, 10);

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
        dragDefaultColumnHeader(0, 2, 10);

        // then
        assertColumnReorderEvent(true);
    }

    @Test
    public void testColumnReordering_addAndRemoveListener_registerUnRegisterWorks() {
        // given
        openTestURL();
        toggleColumnReordering();
        dragDefaultColumnHeader(0, 2, 10);
        assertNoColumnReorderEvent();

        // when
        toggleColumnReorderListener();
        dragDefaultColumnHeader(0, 2, 110);

        // then
        assertColumnReorderEvent(true);

        // when
        toggleColumnReorderListener();
        dragDefaultColumnHeader(0, 3, 10);

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

    private void assertColumnHeaderOrder(int... indices) {
        List<TestBenchElement> headers = getGridHeaderRowCells();
        for (int i = 0; i < indices.length; i++) {
            assertColumnHeader("Column " + indices[i], headers.get(i));
        }
    }

    private void assertColumnHeader(String expectedHeaderCaption,
            TestBenchElement testBenchElement) {
        assertEquals(expectedHeaderCaption.toLowerCase(), testBenchElement
                .getText().toLowerCase());
    }

    private WebElement getDefaultColumnHeader(int index) {
        List<TestBenchElement> headerRowCells = getGridHeaderRowCells();
        return headerRowCells.get(index);
    }

    private void dragDefaultColumnHeader(int draggedColumnHeaderIndex,
            int onTopOfColumnHeaderIndex, int xOffsetFromColumnTopLeftCorner) {
        new Actions(getDriver())
                .clickAndHold(getDefaultColumnHeader(draggedColumnHeaderIndex))
                .moveToElement(
                        getDefaultColumnHeader(onTopOfColumnHeaderIndex),
                        xOffsetFromColumnTopLeftCorner, 0).release().perform();
    }
}
