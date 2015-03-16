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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("grid")
public class GridColumnHidingTest extends GridBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testColumnHiding_hidingColumnsFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumn(1);
        toggleHideColumn(2);
        toggleHideColumn(3);
        assertColumnHeaderOrder(4, 5, 6, 7, 8);
    }

    @Test
    public void testColumnHiding_unhidingColumnsFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumn(0);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(1);
        toggleHideColumn(2);
        toggleHideColumn(3);
        assertColumnHeaderOrder(0, 4, 5, 6, 7, 8);

        toggleHideColumn(1);
        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 4, 5, 6);
    }

    @Test
    public void testColumnHiding_hidingUnhidingFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testColumnHiding_onVisibilityChange_triggersClientSideEvent() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        selectMenuPath("Component", "Internals", "Listeners",
                "Add Column Visibility Change listener");

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 3, 4);

        WebElement webElement = findElement(By.id("columnvisibility"));
        int counter = Integer.parseInt(webElement.getAttribute("counter"));
        int columnIndex = Integer.parseInt(webElement
                .getAttribute("columnindex"));
        boolean userOriginated = Boolean.parseBoolean(webElement
                .getAttribute("useroriginated"));
        boolean hidden = Boolean.parseBoolean(webElement
                .getAttribute("ishidden"));

        assertNotNull("no event fired", webElement);
        assertEquals(1, counter);
        assertEquals(2, columnIndex);
        assertEquals(false, userOriginated);
        assertEquals(true, hidden);

        toggleHideColumn(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        webElement = findElement(By.id("columnvisibility"));
        counter = Integer.parseInt(webElement.getAttribute("counter"));
        columnIndex = Integer.parseInt(webElement.getAttribute("columnIndex"));
        userOriginated = Boolean.parseBoolean(webElement
                .getAttribute("userOriginated"));
        hidden = Boolean.parseBoolean(webElement.getAttribute("ishidden"));

        assertNotNull("no event fired", webElement);
        assertEquals(2, counter);
        assertEquals(2, columnIndex);
        assertEquals(false, userOriginated);
        assertEquals(false, hidden);
    }

    @Test
    public void testColumnHidability_onTriggerColumnHidability_showsSidebarButton() {
        WebElement sidebar = getSidebar();
        assertNull(sidebar);

        toggleHidableColumn(0);

        sidebar = getSidebar();
        assertNotNull(sidebar);
    }

    @Test
    public void testColumnHidability_triggeringColumnHidabilityWithSeveralColumns_showsAndHidesSiderbarButton() {
        verifySidebarNotVisible();

        toggleHidableColumn(3);
        toggleHidableColumn(4);

        verifySidebarVisible();

        toggleHidableColumn(3);

        verifySidebarVisible();

        toggleHidableColumn(4);

        verifySidebarNotVisible();
    }

    @Test
    public void testColumnHidability_clickingSidebarButton_opensClosesSidebar() {
        toggleHidableColumn(0);
        verifySidebarClosed();

        getSidebarOpenButton().click();

        verifySidebarOpened();

        getSidebarOpenButton().click();

        verifySidebarClosed();
    }

    private void verifySidebarOpened() {
        WebElement sidebar = getSidebar();
        assertTrue(sidebar.getAttribute("class").contains("opened"));
    }

    private void verifySidebarClosed() {
        WebElement sidebar = getSidebar();
        assertFalse(sidebar.getAttribute("class").contains("opened"));
    }

    private void verifySidebarNotVisible() {
        WebElement sidebar = getSidebar();
        assertNull(sidebar);
    }

    private void verifySidebarVisible() {
        WebElement sidebar = getSidebar();
        assertNotNull(sidebar);
    }

    private WebElement getSidebar() {
        List<WebElement> elements = findElements(By.className("v-grid-sidebar"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    private WebElement getSidebarOpenButton() {
        List<WebElement> elements = findElements(By
                .className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    private void toggleHidableColumn(int columnIndex) {
        selectMenuPath("Component", "Columns", "Column " + columnIndex,
                "Hidable");
    }

    private void toggleHideColumn(int columnIndex) {
        selectMenuPath("Component", "Columns", "Column " + columnIndex,
                "Hidden");
    }
}
