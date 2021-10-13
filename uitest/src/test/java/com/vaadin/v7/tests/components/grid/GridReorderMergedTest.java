package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridReorderMergedTest extends MultiBrowserTest {

    @Test
    public void dragMerged() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell0_0 = grid.getHeaderCell(0, 0);
        GridCellElement headerCell0_4 = grid.getHeaderCell(0, 4);
        new Actions(driver).dragAndDrop(headerCell0_0, headerCell0_4).perform();

        // ensure the first merged block got dragged over the entire second
        // merged block
        assertEquals("Unexpected column order,", "6",
                grid.getHeaderCell(1, 1).getText());
    }

    @Test
    public void dragMergedReverse() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell0_0 = grid.getHeaderCell(0, 0);
        GridCellElement headerCell0_4 = grid.getHeaderCell(0, 4);
        new Actions(driver).clickAndHold(headerCell0_4)
                .moveByOffset(-headerCell0_0.getSize().getWidth(),
                        headerCell0_0.getSize().getHeight() / 2)
                .release().perform();

        // ensure the second merged block got dragged over the first merged
        // block entirely
        assertEquals("Unexpected column order,", "6",
                grid.getHeaderCell(1, 1).getText());
    }
}
