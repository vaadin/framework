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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;

public class GridColumns {

    private Grid grid;

    private GridState state;

    private Method getStateMethod;

    private Field columnIdGeneratorField;

    private KeyMapper<Object> columnIdMapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        IndexedContainer ds = new IndexedContainer();
        for (int c = 0; c < 10; c++) {
            ds.addContainerProperty("column" + c, String.class, "");
        }
        ds.addContainerProperty("noSort", Object.class, null);
        grid = new Grid(ds);

        getStateMethod = Grid.class.getDeclaredMethod("getState");
        getStateMethod.setAccessible(true);

        state = (GridState) getStateMethod.invoke(grid);

        columnIdGeneratorField = Grid.class.getDeclaredField("columnKeys");
        columnIdGeneratorField.setAccessible(true);

        columnIdMapper = (KeyMapper<Object>) columnIdGeneratorField.get(grid);
    }

    @Test
    public void testColumnGeneration() throws Exception {

        for (Object propertyId : grid.getContainerDataSource()
                .getContainerPropertyIds()) {

            // All property ids should get a column
            Column column = grid.getColumn(propertyId);
            assertNotNull(column);

            // Humanized property id should be the column header by default
            assertEquals(
                    SharedUtil.camelCaseToHumanFriendly(propertyId.toString()),
                    grid.getDefaultHeaderRow().getCell(propertyId).getText());
        }
    }

    @Test
    public void testModifyingColumnProperties() throws Exception {

        // Modify first column
        Column column = grid.getColumn("column1");
        assertNotNull(column);

        column.setHeaderCaption("CustomHeader");
        assertEquals("CustomHeader", column.getHeaderCaption());
        assertEquals(column.getHeaderCaption(), grid.getDefaultHeaderRow()
                .getCell("column1").getText());

        column.setWidth(100);
        assertEquals(100, column.getWidth(), 0.49d);
        assertEquals(column.getWidth(), getColumnState("column1").width, 0.49d);

        try {
            column.setWidth(-1);
            fail("Setting width to -1 should throw exception");
        } catch (IllegalArgumentException iae) {
            // expected
        }

        assertEquals(100, column.getWidth(), 0.49d);
        assertEquals(100, getColumnState("column1").width, 0.49d);
    }

    @Test
    public void testRemovingColumnByRemovingPropertyFromContainer()
            throws Exception {

        Column column = grid.getColumn("column1");
        assertNotNull(column);

        // Remove column
        grid.getContainerDataSource().removeContainerProperty("column1");

        try {
            column.setHeaderCaption("asd");

            fail("Succeeded in modifying a detached column");
        } catch (IllegalStateException ise) {
            // Detached state should throw exception
        }

        try {
            column.setWidth(123);
            fail("Succeeded in modifying a detached column");
        } catch (IllegalStateException ise) {
            // Detached state should throw exception
        }

        assertNull(grid.getColumn("column1"));
        assertNull(getColumnState("column1"));
    }

    @Test
    public void testAddingColumnByAddingPropertyToContainer() throws Exception {
        grid.getContainerDataSource().addContainerProperty("columnX",
                String.class, "");
        Column column = grid.getColumn("columnX");
        assertNotNull(column);
    }

    @Test
    public void testHeaderVisiblility() throws Exception {

        assertTrue(grid.isHeaderVisible());
        assertTrue(state.header.visible);

        grid.setHeaderVisible(false);
        assertFalse(grid.isHeaderVisible());
        assertFalse(state.header.visible);

        grid.setHeaderVisible(true);
        assertTrue(grid.isHeaderVisible());
        assertTrue(state.header.visible);
    }

    @Test
    public void testFooterVisibility() throws Exception {

        assertTrue(grid.isFooterVisible());
        assertTrue(state.footer.visible);

        grid.setFooterVisible(false);
        assertFalse(grid.isFooterVisible());
        assertFalse(state.footer.visible);

        grid.setFooterVisible(true);
        assertTrue(grid.isFooterVisible());
        assertTrue(state.footer.visible);
    }

    @Test
    public void testSetFrozenColumnCount() {
        assertEquals("Grid should not start with a frozen column", 0,
                grid.getFrozenColumnCount());
        grid.setFrozenColumnCount(2);
        assertEquals("Freezing two columns should freeze two columns", 2,
                grid.getFrozenColumnCount());
    }

    @Test
    public void testSetFrozenColumnCountThroughColumn() {
        assertEquals("Grid should not start with a frozen column", 0,
                grid.getFrozenColumnCount());
        grid.getColumns().get(2).setLastFrozenColumn();
        assertEquals(
                "Setting the third column as last frozen should freeze three columns",
                3, grid.getFrozenColumnCount());
    }

    @Test
    public void testFrozenColumnRemoveColumn() {
        assertEquals("Grid should not start with a frozen column", 0,
                grid.getFrozenColumnCount());

        int containerSize = grid.getContainerDataSource()
                .getContainerPropertyIds().size();
        grid.setFrozenColumnCount(containerSize);

        Object propertyId = grid.getContainerDataSource()
                .getContainerPropertyIds().iterator().next();

        grid.getContainerDataSource().removeContainerProperty(propertyId);
        assertEquals(
                "Frozen column count should update when removing last row",
                containerSize - 1, grid.getFrozenColumnCount());
    }

    @Test
    public void testReorderColumns() {
        Set<?> containerProperties = new LinkedHashSet<Object>(grid
                .getContainerDataSource().getContainerPropertyIds());
        Object[] properties = new Object[] { "column3", "column2", "column6" };
        grid.setColumnOrder(properties);

        int i = 0;
        // Test sorted columns are first in order
        for (Object property : properties) {
            containerProperties.remove(property);
            assertEquals(columnIdMapper.key(property),
                    state.columnOrder.get(i++));
        }

        // Test remaining columns are in original order
        for (Object property : containerProperties) {
            assertEquals(columnIdMapper.key(property),
                    state.columnOrder.get(i++));
        }

        try {
            grid.setColumnOrder("foo", "bar", "baz");
            fail("Grid allowed sorting with non-existent properties");
        } catch (IllegalArgumentException e) {
            // All ok
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveColumnThatDoesNotExist() {
        grid.removeColumn("banana phone");
    }

    @Test(expected = IllegalStateException.class)
    public void testSetNonSortableColumnSortable() {
        Column noSortColumn = grid.getColumn("noSort");
        assertFalse("Object property column should not be sortable.",
                noSortColumn.isSortable());
        noSortColumn.setSortable(true);
    }

    @Test
    public void testColumnsEditableByDefault() {
        for (Column c : grid.getColumns()) {
            assertTrue(c + " should be editable", c.isEditable());
        }
    }

    @Test
    public void testPropertyAndColumnEditorFieldsMatch() {
        Column column1 = grid.getColumn("column1");
        column1.setEditorField(new TextField());
        assertSame(column1.getEditorField(), grid.getColumn("column1")
                .getEditorField());

        Column column2 = grid.getColumn("column2");
        column2.setEditorField(new TextField());
        assertSame(column2.getEditorField(), column2.getEditorField());
    }

    @Test
    public void testUneditableColumnHasNoField() {
        Column col = grid.getColumn("column1");

        col.setEditable(false);

        assertFalse("Column should be uneditable", col.isEditable());
        assertNull("Uneditable column should not be auto-assigned a Field",
                col.getEditorField());
    }

    private GridColumnState getColumnState(Object propertyId) {
        String columnId = columnIdMapper.key(propertyId);
        for (GridColumnState columnState : state.columns) {
            if (columnState.id.equals(columnId)) {
                return columnState;
            }
        }
        return null;
    }

    @Test
    public void testAddAndRemoveSortableColumn() {
        boolean sortable = grid.getColumn("column1").isSortable();
        grid.removeColumn("column1");
        grid.addColumn("column1");
        assertEquals("Column sortability changed when re-adding", sortable,
                grid.getColumn("column1").isSortable());
    }

    @Test
    public void testSetColumns() {
        grid.setColumns("column7", "column0", "column9");
        Iterator<Column> it = grid.getColumns().iterator();
        assertEquals(it.next().getPropertyId(), "column7");
        assertEquals(it.next().getPropertyId(), "column0");
        assertEquals(it.next().getPropertyId(), "column9");
        assertFalse(it.hasNext());
    }

    @Test
    public void testAddingColumnsWithSetColumns() {
        Grid g = new Grid();
        g.setColumns("c1", "c2", "c3");
        Iterator<Column> it = g.getColumns().iterator();
        assertEquals(it.next().getPropertyId(), "c1");
        assertEquals(it.next().getPropertyId(), "c2");
        assertEquals(it.next().getPropertyId(), "c3");
        assertFalse(it.hasNext());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddingColumnsWithSetColumnsNonDefaultContainer() {
        grid.setColumns("column1", "column2", "column50");
    }

    @Test
    public void testDefaultColumnHidingToggleCaption() {
        Column firstColumn = grid.getColumns().get(0);
        firstColumn.setHeaderCaption("headerCaption");
        assertEquals(null, firstColumn.getHidingToggleCaption());
    }

    @Test
    public void testOverriddenColumnHidingToggleCaption() {
        Column firstColumn = grid.getColumns().get(0);
        firstColumn.setHidingToggleCaption("hidingToggleCaption");
        firstColumn.setHeaderCaption("headerCaption");
        assertEquals("hidingToggleCaption",
                firstColumn.getHidingToggleCaption());
    }
}
