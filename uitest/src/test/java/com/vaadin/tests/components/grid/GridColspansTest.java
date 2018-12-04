package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridColspansTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void testColSpans() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("5", grid.getHeaderCell(0, 1).getAttribute("colspan"));
        assertEquals("2", grid.getHeaderCell(1, 1).getAttribute("colspan"));
        assertEquals("3", grid.getHeaderCell(1, 3).getAttribute("colspan"));

        assertEquals("5", grid.getFooterCell(1, 1).getAttribute("colspan"));
        assertEquals("2", grid.getFooterCell(0, 1).getAttribute("colspan"));
        assertEquals("3", grid.getFooterCell(0, 3).getAttribute("colspan"));
    }

    @Test
    public void testHideFirstColumnOfColspan() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Failed initial condition.", "all the stuff",
                grid.getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        assertEquals("Failed initial condition.", "first name",
                grid.getHeaderCell(2, 1).getText().toLowerCase(Locale.ROOT));
        $(ButtonElement.class).caption("Show/Hide firstName").first().click();
        assertEquals("Header text changed on column hide.", "all the stuff",
                grid.getHeaderCell(0, 1).getText().toLowerCase(Locale.ROOT));
        assertEquals("Failed initial condition.", "last name",
                grid.getHeaderCell(2, 1).getText().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testHideAndReAddFirstCOlumn() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertEquals("Failed initial condition.", "first name",
                grid.getHeaderCell(2, 1).getText().toLowerCase(Locale.ROOT));

        $(ButtonElement.class).caption("Show/Hide firstName").first().click();

        assertEquals("Failed initial condition.", "last name",
                grid.getHeaderCell(2, 1).getText().toLowerCase(Locale.ROOT));

        $(ButtonElement.class).caption("Show/Hide firstName").first().click();

        assertEquals("Failed to find first name in last column", "first name",
                grid.getHeaderCell(2, 5).getText().toLowerCase(Locale.ROOT));

    }

    @Test
    public void testSplittingMergedHeaders() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell = grid.getHeaderCell(1, 1);
        assertEquals("Failed initial condition.", "full name",
                headerCell.getText().toLowerCase(Locale.ROOT));
        assertEquals("Failed initial condition.", "first name",
                grid.getHeaderCell(2, 1).getText().toLowerCase(Locale.ROOT));
        $(ButtonElement.class).get(1).click();
        headerCell = grid.getHeaderCell(1, 1);
        assertEquals("Joined Header text not changed on column reorder.",
                "misc", headerCell.getText().toLowerCase(Locale.ROOT));
        assertEquals("Unexpected colspan", "1",
                headerCell.getAttribute("colspan"));
        headerCell = grid.getHeaderCell(1, 2);
        assertEquals("Header text not changed on column reorder", "full name",
                headerCell.getText().toLowerCase(Locale.ROOT));
        assertEquals("Unexpected colspan", "2",
                headerCell.getAttribute("colspan"));

        assertFalse("Error indicator not present",
                isElementPresent(By.className("v-errorindicator")));

    }
}
