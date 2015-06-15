/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.grid.basicfeatures.client;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.element.CustomGridElement;

public class GridSidebarContentTest extends GridBasicClientFeaturesTest {

    @Test
    public void testSidebarWithHidableColumn() {
        openTestURL();
        CustomGridElement gridElement = getGridElement();

        Assert.assertEquals("Sidebar should not be initially present", 0,
                countBySelector(".v-grid-sidebar"));

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");

        gridElement.findElement(By.className("v-grid-sidebar-button")).click();

        WebElement toggle = gridElement.findElement(By
                .className("column-hiding-toggle"));

        Assert.assertEquals("Column 0 should be togglable", "Header (0,0)",
                toggle.getText());

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
        Assert.assertEquals("Sidebar should disappear without toggable column",
                0, countBySelector(".v-grid-sidebar"));

    }

    @Test
    public void testAddingCustomSidebarItem() {
        openTestURL();
        CustomGridElement gridElement = getGridElement();

        selectMenuPath("Component", "Sidebar", "Add item to end");

        gridElement.findElement(By.className("v-grid-sidebar-button")).click();

        WebElement sidebarItem = gridElement.findElement(By
                .cssSelector(".v-grid-sidebar-content .gwt-MenuItem"));

        sidebarItem.click();

        Assert.assertEquals("Sidebar should be closed after clicking item 0",
                0, countBySelector(".v-grid-sidebar-content"));
    }

    @Test
    public void testProgrammaticSidebarOpen() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Hidable");

        selectMenuPath("Component", "Sidebar", "Toggle sidebar visibility");

        Assert.assertEquals("Sidebar should be open", 1,
                countBySelector(".v-grid-sidebar-content"));
    }

    @Test
    public void testBasicSidebarOrder() {
        openTestURL();
        CustomGridElement gridElement = getGridElement();

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
        CustomGridElement gridElement = getGridElement();

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
        List<WebElement> menuItems = getGridElement().findElements(
                By.cssSelector(".v-grid-sidebar-content td"));

        Assert.assertEquals("Expected " + items.length + " menu items",
                items.length, menuItems.size());

        for (int i = 0; i < items.length; i++) {
            String expectedItem = items[i];
            if (expectedItem == null) {
                Assert.assertEquals("Item " + i + " should be a separator",
                        "gwt-MenuItemSeparator",
                        menuItems.get(i).getAttribute("class"));
            } else {
                Assert.assertEquals("Unexpected content for item " + i,
                        expectedItem, menuItems.get(i).getText());
            }
        }
    }

    private int countBySelector(String cssSelector) {
        return getGridElement().findElements(By.cssSelector(cssSelector))
                .size();
    }

}
