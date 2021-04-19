package com.vaadin.tests.components.textarea;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextAreaSetRowsTest extends MultiBrowserTest {
    private double rowHeight;
    private int scrollbarHeight;
    private int bordersAndPadding = 14; // fixed by Valo theme

    @Test
    public void testSetRows() {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();

        // calculate scrollbar height for comparison
        PanelElement panel = $(PanelElement.class).first();
        int panelHeight = panel.getSize().getHeight();
        // add scrollbar
        $(ButtonElement.class).caption(TextAreaSetRows.SCROLLB).first().click();
        waitUntilLoadingIndicatorNotVisible();
        scrollbarHeight = panel.getSize().getHeight() - panelHeight;
        assertGreater("Unexpected comparison scrollbar height", scrollbarHeight,
                0);

        TextAreaElement textArea = $(TextAreaElement.class).first();
        int height5 = textArea.getSize().getHeight();
        // calculate height of a single row
        rowHeight = (height5 - bordersAndPadding) / 5d;
        assertEquals("Unexpected initial height,", getExpected(5), height5, 1);

        $(ButtonElement.class).caption(TextAreaSetRows.ROWS_0).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int height0 = textArea.getSize().getHeight();
        assertEquals("Unexpected 0 rows height,", getExpected(0), height0, 1);

        $(ButtonElement.class).caption(TextAreaSetRows.ROWS_4).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int height4 = textArea.getSize().getHeight();
        assertEquals("Unexpected 4 rows height,", getExpected(4), height4, 1);

        $(ButtonElement.class).caption(TextAreaSetRows.ROWS_2).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int height2 = textArea.getSize().getHeight();
        assertEquals("Unexpected 2 rows height,", getExpected(2), height2, 1);

        $(ButtonElement.class).caption(TextAreaSetRows.ROWS_1).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int height1 = textArea.getSize().getHeight();
        assertEquals("Unexpected 1 rows height,", getExpected(1), height1, 1);

        assertEquals("Height mismatch for 0 and 1 rows", height0, height1, 1);

        // set fixed height to 0 (does not affect borders and padding)
        $(ButtonElement.class).caption(TextAreaSetRows.HEIGHT0).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int heightFixed = textArea.getSize().getHeight();
        assertEquals("Unexpected fixed height,", bordersAndPadding, heightFixed,
                1);

        // remove fixed height, should return to height by rows
        $(ButtonElement.class).caption(TextAreaSetRows.HEIGHTR).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int heightReset = textArea.getSize().getHeight();
        assertEquals("Unexpected 1 rows height,", height1, heightReset, 1);

        $(ButtonElement.class).caption(TextAreaSetRows.ROWS_3).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int height3 = textArea.getSize().getHeight();
        assertEquals("Unexpected 3 rows height,", getExpected(3), height3, 1);

        // toggle off word wrap
        $(ButtonElement.class).caption(TextAreaSetRows.WWRAP).first().click();
        waitUntilLoadingIndicatorNotVisible();
        int newHeight3 = textArea.getSize().getHeight();
        // expected height to increase even without a scrollbar
        assertGreater("Unexpected 3 rows height without word wrap (short),",
                newHeight3, height3);

        // trigger horizontal scroll bar
        $(ButtonElement.class).caption(TextAreaSetRows.LONGS).first().click();
        waitUntilLoadingIndicatorNotVisible();
        // height should not have changed
        assertEquals("Unexpected 3 rows height without word wrap (long),",
                newHeight3, textArea.getSize().getHeight(), 1);

        // switch to longer contents with no breaks
        $(ButtonElement.class).caption(TextAreaSetRows.LONGN).first().click();
        waitUntilLoadingIndicatorNotVisible();
        // height should not have changed
        assertEquals(
                "Unexpected 3 rows height without word wrap (long without breaks),",
                newHeight3, textArea.getSize().getHeight(), 1);

        // ensure that the height difference to original matches a scrollbar
        // height, use a Panel's scrollbar as a comparison
        assertEquals("Unexpected textarea scrollbar height,", scrollbarHeight,
                newHeight3 - height3, 1);

        // toggle word wrap back on
        $(ButtonElement.class).caption(TextAreaSetRows.WWRAP).first().click();
        waitUntilLoadingIndicatorNotVisible();
        // height should have reverted to what it was before removing wrap
        assertEquals(
                "Unexpected 3 rows height with word wrap (long without breaks),",
                height3, textArea.getSize().getHeight(), 1);
    }

    /**
     * Calculates the expected height when horizontal scrollbar isn't possible.
     *
     * @param rows
     *            how many rows are displayed
     * @return expected text area height
     */
    private double getExpected(int rows) {
        // minimum row count is one
        return bordersAndPadding + (Math.max(1, rows) * rowHeight);
    }

}
