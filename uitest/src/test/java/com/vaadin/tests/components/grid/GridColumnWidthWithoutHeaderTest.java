package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

public class GridColumnWidthWithoutHeaderTest extends MultiBrowserTest {
    public static final int THREASHOLD = 2;

    @Test
    public void testWidthWithoutHeader() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        int columnsWidth = getColWidthsRounded(grid);
        // int gridWrapperWidth=
        // getDriver().findElement(By.className("v-grid-tablewrapper")).getSize().getWidth();
        System.out.println(grid.getSize().getWidth());
        System.out.println(columnsWidth);
        assertTrue(Math
                .abs(columnsWidth - grid.getSize().getWidth()) <= THREASHOLD);
    }

    private int getColWidthsRounded(GridElement grid) {
        GridElement.GridRowElement firstRow = grid.getRow(0);
        int width = 0;
        for (int i = 0; i < 2; i++) {
            width = width + firstRow.getCell(i).getSize().getWidth();
        }
        return width;
    }
}
