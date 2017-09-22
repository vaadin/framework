package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridAddColumnTest extends SingleBrowserTest {

    GridElement grid;

    @Before
    public void init() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void columns_rendered_correctly() {
        assertCellEquals(0, 0, "a");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "aaa");

        assertCellEquals(0, 1, "1");
        assertCellEquals(1, 1, "2");
        assertCellEquals(2, 1, "3");

        assertCellEquals(0, 2, "1");
        assertCellEquals(1, 2, "2");
        assertCellEquals(2, 2, "3");

        assertCellEquals(0, 3, "-1");
        assertCellEquals(1, 3, "-2");
        assertCellEquals(2, 3, "-3");

        assertCellStartsWith(0, 4, "java.lang.Object@");
        assertCellStartsWith(1, 4, "java.lang.Object@");
        assertCellStartsWith(2, 4, "java.lang.Object@");
    }

    @Test
    public void sort_column_with_automatic_conversion() {
        grid.getHeaderCell(0, 2).click();
        assertCellEquals(0, 0, "a");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "aaa");

        grid.getHeaderCell(0, 3).click();
        assertCellEquals(0, 0, "aaa");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "a");
    }

    @Test
    public void initial_header_content() {
        GridCellElement firstHeader = grid.getHeaderCell(0, 0);
        Assert.assertTrue("No label element in header",
                firstHeader.isElementPresent(By.className("v-label")));
        Assert.assertEquals("Text in label does not match", "Label Header",
                firstHeader.getText());
    }

    @Test
    public void replace_all_columns() {
        $(ButtonElement.class).first().click();

        // Verify button got clicked
        Assert.assertTrue(isElementPresent(NotificationElement.class));
        Assert.assertEquals("Columns replaced.",
                $(NotificationElement.class).first().getText());

        // Run default rendering test
        columns_rendered_correctly();
    }

    private void assertCellEquals(int rowIndex, int colIndex, String content) {
        Assert.assertEquals("Cell text should equal", content,
                grid.getCell(rowIndex, colIndex).getText());
    }

    private void assertCellStartsWith(int rowIndex, int colIndex,
            String startsWith) {
        Assert.assertTrue("Cell text should start with", grid
                .getCell(rowIndex, colIndex).getText().startsWith(startsWith));
    }
}
