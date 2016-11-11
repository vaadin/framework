package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;

public class GridSelectAllTest extends GridBasicsTest {

    // TODO remove once select all is added
    @Test
    public void testSelectAllCheckBoxNotVisble() {
        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertFalse("Checkbox visible",
                header.isElementPresent(By.tagName("input")));
    }

    // TODO enable once select all is added
    // @Test
    public void testSelectAllCheckbox() {
        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < GridBasicsTest.ROWS; i += 100) {
            assertTrue("Row " + i + " was not selected.",
                    getGridElement().getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 100 was still selected",
                getGridElement().getRow(100).isSelected());
    }

    // TODO enable once select all is added
    // @Test
    public void testSelectAllAndSort() {
        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        header.findElement(By.tagName("input")).click();

        getGridElement().getHeaderCell(0, 1).click();

        WebElement selectionBox = getGridElement().getCell(4, 0)
                .findElement(By.tagName("input"));
        selectionBox.click();
        selectionBox.click();

        assertFalse("Exception occured on row reselection.", logContainsText(
                "Exception occured, java.lang.IllegalStateException: No item id for key 101 found."));
    }

    // TODO enable once select all is added
    // @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        // Single selection model shouldn't have selection column to begin with
        assertFalse(
                "Selection columnn shouldn't have been in grid for Single Selection Model",
                getGridElement().getCell(0, 1)
                        .isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));
    }

    // TODO enable once select all is added
    // @Test
    public void testSelectAllCheckboxWithHeaderOperations() {
        setSelectionModelMulti();
        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");

        GridCellElement header = getGridElement().getHeaderCell(1, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));
    }

}
