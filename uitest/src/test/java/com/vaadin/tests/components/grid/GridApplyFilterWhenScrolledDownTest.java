package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridApplyFilterWhenScrolledDownTest extends MultiBrowserTest {

    @Test
    public void scrolledCorrectly() throws InterruptedException {
        openTestURL();
        final GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(50);
        $(ButtonElement.class).first().click();
        final TestBenchElement gridBody = grid.getBody();
        // Can't use element API because it scrolls
        waitUntil(input -> gridBody.findElements(By.className("v-grid-row"))
                .size() == 1);
        WebElement cell = gridBody.findElements(By.className("v-grid-cell"))
                .get(0);
        assertEquals("Test", cell.getText());

        int gridHeight = grid.getSize().getHeight();
        int scrollerHeight = grid.getVerticalScroller().getSize().getHeight();
        assertTrue(
                "Scroller height is " + scrollerHeight
                        + ", should be smaller than grid height: " + gridHeight,
                scrollerHeight < gridHeight);
    }
}
