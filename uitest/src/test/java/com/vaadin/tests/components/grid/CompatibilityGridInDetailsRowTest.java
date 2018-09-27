package com.vaadin.tests.components.grid;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CompatibilityGridInDetailsRowTest extends MultiBrowserTest {
    @Test
    public void testNestedGridMultiRowHeaderPositions() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(1);
        row.doubleClick();

        waitForElementPresent(By.className("v-grid-spacer"));

        GridElement nestedGrid = $(GridElement.class).id("grid2");
        assertEquals("Incorrect header row count.", 2,
                nestedGrid.getHeaderCount());
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
        GridRowElement row = grid.getRow(1);
        row.doubleClick();

        waitForElementPresent(By.className("v-grid-spacer"));

        GridElement nestedGrid = $(GridElement.class).id("grid2");
        grid.findElement(By.className("v-grid-sidebar-button")).click();

        assertNotNull(
                "There are no options for toggling column visibility but there should be.",
                getColumnHidingToggle(nestedGrid));
    }

    /**
     * Returns the first toggle inside the sidebar for hiding a column, or null
     * if not found.
     */
    protected WebElement getColumnHidingToggle(GridElement grid) {
        WebElement sidebar = findElement(By.className("v-grid-sidebar-popup"));
        List<WebElement> elements = sidebar
                .findElements(By.className("column-hiding-toggle"));
        for (WebElement e : elements) {
            return e;
        }
        return null;
    }
}
