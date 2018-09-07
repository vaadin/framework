package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GridColumnWidthWithoutHeaderTest extends SingleBrowserTest {
    public static final int THRESHOLD = 3;

    @Test
    public void testWidthWithoutHeader() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        int columnsWidth = getColWidthsRounded(grid);
        assertTrue(Math
                .abs(columnsWidth - grid.getSize().getWidth()) <= THRESHOLD);
    }

    private int getColWidthsRounded(GridElement grid) {
        GridElement.GridRowElement firstRow = grid.getRow(0);
        int width = 0;
        for (int i = 0; i < 3; i++) {
            width = width + firstRow.getCell(i).getSize().getWidth();
        }
        return width;
    }
}
