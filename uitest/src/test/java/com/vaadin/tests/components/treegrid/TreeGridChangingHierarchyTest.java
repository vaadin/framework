package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridChangingHierarchyTest extends SingleBrowserTest {

    private TreeGridElement grid;
    private ButtonElement addItemsToABtn;
    private ButtonElement addItemsToAABtn;
    private ButtonElement removeAABtn;
    private ButtonElement removeChildrenOfAABtn;
    private ButtonElement removeABtn;
    private ButtonElement removeChildrenOfABtn;
    private ButtonElement removeChildrenOfAAABtn;

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
        grid = $(TreeGridElement.class).first();
        addItemsToABtn = $(ButtonElement.class).get(0);
        addItemsToAABtn = $(ButtonElement.class).get(1);
        removeAABtn = $(ButtonElement.class).get(2);
        removeChildrenOfAABtn = $(ButtonElement.class).get(3);
        removeABtn = $(ButtonElement.class).get(4);
        removeChildrenOfABtn = $(ButtonElement.class).get(5);
        removeChildrenOfAAABtn = $(ButtonElement.class).get(6);
    }

    @After
    public void after() {
        assertNoErrorNotifications();
        assertFalse(isElementPresent(By.className("v-errorindicator")));
    }

    @Test
    public void removing_items_from_hierarchy() {
        addItemsToABtn.click();
        addItemsToAABtn.click();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        removeAABtn.click();
        // Item removed from hierarchy. when encountering less children than
        // expected, should reset:
        grid.expandWithClick(0);
        // expand "a" after the reset:
        grid.expandWithClick(0);
        // "a/a" should be removed from a's children:
        assertEquals("a/b", grid.getCell(1, 0).getText());
    }

    @Test
    public void removing_all_children_from_item() {
        addItemsToABtn.click();
        assertTrue(grid.isRowCollapsed(0, 0));
        // drop added children from backing data source
        removeChildrenOfABtn.click();
        // changes are not refreshed, thus the row should still appear as
        // collapsed
        assertTrue(grid.isRowCollapsed(0, 0));
        // when encountering 0 children, will reset
        grid.expandWithClick(0);
        assertEquals(3, grid.getRowCount());
        assertFalse(grid.hasExpandToggle(0, 0));
        // verify other items still expand/collapse correctly:
        grid.expandWithClick(1);
        assertEquals("b/a", grid.getCell(2, 0).getText());
        assertEquals(4, grid.getRowCount());
        grid.collapseWithClick(1);
        assertEquals("c", grid.getCell(2, 0).getText());
        assertEquals(3, grid.getRowCount());
    }

    @Test
    public void removal_of_deeply_nested_items() {
        addItemsToABtn.click();
        addItemsToAABtn.click();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.expandWithClick(2);
        removeChildrenOfAAABtn.click();
        grid.collapseWithClick(1);
        // reset should get triggered here
        grid.expandWithClick(1);
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        assertEquals("a/a/a", grid.getCell(2, 0).getText());
        assertFalse(grid.hasExpandToggle(2, 0));
    }

    @Test
    public void changing_selection_from_selected_removed_item() {
        addItemsToABtn.click();
        grid.expandWithClick(0);
        grid.getCell(1, 0).click();
        removeChildrenOfABtn.click();
        grid.collapseWithClick(0);
        grid.getCell(1, 0).click();
        assertTrue(grid.getRow(1).isSelected());
    }

    @Test
    public void remove_item_from_root() {
        addItemsToABtn.click();
        removeABtn.click();
        grid.expandWithClick(0);
        assertEquals("b", grid.getCell(0, 0).getText());
    }
}
