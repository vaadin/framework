package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @author Vaadin Ltd
 */
public class SetPageFirstItemLoadsNeededRowsOnlyTest extends MultiBrowserTest {

    /*
     * expectedRowsRequested is related to VScrollTable's cache_rate and
     * pageLength. See for instance VScrollTable.ensureCacheFilled().
     *
     * This also takes into account if the visible rows are at the very start or
     * end of the table, if the user scrolled or the
     * Table.setCurrentPageFirstItemIndex(int) method was used.
     *
     * This value should not change if cache_rate and pageLength are not changed
     * as well, and if this test remains constant: the table is scrolled to the
     * very end (done in the actual UI: SetPageFirstItemLoadsNeededRowsOnly).
     */
    private int expectedRowsRequested = 45;

    @Test
    public void verifyLoadedRows() throws InterruptedException {

        openTestURL();

        // wait for events to be processed in UI after loading page
        sleep(2000);

        String labelValue = $(LabelElement.class).get(1).getText();
        String expectedLabelValue = "rows requested: " + expectedRowsRequested;
        String errorMessage = "Too many rows were requested";
        assertEquals(errorMessage, expectedLabelValue, labelValue);
    }
}
