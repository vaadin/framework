package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridAssistiveCaptionTest extends SingleBrowserTest {

    @Test
    public void checkGridAriaLabel() {
        openTestURL();

        GridElement.GridCellElement headerCell = $(GridElement.class).first()
                .getHeaderCell(0, 0);

        // default grid has no aria-label
        assertNull("Column should not contain aria-label",
                headerCell.getAttribute("aria-label"));

        $(ButtonElement.class).caption("addAssistiveCaption").first().click();
        assertTrue("Column should contain aria-label", headerCell
                .getAttribute("aria-label").equals("Press Enter to sort."));

        $(ButtonElement.class).caption("removeAssistiveCaption").first()
                .click();
        assertNull("Column should not contain aria-label",
                headerCell.getAttribute("aria-label"));
    }
}
