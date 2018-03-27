package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

public class GridDescriptionGeneratorTest extends GridBasicFeaturesTest {

    @Test
    public void testCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Cell description generator");

        getGridElement().getCell(1, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 1, column 0",
                tooltipText);

        getGridElement().getCell(1, 1).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testRowDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator");

        getGridElement().getCell(5, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);

        getGridElement().getCell(15, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 15", tooltipText);
    }

    @Test
    public void testRowAndCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator");
        selectMenuPath("Component", "State", "Cell description generator");

        getGridElement().getCell(5, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 5, column 0",
                tooltipText);

        getGridElement().getCell(5, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingFirefox();
    }

}
