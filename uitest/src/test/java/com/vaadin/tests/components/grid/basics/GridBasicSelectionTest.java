package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.customelements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;

public class GridBasicSelectionTest extends GridBasicsTest {

    @Test
    public void testKeyboardWithSingleSelection() {

        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();

        assertTrue("Grid row 3 was not selected with clicking.",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.",
                !grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.",
                grid.getRow(3).isSelected());

        grid.scrollToRow(500);

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.",
                !grid.getRow(3).isSelected());
    }

    @Test
    public void testSingleSelectionUpdatesFromServer() {
        GridElement grid = getGridElement();
        assertFalse("First row was selected from start",
                grid.getRow(0).isSelected());
        toggleFirstRowSelection();
        assertTrue("First row was not selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Selected: DataObject[0]"));
        grid.getCell(5, 0).click();
        assertTrue("Fifth row was not selected.", getRow(5).isSelected());
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Selected: DataObject[5]"));
        grid.getCell(0, 6).click();
        assertTrue("Selection event was not correct",
                logContainsText("Selected: DataObject[0]"));
        toggleFirstRowSelection();
        assertTrue("Selection event was not correct",
                logContainsText("Selected: null"));
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertFalse("Fifth row was still selected.", getRow(5).isSelected());

        grid.scrollToRow(600);
        grid.getCell(595, 3).click();
        assertTrue("Row 595 was not selected.", getRow(595).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Selected: DataObject[595]"));
        toggleFirstRowSelection();
        assertFalse("Row 595 was still selected.", getRow(595).isSelected());
        assertTrue("First row was not selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Selected: DataObject[0]"));
    }

    private void toggleFirstRowSelection() {
        selectMenuPath("Component", "Body rows", "Toggle first row selection");
    }

    private GridRowElement getRow(int i) {
        return getGridElement().getRow(i);
    }
}
