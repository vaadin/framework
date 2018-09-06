package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

public class GridColumnWidthWithoutHeaderTest extends MultiBrowserTest {
    public static final int THREASHOLD = 3;

    @Test
    public void testWidthWithoutHeader() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        int columnsWidth = getColWidthsRounded(grid);
        assertTrue(Math
                .abs(columnsWidth - grid.getSize().getWidth()) <= THREASHOLD);
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
