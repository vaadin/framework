package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridDetailsWidthTest extends SingleBrowserTest {

    @Test
    public void testSpacerTDsHaveNoWidth() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Open all details rows
        grid.getCell(0, 0).click();
        checkSpacersHaveNoWidths(1);

        grid.getCell(1, 0).click();
        checkSpacersHaveNoWidths(2);

        grid.getCell(2, 0).click();
        checkSpacersHaveNoWidths(3);

        // Close all details rows
        grid.getCell(2, 0).click();
        checkSpacersHaveNoWidths(2);

        grid.getCell(1, 0).click();
        checkSpacersHaveNoWidths(1);

        grid.getCell(0, 0).click();
        checkSpacersHaveNoWidths(0);
    }

    private void checkSpacersHaveNoWidths(int expectedCount) {
        List<WebElement> spacers = findElements(By.className("v-grid-spacer"));
        assertEquals("Wrong amount of spacers visible.", expectedCount,
                spacers.size());
        for (WebElement spacer : spacers) {
            assertFalse("Spacer element had an unexpected width set.",
                    spacer.findElement(By.tagName("td")).getAttribute("style")
                            .contains("width"));
        }
    }

    @Test
    public void testDetailsOnSort() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Open a details rows
        grid.getCell(0, 0).click();

        GridCellElement cell = grid.getHeaderCell(0, 0);
        cell.click();
        cell.click();

        cell = grid.getCell(2, 0);
        WebElement spacer = findElement(By.className("v-grid-spacer"));
        assertEquals("Grid was not sorted correctly", "Hello 0",
                cell.getText());
        assertEquals("Details row was not in correct location",
                cell.getLocation().getY() + cell.getSize().getHeight(),
                spacer.getLocation().getY(), 2);

    }

}
