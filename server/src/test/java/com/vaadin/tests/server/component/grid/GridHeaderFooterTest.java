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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderRow;

public class GridHeaderFooterTest {

    private Grid<String> grid;
    private Column<?, ?> column1, column2;

    @Before
    public void setUp() {
        grid = new Grid<>();

        column1 = grid.addColumn("First", s -> s.substring(0, 1));
        column2 = grid.addColumn("Rest", s -> s.substring(1));
    }

    @Test
    public void initialState_hasDefaultHeader() {
        assertEquals(1, grid.getHeaderRowCount());
        HeaderRow defaultHeader = grid.getHeaderRow(0);
        assertEquals("First", defaultHeader.getCell(column1).getText());
        assertEquals("Rest", defaultHeader.getCell(column2).getText());
    }

    @Test
    public void initialState_defaultHeaderRemovable() {
        grid.removeHeaderRow(0);
        assertEquals(0, grid.getHeaderRowCount());
    }

    @Test
    public void appendHeaderRow_addedToBottom() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.appendHeaderRow();

        assertSame(defaultRow, grid.getHeaderRow(0));
        assertSame(addedRow, grid.getHeaderRow(1));
    }

    @Test
    public void prependHeaderRow_addedToTop() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.prependHeaderRow();

        assertSame(addedRow, grid.getHeaderRow(0));
        assertSame(defaultRow, grid.getHeaderRow(1));
    }

    @Test
    public void addHeaderRowAtZero_addedToTop() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.addHeaderRowAt(0);

        assertSame(addedRow, grid.getHeaderRow(0));
        assertSame(defaultRow, grid.getHeaderRow(1));
    }

    @Test
    public void addHeaderRowAtRowCount_addedToBottom() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.addHeaderRowAt(grid.getHeaderRowCount());

        assertSame(defaultRow, grid.getHeaderRow(0));
        assertSame(addedRow, grid.getHeaderRow(1));
    }

    @Test
    public void removeExistingHeaderRow_removed() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.appendHeaderRow();

        grid.removeHeaderRow(addedRow);

        assertEquals(1, grid.getHeaderRowCount());
        assertSame(defaultRow, grid.getHeaderRow(0));
    }

    @Test
    public void removeDefaultHeaderRow_removed() {
        HeaderRow defaultRow = grid.getHeaderRow(0);
        HeaderRow addedRow = grid.appendHeaderRow();

        grid.removeHeaderRow(defaultRow);

        assertEquals(1, grid.getHeaderRowCount());
        assertSame(addedRow, grid.getHeaderRow(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getHeaderRowNegativeIndex_throws() {
        grid.getHeaderRow(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getHeaderRowIndexTooLarge_throws() {
        grid.appendHeaderRow();
        grid.getHeaderRow(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addHeaderRowAtNegativeIndex_throws() {
        grid.addHeaderRowAt(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addHeaderRowAtIndexTooLarge_throws() {
        grid.addHeaderRowAt(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeHeaderRowNegativeIndex_throws() {
        grid.removeHeaderRow(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeHeaderRowIndexTooLarge_throws() {
        grid.removeHeaderRow(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNonExistingHeaderRow_throws() {
        HeaderRow row = grid.getHeaderRow(0);
        try {
            grid.removeHeaderRow(row);
        } catch (Exception e) {
            fail("unexpected exception: " + e);
        }
        grid.removeHeaderRow(row);
    }
}
