package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridWithBrokenRendererTest extends SingleBrowserTest {

    @Test
    public void ensureRendered() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertRow(grid, 0, "FI", "", "Finland");
        assertRow(grid, 1, "SE", "", "Sweden");
    }

    private void assertRow(GridElement grid, int row, String... texts) {
        for (int column = 0; column < texts.length; column++) {
            Assert.assertEquals("Cell " + row + "," + column, texts[column],
                    grid.getCell(row, column).getText());
        }

    }
}
