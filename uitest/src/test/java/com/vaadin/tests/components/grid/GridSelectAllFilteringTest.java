package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSelectAllFilteringTest extends MultiBrowserTest {

    @Test
    public void checkSelectAll() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        ButtonElement toggleButton = $(ButtonElement.class).id("toggle");
        ButtonElement checkButton = $(ButtonElement.class).id("check");

        // ensure no initial selection
        checkButton.click();
        assertEquals("Unexpected log entry,", "1. selected 0", getLogRow(0));
        assertEquals("Unexpected amount of visually selected rows,", 0,
                grid.findElements(By.className("v-grid-row-selected")).size());

        // select all
        WebElement selectAllCheckbox = grid
                .findElement(By.className("v-grid-select-all-checkbox"));
        selectAllCheckbox.click();

        // ensure only the two visible rows get selected
        checkButton.click();
        assertEquals("Unexpected log entry,",
                "2. selected 2: Nicolaus Copernicus, Galileo Galilei",
                getLogRow(0));
        assertEquals("Unexpected amount of visually selected rows,", 2,
                grid.findElements(By.className("v-grid-row-selected")).size());

        // toggle filter
        toggleButton.click();

        // ensure selection did not change but only one selected row is visible
        checkButton.click();
        assertEquals("Unexpected log entry,",
                "3. selected 2: Nicolaus Copernicus, Galileo Galilei",
                getLogRow(0));
        assertEquals("Unexpected amount of visually selected rows,", 1,
                grid.findElements(By.className("v-grid-row-selected")).size());

        // remove all selections
        selectAllCheckbox.click();

        // ensure all selections got removed whether they were visible or not
        checkButton.click();
        assertEquals("Unexpected log entry,", "4. selected 0", getLogRow(0));
        assertEquals("Unexpected amount of visually selected rows,", 0,
                grid.findElements(By.className("v-grid-row-selected")).size());
    }
}
