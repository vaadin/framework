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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

@TestCategory("grid")
public class GridColumnVisibilityTest extends GridBasicFeaturesTest {

    private static final String[] TOGGLE_LISTENER = new String[] { "Component",
            "State", "ColumnVisibilityChangeListener" };
    private static final String[] TOGGLE_HIDE_COLUMN_0 = new String[] {
            "Component", "Columns", "Column 0", "Hidden" };

    private static final String COLUMN_0_BECAME_HIDDEN_MSG = "Visibility "
            + "changed: propertyId: Column 0, isHidden: true";
    private static final String COLUMN_0_BECAME_UNHIDDEN_MSG = "Visibility "
            + "changed: propertyId: Column 0, isHidden: false";
    private static final String USER_ORIGINATED_TRUE = "userOriginated: true";
    private static final String USER_ORIGINATED_FALSE = "userOriginated: false";

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void columnIsNotShownWhenHidden() {
        assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertEquals("column 1", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());
    }

    @Test
    public void columnIsShownWhenUnhidden() {
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertEquals("column 0", getGridElement().getHeaderCell(0, 0).getText()
                .toLowerCase());
    }

    @Test
    public void registeringListener() {
        assertFalse(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));
        selectMenuPath(TOGGLE_LISTENER);
        assertFalse(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertTrue(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));
        assertTrue(logContainsText(USER_ORIGINATED_FALSE));

        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertTrue(logContainsText(COLUMN_0_BECAME_UNHIDDEN_MSG));
        assertTrue(logContainsText(USER_ORIGINATED_FALSE));
    }

    @Test
    public void deregisteringListener() {
        selectMenuPath(TOGGLE_LISTENER);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);

        selectMenuPath(TOGGLE_LISTENER);
        selectMenuPath(TOGGLE_HIDE_COLUMN_0);
        assertFalse(logContainsText(COLUMN_0_BECAME_UNHIDDEN_MSG));
    }

    @Test
    public void testColumnHiding_userOriginated_correctParams() {
        selectMenuPath(TOGGLE_LISTENER);
        toggleColumnHidable(0);
        assertColumnHeaderOrder(0, 1, 2, 3);

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();

        assertColumnHeaderOrder(1, 2, 3);
        assertTrue(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));
        assertTrue(logContainsText(USER_ORIGINATED_TRUE));

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();

        assertColumnHeaderOrder(0, 1, 2, 3);
        assertTrue(logContainsText(COLUMN_0_BECAME_UNHIDDEN_MSG));
        assertTrue(logContainsText(USER_ORIGINATED_TRUE));

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getSidebarOpenButton().click();

        assertColumnHeaderOrder(1, 2, 3);
        assertTrue(logContainsText(COLUMN_0_BECAME_HIDDEN_MSG));
        assertTrue(logContainsText(USER_ORIGINATED_TRUE));
    }

    @Test
    public void testColumnHiding_whenHidableColumnRemoved_toggleRemoved() {
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        assertNotNull(getColumnHidingToggle(0));

        addRemoveColumn(0);

        assertNull(getColumnHidingToggle(0));
    }

    @Test
    public void testColumnHiding_whenHidableColumnAdded_toggleWithCorrectCaptionAdded() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        toggleColumnHidingToggleCaptionChange(0);
        getSidebarOpenButton().click();
        assertEquals("Column 0 caption 0", getColumnHidingToggle(0).getText());
        getSidebarOpenButton().click();

        addRemoveColumn(0);
        addRemoveColumn(4);
        addRemoveColumn(5);
        addRemoveColumn(6);
        addRemoveColumn(7);
        addRemoveColumn(8);
        addRemoveColumn(9);
        addRemoveColumn(10);
        assertColumnHeaderOrder(1, 2, 3, 11);

        getSidebarOpenButton().click();
        assertNull(getColumnHidingToggle(0));
        getSidebarOpenButton().click();

        addRemoveColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 11, 0);

        getSidebarOpenButton().click();
        assertEquals("Column 0 caption 0", getColumnHidingToggle(0).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingToggleCaption_updatesToggle() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText()
                .toLowerCase());
        assertEquals("Column 1", getColumnHidingToggle(1).getText());

        toggleColumnHidingToggleCaptionChange(1);

        getSidebarOpenButton().click();
        assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText()
                .toLowerCase());
        assertEquals("Column 1 caption 0", getColumnHidingToggle(1).getText());

        toggleColumnHidingToggleCaptionChange(1);
        getSidebarOpenButton().click();
        assertEquals("Column 1 caption 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToHeader_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        assertEquals("column 1", getGridElement().getHeaderCell(0, 1).getText()
                .toLowerCase());
        assertEquals("Column 1", getColumnHidingToggle(1).getText());

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getSidebarOpenButton().click();
        assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_settingColumnHeaderCaption_toggleCaptionIsEqual() {
        toggleColumnHidable(1);

        selectMenuPath("Component", "Columns", "Column 1",
                "Change header caption");

        getSidebarOpenButton().click();
        assertEquals("column 1 header 0", getGridElement().getHeaderCell(0, 1)
                .getText().toLowerCase());
        assertEquals("Column 1 header 0", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testColumnHidingToggleCaption_explicitlySet_toggleCaptionIsNotOverridden() {
        toggleColumnHidable(1);

        selectMenuPath("Component", "Columns", "Column 1",
                "Change hiding toggle caption");
        selectMenuPath("Component", "Columns", "Column 1",
                "Change header caption");

        getSidebarOpenButton().click();
        assertEquals("column 1 header 0", getGridElement().getHeaderCell(0, 1)
                .getText().toLowerCase());
        assertEquals("Column 1 caption 0", getColumnHidingToggle(1).getText());
    }

    private void toggleColumnHidingToggleCaptionChange(int index) {
        selectMenuPath("Component", "Columns", "Column " + index,
                "Change hiding toggle caption");
    }

    @Test
    public void testFrozenColumnHiding_hiddenColumnMadeFrozen_frozenWhenMadeVisible() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getColumnHidingToggle(1).click();

        assertColumnHeaderOrder(2, 3, 4, 5);

        setFrozenColumns(2);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        assertColumnHeaderOrder(0, 2, 3, 4, 5);
        verifyColumnFrozen(0);
        verifyColumnNotFrozen(1);

        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);
        verifyColumnFrozen(0);
        verifyColumnFrozen(1);
        verifyColumnNotFrozen(2);
    }

    @Test
    public void testFrozenColumnHiding_hiddenFrozenColumnUnfrozen_notFrozenWhenMadeVisible() {
        selectMenuPath("Component", "Size", "Width", "100%");
        toggleColumnHidable(0);
        toggleColumnHidable(1);
        setFrozenColumns(2);
        verifyColumnFrozen(0);
        verifyColumnFrozen(1);
        verifyColumnNotFrozen(2);
        verifyColumnNotFrozen(3);

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);

        setFrozenColumns(0);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);

        getSidebarOpenButton().click();
        getColumnHidingToggle(0).click();
        assertColumnHeaderOrder(0, 2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);

        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5);
        verifyColumnNotFrozen(0);
        verifyColumnNotFrozen(1);
        verifyColumnNotFrozen(2);
    }

    private void verifyColumnFrozen(int index) {
        assertTrue(getGridElement().getHeaderCell(0, index).isFrozen());
    }

    private void verifyColumnNotFrozen(int index) {
        assertFalse(getGridElement().getHeaderCell(0, index).isFrozen());
    }

    private void toggleColumnHidable(int index) {
        selectMenuPath("Component", "Columns", "Column " + index, "Hidable");
    }

    private void addRemoveColumn(int index) {
        selectMenuPath("Component", "Columns", "Column " + index,
                "Add / Remove");
    }
}
