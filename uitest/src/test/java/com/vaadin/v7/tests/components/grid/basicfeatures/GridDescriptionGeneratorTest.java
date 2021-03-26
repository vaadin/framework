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
        selectCellGenerator("HTML");

        showCellTooltip(1, 0);
        assertEquals("Tooltip text", "Cell tooltip for row 1, column 0",
                getTooltipText());

        showCellTooltip(1, 1);
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                getTooltipText().isEmpty());
    }

    @Test
    public void testRowDescription() {
        openTestURL();
        selectRowGenerator("HTML");

        showCellTooltip(5, 3);
        assertEquals("Tooltip text", "Row tooltip for row 5", getTooltipText());

        showCellTooltip(15, 3);
        assertEquals("Tooltip text", "Row tooltip for row 15",
                getTooltipText());
    }

    @Test
    public void testRowAndCellDescription() {
        openTestURL();
        selectRowGenerator("HTML");
        selectCellGenerator("HTML");

        showCellTooltip(5, 0);
        assertEquals("Tooltip text", "Cell tooltip for row 5, column 0",
                getTooltipText());

        showCellTooltip(5, 3);
        assertEquals("Tooltip text", "Row tooltip for row 5", getTooltipText());
    }

    @Test
    public void testContentTypesCell() {
        openTestURL();
        assertEquals("Unexpected tooltip,", "", getTooltipText());

        selectCellGenerator("Plain text");
        showCellTooltip(2, 0);
        assertPlainTooltipShown();

        selectCellGenerator("Preformatted");
        showCellTooltip(3, 0);
        assertPreTooltipShown();

        selectCellGenerator("HTML");
        showCellTooltip(4, 0);
        assertHtmlTooltipShown();

        selectCellGenerator("None (Default)");
        showCellTooltip(1, 0);
        assertEquals("Unexpected tooltip,", "", getTooltipText());
    }

    @Test
    public void testContentTypesRow() {
        openTestURL();
        assertEquals("Unexpected tooltip,", "", getTooltipText());

        selectRowGenerator("Plain text");
        showCellTooltip(2, 1);
        assertPlainTooltipShown();

        selectRowGenerator("Preformatted");
        showCellTooltip(3, 1);
        assertPreTooltipShown();

        selectRowGenerator("HTML");
        showCellTooltip(4, 1);
        assertHtmlTooltipShown();

        selectRowGenerator("None (Default)");
        showCellTooltip(1, 1);
        assertEquals("Unexpected tooltip,", "", getTooltipText());
    }

    private void assertPreTooltipShown() {
        try {
            assertTrue("Tooltip should contain <b> as text",
                    getTooltipText().contains("<b>"));
            assertTrue("Tooltip should contain a newline",
                    getTooltipText().contains("\n"));
        } catch (AssertionError e) {
            // showing tooltips is somewhat flaky, try again with another cell
            showCellTooltip(5, 0);
            assertTrue("Tooltip should contain <b> as text",
                    getTooltipText().contains("<b>"));
            assertTrue("Tooltip should contain a newline",
                    getTooltipText().contains("\n"));
        }
    }

    private void assertPlainTooltipShown() {
        try {
            assertTrue("Tooltip should contain <b> as text",
                    getTooltipText().contains("<b>"));
            assertFalse("Tooltip should not contain a newline",
                    getTooltipText().contains("\n"));
        } catch (AssertionError e) {
            // showing tooltips is somewhat flaky, try again with another cell
            showCellTooltip(5, 0);
            assertTrue("Tooltip should contain <b> tag",
                    isElementPresent(By.cssSelector(".v-tooltip-text b")));
        }
    }

    private void assertHtmlTooltipShown() {
        try {
            assertTrue("Tooltip should contain <b> tag",
                    isElementPresent(By.cssSelector(".v-tooltip-text b")));
        } catch (AssertionError e) {
            // showing tooltips is somewhat flaky, try again with another cell
            showCellTooltip(5, 0);
            assertTrue("Tooltip should contain <b> tag",
                    isElementPresent(By.cssSelector(".v-tooltip-text b")));
        }
    }

    private void showCellTooltip(int row, int col) {
        getGridElement().getCell(row, col).showTooltip();
        sleep(200);
    }

    private void selectCellGenerator(String name) {
        selectMenuPath("Component", "State", "Cell description generator",
                name);
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
