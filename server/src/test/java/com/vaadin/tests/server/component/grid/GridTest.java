package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridTest {

    private Grid<String> grid;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.addColumn("foo", Function.identity());
        grid.addColumn(String::length, new NumberRenderer());
        grid.addColumn("randomColumnId", Function.identity());
    }

    @Test
    public void testGridHeightModeChange() {
        assertEquals("Initial height mode was not CSS", HeightMode.CSS,
                grid.getHeightMode());
        grid.setHeightByRows(13.24);
        assertEquals("Setting height by rows did not change height mode",
                HeightMode.ROW, grid.getHeightMode());
        grid.setHeight("100px");
        assertEquals("Setting height did not change height mode.",
                HeightMode.CSS, grid.getHeightMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFrozenColumnCountTooBig() {
        grid.setFrozenColumnCount(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFrozenColumnCountTooSmall() {
        grid.setFrozenColumnCount(-2);
    }

    @Test()
    public void testSetFrozenColumnCount() {
        for (int i = -1; i < 2; ++i) {
            grid.setFrozenColumnCount(i);
            assertEquals("Frozen column count not updated", i,
                    grid.getFrozenColumnCount());
        }
    }

    @Test
    public void testGridColumnIdentifier() {
        grid.getColumn("foo").setCaption("Bar");
        assertEquals("Column header not updated correctly", "Bar",
                grid.getHeaderRow(0).getCell("foo").getText());
    }

    @Test
    public void testGridColumnGeneratedIdentifier() {
        assertEquals("Unexpected caption on a generated Column",
                "Generated Column0",
                grid.getColumn("generatedColumn0").getCaption());
    }

    @Test
    public void testGridColumnCaptionFromIdentifier() {
        assertEquals("Unexpected caption on a generated Column",
                "Random Column Id",
                grid.getColumn("randomColumnId").getCaption());
    }
}
