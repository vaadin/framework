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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.components.grid.ColumnGroup;
import com.vaadin.ui.components.grid.ColumnGroupRow;
import com.vaadin.ui.components.grid.Grid;

public class GridColumnGroups {

    private Grid grid;

    private GridState state;

    private Method getStateMethod;

    private Field columnIdGeneratorField;

    private KeyMapper<Object> columnIdMapper;

    @Before
    public void setup() throws Exception {
        IndexedContainer ds = new IndexedContainer();
        for (int c = 0; c < 10; c++) {
            ds.addContainerProperty("column" + c, String.class, "");
        }
        grid = new Grid(ds);

        getStateMethod = Grid.class.getDeclaredMethod("getState");
        getStateMethod.setAccessible(true);

        state = (GridState) getStateMethod.invoke(grid);

        columnIdGeneratorField = Grid.class.getDeclaredField("columnKeys");
        columnIdGeneratorField.setAccessible(true);

        columnIdMapper = (KeyMapper<Object>) columnIdGeneratorField.get(grid);
    }

    @Test
    public void testColumnGroupRows() throws Exception {

        // No column group rows by default
        List<ColumnGroupRow> rows = grid.getColumnGroupRows();
        assertEquals(0, rows.size());

        // Add some rows
        ColumnGroupRow row1 = grid.addColumnGroupRow();
        ColumnGroupRow row3 = grid.addColumnGroupRow();
        ColumnGroupRow row2 = grid.addColumnGroupRow(1);

        rows = grid.getColumnGroupRows();
        assertEquals(3, rows.size());
        assertEquals(row1, rows.get(0));
        assertEquals(row2, rows.get(1));
        assertEquals(row3, rows.get(2));

        // Header should be visible by default, footer should not
        assertTrue(row1.isHeaderVisible());
        assertFalse(row1.isFooterVisible());

        row1.setHeaderVisible(false);
        assertFalse(row1.isHeaderVisible());
        row1.setHeaderVisible(true);
        assertTrue(row1.isHeaderVisible());

        row1.setFooterVisible(true);
        assertTrue(row1.isFooterVisible());
        row1.setFooterVisible(false);
        assertFalse(row1.isFooterVisible());

        row1.setHeaderVisible(true);
        row1.setFooterVisible(true);
        assertTrue(row1.isHeaderVisible());
        assertTrue(row1.isFooterVisible());

        row1.setHeaderVisible(false);
        row1.setFooterVisible(false);
        assertFalse(row1.isHeaderVisible());
        assertFalse(row1.isFooterVisible());
    }

    @Test
    public void testColumnGroupsInState() throws Exception {

        // Add a new row
        ColumnGroupRow row = grid.addColumnGroupRow();
        assertTrue(state.columnGroupRows.size() == 1);

        // Add a group by property id
        ColumnGroup columns12 = row.addGroup("column1", "column2");
        assertTrue(state.columnGroupRows.get(0).groups.size() == 1);

        // Set header of column
        columns12.setHeaderCaption("Column12");
        assertEquals("Column12",
                state.columnGroupRows.get(0).groups.get(0).header);

        // Set footer of column
        columns12.setFooterCaption("Footer12");
        assertEquals("Footer12",
                state.columnGroupRows.get(0).groups.get(0).footer);

        // Add another group by column instance
        ColumnGroup columns34 = row.addGroup(grid.getColumn("column3"),
                grid.getColumn("column4"));
        assertTrue(state.columnGroupRows.get(0).groups.size() == 2);

        // add another group row
        ColumnGroupRow row2 = grid.addColumnGroupRow();
        assertTrue(state.columnGroupRows.size() == 2);

        // add a group by combining the two previous groups
        ColumnGroup columns1234 = row2.addGroup(columns12, columns34);
        assertTrue(columns1234.getColumns().size() == 4);

        // Insert a group as the second group
        ColumnGroupRow newRow2 = grid.addColumnGroupRow(1);
        assertTrue(state.columnGroupRows.size() == 3);
    }

    @Test
    public void testAddingColumnGroups() throws Exception {

        ColumnGroupRow row = grid.addColumnGroupRow();

        // By property id
        ColumnGroup columns01 = row.addGroup("column0", "column1");
        assertEquals(2, columns01.getColumns().size());
        assertEquals("column0", columns01.getColumns().get(0));
        assertTrue(columns01.isColumnInGroup("column0"));
        assertEquals("column1", columns01.getColumns().get(1));
        assertTrue(columns01.isColumnInGroup("column1"));

        // By grid column
        ColumnGroup columns23 = row.addGroup(grid.getColumn("column2"),
                grid.getColumn("column3"));
        assertEquals(2, columns23.getColumns().size());
        assertEquals("column2", columns23.getColumns().get(0));
        assertTrue(columns23.isColumnInGroup("column2"));
        assertEquals("column3", columns23.getColumns().get(1));
        assertTrue(columns23.isColumnInGroup("column3"));

        // Combine groups
        ColumnGroupRow row2 = grid.addColumnGroupRow();
        ColumnGroup columns0123 = row2.addGroup(columns01, columns23);
        assertEquals(4, columns0123.getColumns().size());
        assertEquals("column0", columns0123.getColumns().get(0));
        assertTrue(columns0123.isColumnInGroup("column0"));
        assertEquals("column1", columns0123.getColumns().get(1));
        assertTrue(columns0123.isColumnInGroup("column1"));
        assertEquals("column2", columns0123.getColumns().get(2));
        assertTrue(columns0123.isColumnInGroup("column2"));
        assertEquals("column3", columns0123.getColumns().get(3));
        assertTrue(columns0123.isColumnInGroup("column3"));
    }

    @Test
    public void testColumnGroupHeadersAndFooters() throws Exception {

        ColumnGroupRow row = grid.addColumnGroupRow();
        ColumnGroup group = row.addGroup("column1", "column2");

        // Header
        assertNull(group.getHeaderCaption());
        group.setHeaderCaption("My header");
        assertEquals("My header", group.getHeaderCaption());
        group.setHeaderCaption(null);
        assertNull(group.getHeaderCaption());

        // Footer
        assertNull(group.getFooterCaption());
        group.setFooterCaption("My footer");
        assertEquals("My footer", group.getFooterCaption());
        group.setFooterCaption(null);
        assertNull(group.getFooterCaption());
    }

    @Test
    public void testColumnGroupDetachment() throws Exception {

        ColumnGroupRow row = grid.addColumnGroupRow();
        ColumnGroup group = row.addGroup("column1", "column2");

        // Remove group
        row.removeGroup(group);

        try {
            group.setHeaderCaption("Header");
            fail("Should throw exception for setting header caption on detached group");
        } catch (IllegalStateException ise) {

        }

        try {
            group.setFooterCaption("Footer");
            fail("Should throw exception for setting footer caption on detached group");
        } catch (IllegalStateException ise) {

        }
    }

    @Test
    public void testColumnGroupLimits() throws Exception {

        ColumnGroupRow row = grid.addColumnGroupRow();
        row.addGroup("column1", "column2");
        row.addGroup("column3", "column4");

        try {
            row.addGroup("column2", "column3");
            fail("Adding a group with already grouped properties should throw exception");
        } catch (IllegalArgumentException iae) {

        }

        ColumnGroupRow row2 = grid.addColumnGroupRow();

        try {
            row2.addGroup("column2", "column3");
            fail("Adding a group that breaks previous grouping boundaries should throw exception");
        } catch (IllegalArgumentException iae) {

        }

        // This however should not throw an exception as it spans completely
        // over the parent rows groups
        row2.addGroup("column1", "column2", "column3", "column4");

    }
}
