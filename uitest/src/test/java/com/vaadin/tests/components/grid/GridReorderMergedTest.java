package com.vaadin.tests.components.grid;

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
        new Actions(driver).clickAndHold(headerCell0_0)
                .moveToElement(headerCell0_4,
                        headerCell0_4.getSize().getWidth() / 3, 0)
                .release().perform();

        // ensure the first merged block got dragged over the entire second
        // merged block
        assertEquals("Unexpected column order,", "6",
                grid.getHeaderCell(1, 1).getText());
    }
}
