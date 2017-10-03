package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class GridRowHeightTest extends GridBasicsTest {

    @Test
    public void testSeparateRowHeights() {
        selectMenuPath("Component", "Footer", "Add default footer row");

        int initialHeaderHeight = getHeaderHeight();

        assertNotEquals("Header height should not be 50px initially", 50,
                initialHeaderHeight);

        selectMenuPath("Component", "Body rows", "Body Row Height", "" + 100);
        selectMenuPath("Component", "Header", "Header Row Height", "" + 20);
        selectMenuPath("Component", "Footer", "Footer Row Height", "" + 50);

        checkRowHeights(20, 100, 50);

        selectMenuPath("Component", "Header", "Header Row Height", "" + -1);

        checkRowHeights(initialHeaderHeight, 100, 50);
    }

    @Test
    public void testRowHeights() {
        selectMenuPath("Component", "Footer", "Add default footer row");

        int initialHeaderHeight = getHeaderHeight();
        int initialBodyRowHeight = getBodyRowHeight();
        int initialFooterHeight = getFooterHeight();

        // set automatic size and check that no change
        setRowHeight(-1);
        checkRowHeights(initialHeaderHeight, initialBodyRowHeight,
                initialFooterHeight);

        // set explicit size and check height
        setRowHeight(20);
        checkRowHeights(20, 20, 20);

        // set automatic size and check that initial size
        setRowHeight(-1);
        checkRowHeights(initialHeaderHeight, initialBodyRowHeight,
                initialFooterHeight);
    }

    private void checkRowHeights(int expectedHeaderHeight,
            int expectedBodyRowHeight, int expectedFooterHeight) {
        assertEquals("Header height does not match expected value",
                expectedHeaderHeight, getHeaderHeight());
        assertEquals("Body row height does not match expected value",
                expectedBodyRowHeight, getBodyRowHeight());
        assertEquals("Footer height does not match expected value",
                expectedFooterHeight, getFooterHeight());
    }

    private void setRowHeight(int height) {
        selectMenuPath("Component", "Size", "Row Height", "" + height);
    }

    private int getHeaderHeight() {
        return getGridElement().getHeader().getSize().getHeight();
    }

    private int getBodyRowHeight() {
        return getGridElement().getRow(0).getSize().getHeight();
    }

    private int getFooterHeight() {
        return getGridElement().getFooter().getSize().getHeight();
    }

}
