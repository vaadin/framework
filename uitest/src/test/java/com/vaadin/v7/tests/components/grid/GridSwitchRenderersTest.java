package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSwitchRenderersTest extends MultiBrowserTest {

    @Test
    public void testRendererSwitch() {

        // The UI should start with TEXT rendering in the second column
        // Clicking the checkbox will toggle rendering to HTML mode
        // Clicking it again should return TEXT rendering mode.

        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertTrue("Initial rendering of column 1 is not unformatted text",
                cellTextIsUnformatted(grid.getCell(0, 1).getText()));

        // NOTE: must click at 5,5 because of Valo and rendering in Chrome
        // This is a TestBench bug that may be fixed sometime in the future
        CheckBoxElement cb = $(CheckBoxElement.class).first();
        cb.click(5, 5);

        assertTrue(
                "Column 1 data has not been rendered with HTMLRenderer after renderer swap",
                cellTextIsHTMLFormatted(grid.getCell(0, 1).getText()));
        cb.click(5, 5);

        assertTrue(
                "Column 1 data has not been re-rendered as text after renderer swap",
                cellTextIsUnformatted(grid.getCell(0, 1).getText()));
    }

    /**
     * Attempts to match a string to a string like {@code <b>(4, 1)</b>}.
     *
     * @param cellText
     *            input string
     * @return true if input string is formatted like a raw HTML string
     */
    private boolean cellTextIsUnformatted(String cellText) {
        String regex = "<b>\\(\\d+, \\d+\\)</b>";
        return cellText.matches(regex);
    }

    /**
     * Attempts to match a string to a string like {@code (4, 1)}, i.e. the HTML
     * formatted version of the above (the bold tags should be consumed by the
     * renderer).
     *
     * @param cellText
     *            input string
     * @return true if input string is formatted like plain text (i.e. HTML bits
     *         have been consumed by renderer)
     */
    private boolean cellTextIsHTMLFormatted(String cellText) {
        String regex = "\\(\\d+, \\d+\\)";
        return cellText.matches(regex);
    }

}
