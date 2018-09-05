package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridColumnWidthsWithoutDataTest extends SingleBrowserTest {

    @Test
    public void testWidthsWhenAddingDataBack() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        int[] baseWidths = getColWidths(grid);
        assertEquals("Sanity check", 2, baseWidths.length);

        assertTrue("Columns should not have equal width",
                Math.abs(baseWidths[0] - baseWidths[1]) > 2);

        removeData();

        assertSameWidths(baseWidths, getColWidths(grid));

        addData();

        assertSameWidths(baseWidths, getColWidths(grid));
    }

    @Test
    public void testWidthsWhenInitiallyEmpty() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).caption("Recreate without data").first().click();

        GridElement grid = $(GridElement.class).first();

        int[] baseWidths = getColWidths(grid);
        assertEquals("Sanity check", 2, baseWidths.length);

        assertTrue("Columns should have roughly equal width",
                Math.abs(baseWidths[0] - baseWidths[1]) < 10);
        assertTrue("Columns should not have default widths",
                baseWidths[0] > 140);
        assertTrue("Columns should not have default widths",
                baseWidths[1] > 140);

        addData();

        assertSameWidths(baseWidths, getColWidths(grid));

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testMultiSelectWidths() {
        setDebug(true);
        openTestURL();
        $(NativeSelectElement.class).caption("Selection mode").first()
                .selectByText("MULTI");

        GridElement grid = $(GridElement.class).first();

        int sum = sumUsedWidths(grid);

        // 295 instead of 300 to avoid rounding issues
        assertTrue("Only " + sum + " out of 300px was used", sum > 295);

        $(ButtonElement.class).caption("Recreate without data").first().click();

        grid = $(GridElement.class).first();
        sum = sumUsedWidths(grid);

        // 295 instead of 300 to avoid rounding issues
        assertTrue("Only " + sum + " out of 300px was used", sum > 295);
    }

    private int sumUsedWidths(GridElement grid) {
        int sum = 0;
        for (int i : getColWidths(grid)) {
            sum += i;
        }
        return sum;
    }

    private static void assertSameWidths(int[] expected, int[] actual) {
        assertEquals("Arrays have differing lengths", expected.length,
                actual.length);

        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(expected[i] - actual[i]) > 1) {
                fail("Differing sizes at index " + i + ". Expected "
                        + expected[i] + " but got " + actual[i]);
            }
        }
    }

    private void removeData() {
        $(ButtonElement.class).caption("Remove data").first().click();
    }

    private void addData() {
        $(ButtonElement.class).caption("Add data").first().click();
    }

    private int[] getColWidths(GridElement grid) {
        List<GridCellElement> headerCells = grid.getHeaderCells(0);
        int[] widths = new int[headerCells.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = headerCells.get(i).getSize().getWidth();
        }
        return widths;
    }
}
