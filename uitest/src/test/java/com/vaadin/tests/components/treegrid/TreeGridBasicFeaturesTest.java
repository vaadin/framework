package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;

@RunWith(ParameterizedTB3Runner.class)
public class TreeGridBasicFeaturesTest extends MultiBrowserTest {

    private TreeGridElement grid;

    public void setDataProvider(String dataProviderString) {
        selectMenuPath("Component", "Features", "Set data provider",
                dataProviderString);
    }

    @Parameters
    public static Collection<String> getDataProviders() {
        return Arrays.asList("LazyHierarchicalDataProvider",
                "InMemoryHierarchicalDataProvider");
    }

    @Before
    public void before() {
        openTestURL("theme=valo");
        grid = $(TreeGridElement.class).first();
    }

    @Test
    @Ignore // currently no implementation exists for toggling from the server
            // side
    public void toggle_collapse_server_side() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        selectMenuPath("Component", "Features", "Toggle expand", "0 | 0");
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        selectMenuPath("Component", "Features", "Toggle expand", "0 | 0");
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // collapsing a leaf should have no effect
        selectMenuPath("Component", "Features", "Toggle expand", "1 | 0");
        Assert.assertEquals(3, grid.getRowCount());
    }

    @Test
    public void non_leaf_collapse_on_click() {
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // Should expand "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        // Should collapse "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    @Ignore // FIXME: remove ignore annotation once #8758 is done
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();

        // Should expand "0 | 0"
        new Actions(getDriver()).keyDown(Keys.ALT).sendKeys(Keys.RIGHT)
                .keyUp(Keys.ALT).perform();
        Assert.assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        // Should collapse "0 | 0"
        new Actions(getDriver()).keyDown(Keys.ALT).sendKeys(Keys.LEFT)
                .keyUp(Keys.ALT).perform();
        Assert.assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    public void changing_hierarchy_column() {
        Assert.assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "depth");

        Assert.assertFalse(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertTrue(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "string");

        Assert.assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        Assert.assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));
    }

    @Test
    public void expand_and_collapse_listeners() {
        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        Assert.assertFalse(logContainsText("Item expanded: 0 | 0"));
        Assert.assertFalse(logContainsText("Item collapsed: 0 | 0"));

        grid.collapseWithClick(0);

        Assert.assertTrue(logContainsText("Item expanded: 0 | 0"));
        Assert.assertFalse(logContainsText("Item collapsed: 0 | 0"));

        grid.collapseWithClick(0);

        Assert.assertTrue(logContainsText("Item expanded: 0 | 0"));
        Assert.assertTrue(logContainsText("Item collapsed: 0 | 0"));

        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        grid.collapseWithClick(1);
        grid.collapseWithClick(1);

        Assert.assertFalse(logContainsText("Item expanded: 0 | 1"));
        Assert.assertFalse(logContainsText("Item collapsed: 0 | 1"));
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
