package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDragSelectionWhileScrolledTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testDragSelect() throws IOException {
        openTestURL();

        // Scroll grid to view
        GridElement grid = $(GridElement.class).first();
        ((JavascriptExecutor) getDriver())
                .executeScript("arguments[0].scrollIntoView(true);", grid);

        // Drag select 2 rows
        new Actions(getDriver()).moveToElement(grid.getCell(3, 0), 5, 5)
                .clickAndHold().moveToElement(grid.getCell(2, 0), 5, 5)
                .release().perform();

        // Assert only those are selected.
        assertTrue("Row 3 should be selected", grid.getRow(3).isSelected());
        assertTrue("Row 2 should be selected", grid.getRow(2).isSelected());
        assertFalse("Row 4 should not be selected",
                grid.getRow(4).isSelected());
        assertFalse("Row 1 should not be selected",
                grid.getRow(1).isSelected());
    }
}
