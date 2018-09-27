package com.vaadin.tests.components.grid;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridInDetailsRowTest extends MultiBrowserTest {
    @Test
    public void testNestedGridMultiRowHeaderPositions() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(2);
        row.doubleClick();

        waitForElementPresent(By.className("v-grid-spacer"));

        GridElement nestedGrid = $(GridElement.class).id("grid1");
        GridCellElement headerCell00 = nestedGrid.getHeaderCell(0, 0);
        GridCellElement headerCell11 = nestedGrid.getHeaderCell(1, 1);

        assertThat("Incorrect X-position.", headerCell11.getLocation().getX(),
                greaterThan(headerCell00.getLocation().getX()));
        assertThat("Incorrect Y-position.", headerCell11.getLocation().getY(),
                greaterThan(headerCell00.getLocation().getY()));
    }

    @Test
    public void testNestedGridRowHeights() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(2);
        row.doubleClick();

        waitForElementPresent(By.className("v-grid-spacer"));

        GridElement nestedGrid = $(GridElement.class).id("grid1");
        GridCellElement cell = nestedGrid.getCell(0, 0);

        assertThat("Incorrect row height.", cell.getSize().height,
                greaterThan(30));
    }
}
