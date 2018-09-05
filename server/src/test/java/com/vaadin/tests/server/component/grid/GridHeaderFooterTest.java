package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridHeaderFooterTest {

    private Grid<String> grid;

    @Before
    public void setUp() {
        grid = new Grid<>();
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

    @Test
    public void addColumn_headerCellAdded() {

        Column<?, ?> column = grid.addColumn(ValueProvider.identity())
                .setId("Col");

        assertNotNull(grid.getHeaderRow(0).getCell(column));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeColumn_headerCellRemoved() {

        Column<String, ?> column = grid.addColumn(ValueProvider.identity())
                .setId("Col");
        grid.removeColumn(column);

        grid.getHeaderRow(0).getCell(column);
    }
}
