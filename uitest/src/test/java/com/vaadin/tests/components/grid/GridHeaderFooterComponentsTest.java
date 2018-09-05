package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridHeaderFooterComponentsTest extends SingleBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);

        openTestURL();
    }

    @Test
    public void hideAndShowComponentsInHeader() {
        GridElement grid = $(GridElement.class).first();

        int filterRow = 2;
        assertNull(getHeaderElement(grid, filterRow, 1));
        assertNotNull(getHeaderElement(grid, filterRow, 2));
        assertNotNull(getHeaderElement(grid, filterRow, 3));

        // Show (1,2)
        grid.getHeaderCell(1, 1).$(ButtonElement.class).first().click();

        TextFieldElement textfield = getHeaderElement(grid, filterRow, 1);
        assertNotNull(textfield);
        assertEquals("Filter: string", textfield.getValue());

        textfield.setValue("foo");
        assertEquals("1. value change for field in string to foo",
                getLogRow(0));

        assertNoErrorNotifications();
    }

    private TextFieldElement getHeaderElement(GridElement grid, int row,
            int col) {
        GridCellElement cell = grid.getHeaderCell(row, col);
        List<TextFieldElement> all = cell.$(TextFieldElement.class).all();
        if (all.isEmpty()) {
            return null;
        } else if (all.size() == 1) {
            return all.get(0);
        } else {
            throw new RuntimeException(
                    "Multiple elements found in the header cell at " + row + ","
                            + col);
        }
    }

    @Test
    public void hideAndShowComponentsInFooter() {
        GridElement grid = $(GridElement.class).first();

        int filterRow = 0;
        assertNull(getFooterElement(grid, filterRow, 1));
        assertNotNull(getFooterElement(grid, filterRow, 2));
        assertNotNull(getFooterElement(grid, filterRow, 3));

        // Show (1,2)
        grid.getFooterCell(1, 1).$(ButtonElement.class).first().click();

        TextFieldElement textfield = getFooterElement(grid, filterRow, 1);
        assertNotNull(textfield);
        assertEquals("Filter: string", textfield.getValue());

        textfield.setValue("foo");
        assertEquals("1. value change for field in string to foo",
                getLogRow(0));

        assertNoErrorNotifications();
    }

    private TextFieldElement getFooterElement(GridElement grid, int row,
            int col) {
        GridCellElement cell = grid.getFooterCell(row, col);
        List<TextFieldElement> all = cell.$(TextFieldElement.class).all();
        if (all.isEmpty()) {
            return null;
        } else if (all.size() == 1) {
            return all.get(0);
        } else {
            throw new RuntimeException(
                    "Multiple elements found in the footer cell at " + row + ","
                            + col);
        }
    }

    @Test
    public void testRemoveAllHeadersAndFooters() {
        openTestURL();

        for (int i = 2; i >= 0; --i) {
            // Remove Header
            $(GridElement.class).first().getHeaderCell(i, 0)
                    .$(ButtonElement.class).first().click();
            assertFalse("Header " + i + " should not be present.",
                    $(GridElement.class).first()
                            .isElementPresent(By.vaadin("#header[" + i + "]")));

            // Remove Footer
            $(GridElement.class).first().getFooterCell(i, 0)
                    .$(ButtonElement.class).first().click();
            assertFalse("Footer " + i + " should not be present.",
                    $(GridElement.class).first()
                            .isElementPresent(By.vaadin("#footer[" + i + "]")));
        }

        assertNoErrorNotifications();
    }
}
