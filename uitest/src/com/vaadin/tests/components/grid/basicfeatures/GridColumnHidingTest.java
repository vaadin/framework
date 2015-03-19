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
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
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

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(1);
        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(4, 5, 6, 7, 8);
    }

    @Test
    public void testColumnHiding_unhidingColumnsFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(1);
        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 4, 5, 6, 7, 8);

        toggleHideColumnAPI(1);
        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 2, 4, 5, 6);
    }

    @Test
    public void testColumnHiding_hidingUnhidingFromAPI_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testColumnHiding_changeVisibilityAPI_triggersClientSideEvent() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        selectMenuPath("Component", "Internals", "Listeners",
                "Add Column Visibility Change listener");

        toggleHideColumnAPI(2);
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

        toggleHideColumnAPI(2);
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
    public void testColumnHiding_changeVisibilityToggle_triggersClientSideEvent() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        selectMenuPath("Component", "Internals", "Listeners",
                "Add Column Visibility Change listener");

        toggleHidableColumnAPI(2);
        clickSidebarOpenButton();
        getColumnHidingToggle(2).click();
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
        assertEquals(true, userOriginated);
        assertEquals(true, hidden);

        getColumnHidingToggle(2).click();
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
        assertEquals(true, userOriginated);
        assertEquals(false, hidden);
    }

    @Test
    public void testColumnHidability_onTriggerColumnHidability_showsSidebarButton() {
        WebElement sidebar = getSidebar();
        assertNull(sidebar);

        toggleHidableColumnAPI(0);

        sidebar = getSidebar();
        assertNotNull(sidebar);
    }

    @Test
    public void testColumnHidability_triggeringColumnHidabilityWithSeveralColumns_showsAndHidesSiderbarButton() {
        verifySidebarNotVisible();

        toggleHidableColumnAPI(3);
        toggleHidableColumnAPI(4);

        verifySidebarVisible();

        toggleHidableColumnAPI(3);

        verifySidebarVisible();

        toggleHidableColumnAPI(4);

        verifySidebarNotVisible();
    }

    @Test
    public void testColumnHidability_clickingSidebarButton_opensClosesSidebar() {
        toggleHidableColumnAPI(0);
        verifySidebarClosed();

        clickSidebarOpenButton();

        verifySidebarOpened();

        clickSidebarOpenButton();

        verifySidebarClosed();
    }

    @Test
    public void testColumnHidability_settingColumnHidable_showsToggleInSidebar() {
        toggleHidableColumnAPI(0);
        verifySidebarClosed();
        clickSidebarOpenButton();
        verifySidebarOpened();

        verifyColumnHidingOption(0, false);
    }

    @Test
    public void testColumnHiding_hidingColumnWithToggle_works() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        toggleHidableColumnAPI(0);
        verifySidebarClosed();
        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(0, false);

        getColumnHidingToggle(0).click();
        verifyColumnHidingOption(0, true);
        assertColumnHeaderOrder(1, 2, 3, 4);

        getColumnHidingToggle(0).click();
        verifyColumnHidingOption(0, false);
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
    }

    @Test
    public void testColumnHiding_updatingHiddenWhileSidebarClosed_updatesToggleValue() {
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(3);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 2, 4);
        verifySidebarClosed();

        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(0, false);
        verifyColumnHidingOption(3, true);

        clickSidebarOpenButton();
        verifySidebarClosed();

        toggleHideColumnAPI(0);
        toggleHideColumnAPI(3);

        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(0, true);
        verifyColumnHidingOption(3, false);

    }

    @Test
    public void testColumnHiding_hidingMultipleColumnsWithToggle_hidesColumns() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4);

        toggleHideColumnAPI(1);
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(1);
        toggleHidableColumnAPI(2);
        toggleHidableColumnAPI(3);
        toggleHidableColumnAPI(4);
        verifySidebarClosed();
        assertColumnHeaderOrder(0, 2, 3, 4);

        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(0, false);
        verifyColumnHidingOption(1, true);
        verifyColumnHidingOption(2, false);
        verifyColumnHidingOption(3, false);
        verifyColumnHidingOption(4, false);

        // must be done in a funny order so that the header indexes won't break
        // (because of data source uses counter)
        getColumnHidingToggle(1).click();
        getColumnHidingToggle(2).click();
        getColumnHidingToggle(3).click();
        getColumnHidingToggle(4).click();
        getColumnHidingToggle(0).click();
        verifyColumnHidingOption(0, true);
        verifyColumnHidingOption(1, false);
        verifyColumnHidingOption(2, true);
        verifyColumnHidingOption(3, true);
        verifyColumnHidingOption(4, true);

        assertColumnHeaderOrder(1, 5, 6, 7);

        getColumnHidingToggle(0).click();
        getColumnHidingToggle(2).click();
        getColumnHidingToggle(1).click();
        verifyColumnHidingOption(0, false);
        verifyColumnHidingOption(1, true);
        verifyColumnHidingOption(2, false);
        assertColumnHeaderOrder(0, 2, 5, 6);
    }

    @Test
    public void testColumnHidability_changingHidabilityWhenSidebarClosed_addsRemovesToggles() {
        toggleHideColumnAPI(0);
        toggleHideColumnAPI(4);
        assertColumnHeaderOrder(1, 2, 3, 5);
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(3);
        toggleHidableColumnAPI(4);
        verifySidebarClosed();

        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(0, true);
        verifyColumnHidingOption(3, false);
        verifyColumnHidingOption(4, true);

        clickSidebarOpenButton();
        verifySidebarClosed();

        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(3);

        verifySidebarClosed();
        clickSidebarOpenButton();
        verifySidebarOpened();
        verifyColumnHidingOption(4, true);

        assertNull(getColumnHidingToggle(0));
        assertNull(getColumnHidingToggle(3));
    }

    @Test
    public void testColumnHidability_togglingHidability_placesTogglesInRightOrder() {
        toggleHidableColumnAPI(3);
        toggleHidableColumnAPI(2);
        clickSidebarOpenButton();

        verifyColumnHidingTogglesOrder(2, 3);

        toggleHidableColumnAPI(1);
        toggleHidableColumnAPI(2);
        toggleHidableColumnAPI(6);
        toggleHidableColumnAPI(0);

        verifyColumnHidingTogglesOrder(0, 1, 3, 6);

        clickSidebarOpenButton();

        toggleHidableColumnAPI(2);
        toggleHidableColumnAPI(4);
        toggleHidableColumnAPI(7);

        clickSidebarOpenButton();

        verifyColumnHidingTogglesOrder(0, 1, 2, 3, 4, 6, 7);
    }

    @Test
    public void testColumnHidability_reorderingColumns_updatesColumnToggleOrder() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(1);
        toggleHidableColumnAPI(3);
        toggleHidableColumnAPI(4);
        clickSidebarOpenButton();
        verifyColumnHidingTogglesOrder(0, 1, 3, 4);
        clickSidebarOpenButton();

        toggleColumnReorder();
        dragAndDropColumnHeader(0, 3, 0, 5);

        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        clickSidebarOpenButton();
        verifyColumnHidingTogglesOrder(3, 0, 1, 4);

        clickSidebarOpenButton();
        dragAndDropColumnHeader(0, 1, 3, 100);
        dragAndDropColumnHeader(0, 4, 0, 5);
        dragAndDropColumnHeader(0, 3, 0, 5);

        assertColumnHeaderOrder(2, 4, 3, 1, 0);
        clickSidebarOpenButton();
        verifyColumnHidingTogglesOrder(4, 3, 1, 0);
    }

    @Test
    public void testColumnHidingAndReorder_reorderingOverHiddenColumn_orderIsKept() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleColumnReorder();
        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);

        dragAndDropColumnHeader(0, 1, 0, 5);
        assertColumnHeaderOrder(2, 1, 3, 4, 5);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(0, 2, 1, 3, 4, 5);

        toggleHideColumnAPI(1);
        assertColumnHeaderOrder(0, 2, 3, 4, 5);

        // right side of hidden column
        dragAndDropColumnHeader(0, 0, 2, 5);
        assertColumnHeaderOrder(2, 0, 3, 4, 5);

        toggleHideColumnAPI(1);
        assertColumnHeaderOrder(2, 1, 0, 3, 4, 5);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(2, 1, 3, 4, 5);

        // left side of hidden column
        dragAndDropColumnHeader(0, 0, 1, 100);
        assertColumnHeaderOrder(1, 2, 3, 4, 5);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 0, 2, 3, 4, 5);
    }

    @Test
    public void testColumnHidingAndReorder_reorderingWithMultipleHiddenColumns_works() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleColumnReorder();
        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        dragAndDropDefaultColumnHeader(0, 2, 5);
        assertColumnHeaderOrder(1, 0, 4, 5, 6);

        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(1, 3, 0, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(1, 2, 3, 0, 4, 5, 6);

        toggleHideColumnAPI(0);
        toggleHideColumnAPI(4);
        assertColumnHeaderOrder(1, 2, 3, 5, 6);

        dragAndDropDefaultColumnHeader(4, 3, 2);
        assertColumnHeaderOrder(1, 2, 3, 6, 5);

        dragAndDropDefaultColumnHeader(4, 2, 100);
        assertColumnHeaderOrder(1, 2, 3, 5, 6);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 2, 3, 0, 5, 6);

        toggleHideColumnAPI(4);
        assertColumnHeaderOrder(1, 2, 3, 0, 4, 5, 6);
    }

    @Test
    public void testReorderingHiddenColumns_movingHiddenColumn_indexIsUpdated() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        moveColumnLeft(3);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 3, 4, 5, 6);
        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(0, 1, 3, 2, 4, 5, 6);

        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        moveColumnLeft(2);
        moveColumnLeft(2);
        moveColumnLeft(2);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(2, 0, 1, 4, 5, 6);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(2, 0, 1, 3, 4, 5, 6);
    }

    // keyboard actions not working in client side test case?
    @Test
    @Ignore
    public void testNavigationWithHiddenColumns_navigatingOverHiddenColumn_goesToNextVisibleColumn() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleHideColumnAPI(2);
        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(0, 1, 4, 5, 6);

        getGridElement().getCell(2, 4).click();
        GridCellElement cell = getGridElement().getCell(2, 4);
        assertTrue(cell.isFocused());

        new Actions(getDriver()).sendKeys(Keys.ARROW_LEFT);
        cell = getGridElement().getCell(2, 1);
        assertTrue(cell.isFocused());

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT);
        cell = getGridElement().getCell(2, 4);
        assertTrue(cell.isFocused());
    }

    @Test
    public void testNavigationWithHiddenColumns_hiddenFirstAndLastColumn_keepsNavigation() {
        selectMenuPath("Component", "State", "Width", "1000px");
        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(1, 2, 3, 4, 5, 6);

        getGridElement().getCell(2, 1).click();
        assertTrue(getGridElement().getCell(2, 1).isFocused());

        new Actions(getDriver()).sendKeys(Keys.ARROW_LEFT);
        GridCellElement cell = getGridElement().getCell(2, 1);
        assertTrue(cell.isFocused());

        scrollGridHorizontallyTo(10000);

        //
        getGridElement().getHeaderCell(0, 9).click();
        cell = getGridElement().getHeaderCell(0, 9);
        assertTrue(cell.isFocused());
        toggleHideColumnAPI(10);
        toggleHideColumnAPI(11);

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT);
        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT);
        toggleHideColumnAPI(10);
        toggleHideColumnAPI(11);
        cell = getGridElement().getHeaderCell(0, 9);
        assertTrue(cell.isFocused());
    }

    private void verifyColumnHidingTogglesOrder(int... indices) {
        WebElement sidebar = getSidebar();
        List<WebElement> elements = sidebar.findElements(By
                .className("column-hiding-toggle"));
        for (int i = 0; i < indices.length; i++) {
            WebElement e = elements.get(i);
            assertTrue(("Header (0," + indices[i] + ")").equalsIgnoreCase(e
                    .getText()));
        }
    }

    private void verifyColumnHidingOption(int columnIndex, boolean hidden) {
        WebElement columnHidingToggle = getColumnHidingToggle(columnIndex);
        assertEquals(hidden,
                columnHidingToggle.getAttribute("class").contains("hidden"));
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

    /**
     * Returns the toggle inside the sidebar for hiding the column at the given
     * index, or null if not found.
     */
    private WebElement getColumnHidingToggle(int columnIndex) {
        WebElement sidebar = getSidebar();
        List<WebElement> elements = sidebar.findElements(By
                .className("column-hiding-toggle"));
        for (WebElement e : elements) {
            if (("Header (0," + columnIndex + ")")
                    .equalsIgnoreCase(e.getText())) {
                return e;
            }
        }
        return null;
    }

    private void clickSidebarOpenButton() {
        getSidebarOpenButton().click();
    }

    private void moveColumnLeft(int index) {
        selectMenuPath("Component", "Columns", "Column " + index,
                "Move column left");
    }

    private void toggleHidableColumnAPI(int columnIndex) {
        selectMenuPath("Component", "Columns", "Column " + columnIndex,
                "Hidable");
    }

    private void toggleHideColumnAPI(int columnIndex) {
        selectMenuPath("Component", "Columns", "Column " + columnIndex,
                "Hidden");
    }
}
