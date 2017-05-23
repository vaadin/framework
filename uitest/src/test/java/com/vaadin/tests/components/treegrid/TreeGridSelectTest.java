package com.vaadin.tests.components.treegrid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridSelectTest extends SingleBrowserTest {

    @Override
    public Class<?> getUIClass() {
        return TreeGridBasicFeatures.class;
    }

    @Test
    public void select_and_deselect_all() {
        openTestURL();

        selectMenuPath("Component", "Features", "Set data provider",
                "TreeDataProvider");
        selectMenuPath("Component", "State", "Selection mode", "multi");

        TreeGridElement grid = $(TreeGridElement.class).first();

        assertAllRowsDeselected(grid);
        clickSelectAll(grid);
        assertAllRowsSelected(grid);
        grid.expandWithClick(1, 1);
        grid.expandWithClick(2, 1);
        assertAllRowsSelected(grid);
        clickSelectAll(grid);
        assertAllRowsDeselected(grid);
        clickSelectAll(grid);
        grid.collapseWithClick(2, 1);
        grid.expandWithClick(2, 1);
        assertAllRowsSelected(grid);
        grid.collapseWithClick(2, 1);
        clickSelectAll(grid);
        grid.expandWithClick(2, 1);
        assertAllRowsDeselected(grid);
    }

    private void assertAllRowsSelected(TreeGridElement grid) {
        for (int i = 0; i < grid.getRowCount(); i++) {
            Assert.assertTrue(grid.getRow(i).isSelected());
        }
    }

    private void assertAllRowsDeselected(TreeGridElement grid) {
        for (int i = 0; i < grid.getRowCount(); i++) {
            Assert.assertFalse(grid.getRow(i).isSelected());
        }
    }

    private void clickSelectAll(TreeGridElement grid) {
        grid.getHeaderCell(0, 0).findElement(By.tagName("input")).click();
    }
}
