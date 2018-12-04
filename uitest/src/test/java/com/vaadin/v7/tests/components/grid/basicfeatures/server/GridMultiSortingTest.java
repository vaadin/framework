package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridMultiSortingTest extends GridBasicFeaturesTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return super.getBrowsersSupportingShiftClick();
    }

    @Test
    public void testUserMultiColumnSorting() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 11", "Column 11 Width",
                "Auto");

        GridCellElement cell = getGridElement().getHeaderCell(0, 11);
        new Actions(driver).moveToElement(cell, 20, 10).click().perform();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        new Actions(driver)
                .moveToElement(getGridElement().getHeaderCell(0, 0), 20, 10)
                .click().perform();
        new Actions(driver).keyUp(Keys.SHIFT).perform();

        String prev = getGridElement().getCell(0, 11).getAttribute("innerHTML");
        for (int i = 1; i <= 6; ++i) {
            assertEquals("Column 11 should contain same values.", prev,
                    getGridElement().getCell(i, 11).getAttribute("innerHTML"));
        }

        prev = getGridElement().getCell(0, 0).getText();
        for (int i = 1; i <= 6; ++i) {
            assertTrue("Grid is not sorted by column 0.", prev
                    .compareTo(getGridElement().getCell(i, 0).getText()) < 0);
        }
    }
}
