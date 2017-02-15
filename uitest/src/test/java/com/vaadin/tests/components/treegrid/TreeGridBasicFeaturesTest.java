package com.vaadin.tests.components.treegrid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridBasicFeaturesTest extends MultiBrowserTest {

    private TreeGridElement grid;

    @Before
    public void before() {
        openTestURL("theme=valo");
        grid = $(TreeGridElement.class).first();
    }

    @Test
    public void toggle_collapse_server_side() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "a", "b", "c" });

        selectMenuPath("Component", "Features", "Toggle collapse", "a");
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "a/a", "a/b", "a/c" });

        selectMenuPath("Component", "Features", "Toggle collapse", "a");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "a", "b", "c" });

        // collapsing a leaf should have no effect
        selectMenuPath("Component", "Features", "Toggle collapse", "a/a");
        Assert.assertEquals(3, grid.getRowCount());
    }

    @Test
    public void non_leaf_collapse_on_click() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "a", "b", "c" });

        // click the expander corresponding to "a"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "a/a", "a/b", "a/c" });

        // click the expander corresponding to "a"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "a", "b", "c" });
    }

    @Test
    public void keyboard_navigation() {

    }

    @Test
    public void changing_hierarchy_column() {
        Assert.assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "Second column");

        Assert.assertFalse(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertTrue(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "First column");

        Assert.assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            Assert.assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
