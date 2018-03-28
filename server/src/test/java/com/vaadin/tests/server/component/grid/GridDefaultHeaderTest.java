package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridDefaultHeaderTest {
    private Grid<String> grid;
    private Column<?, ?> column1, column2;

    @Before
    public void setUp() {
        grid = new Grid<>();

        column1 = grid.addColumn(ValueProvider.identity()).setId("First")
                .setCaption("First");
        column2 = grid.addColumn(ValueProvider.identity()).setId("Second")
                .setCaption("Second");
    }

    @Test
    public void initialState_hasDefaultHeader() {
        HeaderRow defaultHeader = grid.getDefaultHeaderRow();

        assertEquals(1, grid.getHeaderRowCount());
        assertSame(grid.getHeaderRow(0), defaultHeader);
        assertEquals("First", defaultHeader.getCell(column1).getText());
        assertEquals("Second", defaultHeader.getCell(column2).getText());
    }

    @Test
    public void initialState_defaultHeaderRemovable() {
        grid.removeHeaderRow(0);

        assertEquals(0, grid.getHeaderRowCount());
        assertNull(grid.getDefaultHeaderRow());
    }

    @Test
    public void initialState_updateColumnCaption_defaultHeaderUpdated() {
        column1.setCaption("1st");

        assertEquals("1st",
                grid.getDefaultHeaderRow().getCell(column1).getText());
    }

    @Test
    public void customDefaultHeader_updateColumnCaption_defaultHeaderUpdated() {
        grid.setDefaultHeaderRow(grid.appendHeaderRow());
        column1.setCaption("1st");

        assertEquals("1st",
                grid.getDefaultHeaderRow().getCell(column1).getText());
        assertEquals("First", grid.getHeaderRow(0).getCell(column1).getText());
    }

    @Test
    public void noDefaultRow_updateColumnCaption_headerNotUpdated() {
        grid.setDefaultHeaderRow(null);
        column1.setCaption("1st");

        assertEquals("First", grid.getHeaderRow(0).getCell(column1).getText());
    }

    @Test
    public void updateDefaultRow_columnCaptionUpdated() {
        grid.getDefaultHeaderRow().getCell(column1).setText("new");
        assertEquals("new", column1.getCaption());
        assertEquals("Second", column2.getCaption());
    }

    @Test
    public void updateDefaultRowWithMergedCell_columnCaptionNotUpdated() {
        HeaderCell merged = grid.getDefaultHeaderRow().join(column1, column2);
        merged.setText("new");
        assertEquals("First", column1.getCaption());
        assertEquals("Second", column2.getCaption());
    }

    @Test
    public void updateColumnCaption_defaultRowWithMergedCellNotUpdated() {
        HeaderCell merged = grid.getDefaultHeaderRow().join(column1, column2);
        merged.setText("new");
        column1.setCaption("foo");
        column2.setCaption("bar");

        assertEquals("new", merged.getText());
    }
}
