package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class GridAriaRowcountTest extends SingleBrowserTest {

    private GridElement grid;

    @Test
    public void checkGridAriaRowcount() {
        openTestURL();

        grid = $(GridElement.class).first();

        // default grid should contain at least one of each role
        String gridHtml = grid.getHTML();
        assertTrue("Grid should contain a role=\"rowheader\"", gridHtml.contains("role=\"rowheader\""));
        assertTrue("Grid should contain a role=\"columnheader\"", gridHtml.contains("role=\"columnheader\""));
        assertTrue("Grid should contain a role=\"row\"", gridHtml.contains("role=\"row\""));
        assertTrue("Grid should contain a role=\"gridcell\"", gridHtml.contains("role=\"gridcell\""));
        assertTrue("Grid should contain a role=\"rowgroup\"", gridHtml.contains("role=\"rowgroup\""));

        // default with 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("addFooter").first().click();
        // 1 header row, 2 body rows and 1 footer row.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("removeFooter").first().click();
        // 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("addHeader").first().click();
        // 2 header row and 2 body rows.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("removeHeader").first().click();
        // 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("setItemsTo3").first().click();
        // 1 header row and 3 body rows.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("setItemsTo6").first().click();
        // 1 header row and 6 body rows.
        assertTrue("Grid should have 7 rows", containsRows(7));

        $(ButtonElement.class).caption("updateAll").first().click();
        // 2 header rows, 4 body rows and 1 footer row.
        assertTrue("Grid should have 7 rows", containsRows(7));
    }

    private boolean containsRows(int rowcount) {
        return grid.getHTML()
                .contains("aria-rowcount=\"" + String.valueOf(rowcount) + "\"");
    }
}
