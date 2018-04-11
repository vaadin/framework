package com.vaadin.v7.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridSidebarContentTest extends GridBasicClientFeaturesTest {

    @Test
    public void testSidebarWithHidableColumn() {
        openTestURL();

        assertEquals("Sidebar should not be initially present", 0,
                countBySelector(".v-grid-sidebar-button"));

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");

        getSidebarOpenButton().click();

        WebElement toggle = getSidebarPopup()
                .findElement(By.className("column-hiding-toggle"));

        assertEquals("Column 0 should be togglable", "Header (0,0)",
                toggle.getText());

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
        assertEquals("Sidebar should disappear without toggable column", 0,
                countBySelector(".v-grid-sidebar-button"));

    }

    @Test
    public void testAddingCustomSidebarItem() {
        openTestURL();
        GridElement gridElement = getGridElement();

        selectMenuPath("Component", "Sidebar", "Add item to end");

        gridElement.findElement(By.className("v-grid-sidebar-button")).click();

        WebElement sidebarItem = getSidebarPopup().findElement(
                By.cssSelector(".v-grid-sidebar-content .gwt-MenuItem"));

        sidebarItem.click();

        assertEquals("Sidebar should be closed after clicking item 0", 0,
                countBySelector(".v-grid-sidebar-content"));
    }

    @Test
    public void testProgrammaticSidebarOpen() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");

        selectMenuPath("Component", "Sidebar", "Toggle sidebar visibility");

        assertEquals("Sidebar should be open", 1,
                countBySelector(".v-grid-sidebar-content"));
    }

    @Test
    public void testBasicSidebarOrder() {
        openTestURL();

        // First add custom content
        selectMenuPath("Component", "Sidebar", "Add separator to end");
        selectMenuPath("Component", "Sidebar", "Add item to end");

        // Then make one column togglable
        selectMenuPath("Component", "Columns", "Column 0", "Hidable");

        selectMenuPath("Component", "Sidebar", "Toggle sidebar visibility");

        assertSidebarMenuItems("Header (0,0)", null, "Custom menu item 0");
    }

    @Test
    public void testSidebarOrderAbuse() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
        selectMenuPath("Component", "Columns", "Column 1", "Hidable");

        // Inserts a menu item between the two visibility toggles
        selectMenuPath("Component", "Sidebar", "Add item before index 1");

        selectMenuPath("Component", "Sidebar", "Toggle sidebar visibility");

        // Total order enforcement not implemented at this point. Test can be
        // updated when it is.
        assertSidebarMenuItems("Header (0,0)", "Custom menu item 0",
                "Header (0,1)");

        selectMenuPath("Component", "Columns", "Column 2", "Hidable");
        selectMenuPath("Component", "Sidebar", "Toggle sidebar visibility");

        // Adding a new togglable column should have restored the expected order
        assertSidebarMenuItems("Header (0,0)", "Header (0,1)", "Header (0,2)",
                "Custom menu item 0");
    }

    private void assertSidebarMenuItems(String... items) {
        List<WebElement> menuItems = getSidebarPopup()
                .findElements(By.cssSelector(".v-grid-sidebar-content td"));

        assertEquals("Expected " + items.length + " menu items", items.length,
                menuItems.size());

        for (int i = 0; i < items.length; i++) {
            String expectedItem = items[i];
            if (expectedItem == null) {
                assertEquals("Item " + i + " should be a separator",
                        "gwt-MenuItemSeparator",
                        menuItems.get(i).getAttribute("class"));
            } else {
                assertEquals("Unexpected content for item " + i, expectedItem,
                        menuItems.get(i).getText());
            }
        }
    }

    private int countBySelector(String cssSelector) {
        return findElements(By.cssSelector(cssSelector)).size();
    }
}
