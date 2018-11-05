package com.vaadin.tests.components.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests if the sort indicator is visible after the table has been sorted from
 * the serverside.
 *
 * @author Vaadin Ltd
 */
public class TableSortingIndicatorTest extends MultiBrowserTest {
    private static final String TABLE_HEADER_DESC_INDICATOR = "v-table-header-cell-desc";
    private static final String TABLE_HEADER_ASC_INDICATOR = "v-table-header-cell-asc";

    @Test
    public void testTableSortingIndicatorIsVisibleAfterServersideSort() {
        openTestURL();

        ButtonElement button = $(ButtonElement.class).caption("Sort").first();
        TableElement table = $(TableElement.class).first();

        assertFalse("Descending indicator was prematurely visible",
                getHeaderClasses(table).contains(TABLE_HEADER_DESC_INDICATOR));
        assertFalse("Ascending indicator was prematurely visible",
                getHeaderClasses(table).contains(TABLE_HEADER_ASC_INDICATOR));

        button.click();
        assertTrue("Indicator did not become visible",
                getHeaderClasses(table).contains(TABLE_HEADER_DESC_INDICATOR));
        assertFalse("Ascending sort indicator was wrongly visible",
                getHeaderClasses(table).contains(TABLE_HEADER_ASC_INDICATOR));

        table.getHeaderCell(0).click();
        assertFalse("Table sort indicator didn't change",
                getHeaderClasses(table).contains(TABLE_HEADER_DESC_INDICATOR));
        assertTrue("Ascending sort indicator didn't become visible",
                getHeaderClasses(table).contains(TABLE_HEADER_ASC_INDICATOR));

        button.click();
        assertTrue(
                "Descending sort indicator didn't appear on the second serverside sort.",
                getHeaderClasses(table).contains(TABLE_HEADER_DESC_INDICATOR));
        assertFalse("Ascending sort indicator didn't disappear",
                getHeaderClasses(table).contains(TABLE_HEADER_ASC_INDICATOR));
    }

    private String getHeaderClasses(TableElement table) {
        return table.getHeaderCell(0).getAttribute("class");
    }
}
