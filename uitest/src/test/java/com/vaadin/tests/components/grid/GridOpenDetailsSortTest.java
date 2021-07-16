package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridOpenDetailsSortTest extends MultiBrowserTest {

    @Test
    public void sort() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertRow(grid, 0, "row3");
        assertDetails();

        grid.getHeaderCell(0, 0).click();
        assertTrue("First column should be sorted ascending",
                grid.getHeaderCell(0, 0).getAttribute("class")
                        .contains("sort-asc"));
        assertRow(grid, 0, "row1");
        assertDetails();
    }

    private void assertRow(GridElement grid, int row, String value) {
        assertEquals(String.valueOf(value), grid.getCell(row, 0).getText());
    }

    private void assertDetails() {
        List<WebElement> details = findElements(By.className("v-grid-spacer"));
        assertEquals("Unexpected amount of details,", 3, details.size());
        for (WebElement detail : details) {
            assertEquals("Unexpected detail contents,", 1,
                    detail.findElements(By.className("v-label")).size());
        }
    }
}
