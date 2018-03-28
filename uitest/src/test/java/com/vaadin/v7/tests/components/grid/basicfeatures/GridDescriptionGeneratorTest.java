package com.vaadin.v7.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void testContentTypes() {
        openTestURL();

        selectCellGenerator("Default");
        showCellTooltip(1, 0);
        /*
         * When porting this to the v7 version in Framework 8, the default
         * should be changed to PREFORMATTED to preserve the more secure default
         * that has accidentally been used there.
         */
        assertHtmlTooltipShown();

        selectRowGenerator("Default");
        showCellTooltip(1, 1);
        /*
         * When porting this to the v7 version in Framework 8, the default
         * should be changed to PREFORMATTED to preserve the more secure default
         * that has accidentally been used there.
         */
        assertHtmlTooltipShown();

        selectCellGenerator("Plain text");
        showCellTooltip(2, 0);
        assertPlainTooltipShown();

        selectRowGenerator("Plain text");
        showCellTooltip(2, 1);
        assertPlainTooltipShown();

        selectCellGenerator("Preformatted");
        showCellTooltip(3, 0);
        assertPreTooltipShown();

        selectRowGenerator("Preformatted");
        showCellTooltip(3, 1);
        assertPreTooltipShown();

        selectCellGenerator("HTML");
        showCellTooltip(4, 0);
        assertHtmlTooltipShown();

        selectRowGenerator("HTML");
        showCellTooltip(4, 1);
        assertHtmlTooltipShown();
    }

    private void assertPreTooltipShown() {
        assertTrue("Tooltip should contain <b> as text", getTooltipText()
                .contains("<b>"));
        assertTrue("Tooltip should contain a newline", getTooltipText()
                .contains("\n"));
    }

    private void assertPlainTooltipShown() {
        assertTrue("Tooltip should contain <b> as text", getTooltipText()
                .contains("<b>"));
        assertFalse("Tooltip should not contain a newline", getTooltipText()
                .contains("\n"));
    }

    private void assertHtmlTooltipShown() {
        assertTrue("Tooltip should contain <b> tag",
                isElementPresent(By.cssSelector(".v-tooltip-text b")));
    }

    private void showCellTooltip(int row, int col) {
        getGridElement().getCell(row, col).showTooltip();
    }

    private void selectCellGenerator(String name) {
        selectMenuPath("Component", "State", "Cell description generator", name);
    }

    private void selectRowGenerator(String name) {
        selectMenuPath("Component", "State", "Row description generator", name);
    }

    private String getTooltipText() {
        return findElement(By.className("v-tooltip-text")).getText();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingFirefox();
    }

}
