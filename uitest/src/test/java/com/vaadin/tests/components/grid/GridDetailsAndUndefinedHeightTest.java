package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDetailsAndUndefinedHeightTest extends MultiBrowserTest {

    @Test
    public void changingSelectionClosesOldDetails() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(4, 0).click();
        waitForElementPresent(By.className("v-grid-spacer"));

        List<WebElement> spacers = grid
                .findElements(By.className("v-grid-spacer"));
        assertEquals("Unexpected amount of details rows", 1, spacers.size());
        assertEquals("Unexpected details row contents", "Details 4",
                spacers.get(0).findElement(By.className("v-label")).getText());

        // change selection
        grid.getCell(3, 0).click();

        spacers = grid.findElements(By.className("v-grid-spacer"));
        assertEquals("Unexpected amount of details rows", 1, spacers.size());
        assertEquals("Unexpected details row contents", "Details 3",
                spacers.get(0).findElement(By.className("v-label")).getText());

    }

}
