package com.vaadin.tests.components.grid;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridMultiselectSelectWithSpaceKeyTest extends MultiBrowserTest {

    @Test
    public void shouldSelectRowByPressingSpaceOnSelectionCheckbox() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(1, 0)
            .findElement(By.cssSelector("input[type=checkbox]")).sendKeys(Keys.SPACE);
        assertRowsSelection(true, 1);
        assertRowsSelection(false, 0, 2, 3);

        grid.getCell(2, 0)
            .findElement(By.cssSelector("input[type=checkbox]")).sendKeys(Keys.SPACE);
        assertRowsSelection(true, 1, 2);
        assertRowsSelection(false, 0, 3);

        grid.getCell(2, 0)
            .findElement(By.cssSelector("input[type=checkbox]")).sendKeys(Keys.SPACE);
        assertRowsSelection(true, 1);
        assertRowsSelection(false, 0, 2, 3);

        grid.getCell(1, 0)
            .findElement(By.cssSelector("input[type=checkbox]")).sendKeys(Keys.SPACE);
        assertRowsSelection(false, 0, 1, 2, 3);
    }

    private void assertRowsSelection(boolean selected, int... rows) {
        GridElement grid = $(GridElement.class).first();
        for (int row : rows) {
            boolean isRowSelected = grid.getRow(row).isSelected();
            if (selected) {
                assertTrue("Row " + row + " should be selected", isRowSelected);
            } else {
                assertFalse("Row " + row + " should not be selected", isRowSelected);
            }
        }
    }
}
