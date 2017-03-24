package com.vaadin.tests.components.treegrid;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.testbench.parallel.Browser;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        openTestURL("theme=valo","debug");
        grid = $(TreeGridElement.class).first();
    }

    @Test
    @Ignore // currently no implementation exists for toggling from the server
            // side
    public void toggle_collapse_server_side() {
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        selectMenuPath("Component", "Features", "Toggle expand", "0 | 0");
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        selectMenuPath("Component", "Features", "Toggle expand", "0 | 0");
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // collapsing a leaf should have no effect
        selectMenuPath("Component", "Features", "Toggle expand", "1 | 0");
        assertEquals(3, grid.getRowCount());
    }

    @Test
    public void non_leaf_collapse_on_click() {
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });

        // Should expand "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });

        // Should collapse "0 | 0"
        grid.getRow(0).getCell(0)
                .findElement(By.className("v-tree-grid-expander")).click();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
    }

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();

        // Should expand "0 | 0" without moving focus
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });
        assertTrue(grid.getRow(0).hasClassName("v-grid-rowmode-row-focused"));
        assertFalse(grid.getRow(1).hasClassName("v-grid-rowmode-row-focused"));

        // Should navigate 2 times down to "1 | 1"
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(1, 0, new String[] { "1 | 0", "1 | 1", "1 | 2" });
        assertFalse(grid.getRow(0).hasClassName("v-grid-rowmode-row-focused"));
        assertFalse(grid.getRow(1).hasClassName("v-grid-rowmode-row-focused"));
        assertTrue(grid.getRow(2).hasClassName("v-grid-rowmode-row-focused"));

        // Should expand "1 | 1" without moving focus
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        assertEquals(9, grid.getRowCount());
        assertCellTexts(2, 0, new String[] { "1 | 1", "2 | 0",  "2 | 1",  "2 | 2",  "1 | 2"});
        assertTrue(grid.getRow(2).hasClassName("v-grid-rowmode-row-focused"));

        // Should collapse "1 | 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(2, 0, new String[] { "1 | 1", "1 | 2",  "0 | 1"});
        assertTrue(grid.getRow(2).hasClassName("v-grid-rowmode-row-focused"));


        // Should navigate to "0 | 0"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "1 | 0", "1 | 1", "1 | 2" , "0 | 1" });
        assertTrue(grid.getRow(0).hasClassName("v-grid-rowmode-row-focused"));

        // Should collapse "0 | 0"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
        assertTrue(grid.getRow(0).hasClassName("v-grid-rowmode-row-focused"));

        // Nothing should happen
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertEquals(3, grid.getRowCount());
        assertCellTexts(0, 0, new String[] { "0 | 0", "0 | 1", "0 | 2" });
        assertTrue(grid.getRow(0).hasClassName("v-grid-rowmode-row-focused"));

        assertNoErrorNotifications();
    }

    @Test
    public void changing_hierarchy_column() {
        assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "depth");

        assertFalse(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        assertTrue(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));

        selectMenuPath("Component", "Features", "Set hierarchy column",
                "string");

        assertTrue(grid.getRow(0).getCell(0)
                .isElementPresent(By.className("v-tree-grid-expander")));
        assertFalse(grid.getRow(0).getCell(1)
                .isElementPresent(By.className("v-tree-grid-expander")));
    }

    @Override
    protected boolean useNativeEventsForIE() {
        //Fixes IE11 selectMenuPath troubles
        return false;
    }

    @Test
    public void expand_and_collapse_listeners() {
        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        assertFalse(logContainsText("Item expanded: 0 | 0"));
        assertFalse(logContainsText("Item collapsed: 0 | 0"));

        grid.expandWithClick(0);

        assertTrue(logContainsText("Item expanded: 0 | 0"));
        assertFalse(logContainsText("Item collapsed: 0 | 0"));

        grid.collapseWithClick(0);

        assertTrue(logContainsText("Item expanded: 0 | 0"));
        assertTrue(logContainsText("Item collapsed: 0 | 0"));

        selectMenuPath("Component", "State", "Expand listener");
        selectMenuPath("Component", "State", "Collapse listener");

        grid.expandWithClick(1);
        grid.collapseWithClick(1);

        assertFalse(logContainsText("Item expanded: 0 | 1"));
        assertFalse(logContainsText("Item collapsed: 0 | 1"));
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
