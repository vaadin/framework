package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.NotificationElement;

public class GridBasicStyleGeneratorTest extends GridBasicsTest {
    @Test
    public void testStyleNameGeneratorScrolling() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(
                GridBasics.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_SPECIAL);

        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);

        assertTrue(hasCssClass(row, "row2"));
        assertTrue(hasCssClass(cell, "Column_2"));

        // Scroll down and verify that the old elements don't have the
        // stylename any more

        // Carefully chosen offset to hit an index % 4 without cell style
        row = getGridElement().getRow(352);
        cell = getGridElement().getCell(353, 2);

        assertFalse(hasCssClass(row, "row352"));
        assertFalse(hasCssClass(cell, "Column_2"));
    }

    @Test
    public void testDisableStyleNameGenerator() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(
                GridBasics.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_SPECIAL);

        // Just verify that change was effective
        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);

        assertTrue(hasCssClass(row, "row2"));
        assertTrue(hasCssClass(cell, "Column_2"));

        // Disable the generator and check again
        selectRowStyleNameGenerator(GridBasics.ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_NONE);

        row = getGridElement().getRow(2);
        cell = getGridElement().getCell(3, 2);

        assertFalse(hasCssClass(row, "row2"));
        assertFalse(hasCssClass(cell, "Column_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();

        selectRowStyleNameGenerator(
                GridBasics.ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4);
        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_SPECIAL);

        // Just verify that change was effective
        GridRowElement row = getGridElement().getRow(2);
        GridCellElement cell = getGridElement().getCell(3, 2);

        assertTrue(hasCssClass(row, "row2"));
        assertTrue(hasCssClass(cell, "Column_2"));

        // Change the generator and check again
        selectRowStyleNameGenerator(GridBasics.ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(
                GridBasics.CELL_STYLE_GENERATOR_PROPERTY_TO_STRING);

        row = getGridElement().getRow(2);
        cell = getGridElement().getCell(3, 2);

        // Old styles removed?
        assertFalse(hasCssClass(row, "row2"));
        assertFalse(hasCssClass(cell, "Column_2"));

        // New style present?
        assertTrue(hasCssClass(cell, "Column-2"));
    }

    @Test
    @Ignore
    public void testCellStyleGeneratorWithSelectionColumn() {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "State", "Selection mode", "multi");

        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_SPECIAL);

        assertFalse("Error notification was present",
                isElementPresent(NotificationElement.class));
    }

    private void selectRowStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Row style generator", name);
    }

    private void selectCellStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Cell style generator", name);
    }

    @Test
    public void testEmptyStringStyleGenerator() {
        setDebug(true);
        openTestURL();

        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_EMPTY);
        selectRowStyleNameGenerator(GridBasics.ROW_STYLE_GENERATOR_EMPTY);

        assertFalse("Error notification was present",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testNullStringStyleGenerator() {
        setDebug(true);
        openTestURL();

        selectCellStyleNameGenerator(GridBasics.CELL_STYLE_GENERATOR_NULL);
        selectRowStyleNameGenerator(GridBasics.ROW_STYLE_GENERATOR_NULL);

        assertFalse("Error notification was present",
                isElementPresent(NotificationElement.class));
    }
}
