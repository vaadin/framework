package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridCustomSelectionModelTest extends MultiBrowserTest {

    @Test
    public void testCustomSelectionModel() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement cell = grid.getCell(0, 0);
        assertTrue("First column of Grid should not have an input element",
                cell.findElements(By.tagName("input")).isEmpty());

        assertFalse("Row should not be selected initially",
                grid.getRow(0).isSelected());

        cell.click(5, 5);
        assertTrue("Click should select row", grid.getRow(0).isSelected());
        cell.click(5, 5);
        assertFalse("Click should deselect row", grid.getRow(0).isSelected());

        grid.sendKeys(Keys.SPACE);
        assertTrue("Space should select row", grid.getRow(0).isSelected());
        grid.sendKeys(Keys.SPACE);
        assertFalse("Space should deselect row", grid.getRow(0).isSelected());

        assertNoErrorNotifications();
    }
}
