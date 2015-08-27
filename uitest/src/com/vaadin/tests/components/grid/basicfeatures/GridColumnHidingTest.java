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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;

@TestCategory("grid")
public class GridColumnHidingTest extends GridBasicClientFeaturesTest {

    private static final String CAPTION_0_1 = "Join column cells 0, 1";
    private static final String CAPTION_1_2 = "Join columns 1, 2";
    private static final String CAPTION_3_4_5 = "Join columns 3, 4, 5";
    private static final String CAPTION_ALL = "Join all columns";

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

        // Selecting from the menu closes the sidebar
        clickSidebarOpenButton();

        verifyColumnHidingTogglesOrder(0, 1, 3, 6);

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
        dragAndDropColumnHeader(0, 3, 0, CellSide.LEFT);

        assertColumnHeaderOrder(3, 0, 1, 2, 4);
        clickSidebarOpenButton();
        verifyColumnHidingTogglesOrder(3, 0, 1, 4);

        clickSidebarOpenButton();
        dragAndDropColumnHeader(0, 1, 3, CellSide.RIGHT);
        dragAndDropColumnHeader(0, 4, 0, CellSide.LEFT);
        dragAndDropColumnHeader(0, 3, 0, CellSide.LEFT);

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

        dragAndDropColumnHeader(0, 1, 0, CellSide.LEFT);
        assertColumnHeaderOrder(2, 1, 3, 4, 5);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(0, 2, 1, 3, 4, 5);

        toggleHideColumnAPI(1);
        assertColumnHeaderOrder(0, 2, 3, 4, 5);

        // right side of hidden column
        dragAndDropColumnHeader(0, 0, 2, CellSide.LEFT);
        assertColumnHeaderOrder(2, 0, 3, 4, 5);

        toggleHideColumnAPI(1);
        assertColumnHeaderOrder(2, 1, 0, 3, 4, 5);

        toggleHideColumnAPI(0);
        assertColumnHeaderOrder(2, 1, 3, 4, 5);

        // left side of hidden column
        dragAndDropColumnHeader(0, 0, 1, CellSide.RIGHT);
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

        dragAndDropDefaultColumnHeader(0, 2, CellSide.LEFT);
        assertColumnHeaderOrder(1, 0, 4, 5, 6);

        toggleHideColumnAPI(3);
        assertColumnHeaderOrder(1, 3, 0, 4, 5, 6);

        toggleHideColumnAPI(2);
        assertColumnHeaderOrder(1, 2, 3, 0, 4, 5, 6);

        toggleHideColumnAPI(0);
        toggleHideColumnAPI(4);
        assertColumnHeaderOrder(1, 2, 3, 5, 6);

        dragAndDropDefaultColumnHeader(4, 3, CellSide.LEFT);
        assertColumnHeaderOrder(1, 2, 3, 6, 5);

        dragAndDropDefaultColumnHeader(4, 2, CellSide.RIGHT);
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

    @Test
    public void testFrozenColumnHiding_lastFrozenColumnHidden_isFrozenWhenMadeVisible() {
        toggleFrozenColumns(2);
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(1);
        getSidebarOpenButton().click();
        verifyColumnIsFrozen(0);
        verifyColumnIsFrozen(1);
        verifyColumnIsNotFrozen(2);
        assertColumnHeaderOrder(0, 1, 2, 3);

        getColumnHidingToggle(1).click();
        verifyColumnIsFrozen(0);
        // the grid element indexing doesn't take hidden columns into account!
        verifyColumnIsNotFrozen(1);
        assertColumnHeaderOrder(0, 2, 3);

        getColumnHidingToggle(0).click();
        verifyColumnIsNotFrozen(0);
        assertColumnHeaderOrder(2, 3, 4);

        getColumnHidingToggle(0).click();
        assertColumnHeaderOrder(0, 2, 3);
        verifyColumnIsFrozen(0);
        verifyColumnIsNotFrozen(1);

        getColumnHidingToggle(1).click();
        assertColumnHeaderOrder(0, 1, 2, 3);
        verifyColumnIsFrozen(0);
        verifyColumnIsFrozen(1);
        verifyColumnIsNotFrozen(2);
    }

    @Test
    public void testFrozenColumnHiding_columnHiddenFrozenCountChanged_columnIsFrozenWhenVisible() {
        toggleHidableColumnAPI(1);
        toggleHidableColumnAPI(2);
        getSidebarOpenButton().click();
        getColumnHidingToggle(1).click();
        getColumnHidingToggle(2).click();
        assertColumnHeaderOrder(0, 3, 4);

        toggleFrozenColumns(3);
        verifyColumnIsFrozen(0);
        // the grid element indexing doesn't take hidden columns into account!
        verifyColumnIsNotFrozen(1);
        verifyColumnIsNotFrozen(2);

        getSidebarOpenButton().click();
        getColumnHidingToggle(2).click();
        verifyColumnIsFrozen(0);
        verifyColumnIsFrozen(1);
        verifyColumnIsNotFrozen(2);
        verifyColumnIsNotFrozen(3);

        getColumnHidingToggle(1).click();
        verifyColumnIsFrozen(0);
        verifyColumnIsFrozen(1);
        verifyColumnIsFrozen(2);
        verifyColumnIsNotFrozen(3);
        verifyColumnIsNotFrozen(4);
    }

    @Test
    public void testSpannedCells_hidingColumnInBeginning_rendersSpannedCellCorrectly() {
        loadSpannedCellsFixture();
        verifySpannedCellsFixtureStart();

        toggleHideColumnAPI(0);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 6);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 2, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 0, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 1);
        verifyHeaderCellColspan(1, 2, 3);
        verifyHeaderCellColspan(2, 1, 2);

        toggleHideColumnAPI(0);

        verifySpannedCellsFixtureStart();

        toggleHideColumnAPI(1);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 7);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 2, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 1);
        verifyHeaderCellColspan(1, 2, 3);
        verifyHeaderCellColspan(2, 1, 1);

        toggleHideColumnAPI(3);

        verifyNumberOfCellsInHeader(0, 6);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 6);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 2, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 1);
        verifyHeaderCellColspan(1, 2, 2);
        verifyHeaderCellColspan(2, 1, 1);

        toggleHideColumnAPI(1);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 6);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 2);
        verifyHeaderCellColspan(1, 3, 2);
        verifyHeaderCellColspan(2, 1, 2);

        toggleHideColumnAPI(3);

        verifySpannedCellsFixtureStart();
    }

    @Test
    public void testSpannedCells_hidingColumnInMiddle_rendersSpannedCellCorrectly() {
        loadSpannedCellsFixture();
        verifySpannedCellsFixtureStart();

        toggleHideColumnAPI(4);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 6);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 2);
        verifyHeaderCellColspan(1, 3, 2);
        verifyHeaderCellColspan(2, 1, 2);

        toggleHideColumnAPI(4);

        verifySpannedCellsFixtureStart();
    }

    @Test
    public void testSpannedCells_hidingColumnInEnd_rendersSpannedCellCorrectly() {
        loadSpannedCellsFixture();
        verifySpannedCellsFixtureStart();

        toggleHideColumnAPI(1);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 7);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 2, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 1, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 1);
        verifyHeaderCellColspan(1, 2, 3);
        verifyHeaderCellColspan(2, 1, 1);

        toggleHideColumnAPI(1);

        verifySpannedCellsFixtureStart();

        toggleHideColumnAPI(2);

        verifyNumberOfCellsInHeader(0, 7);
        verifyNumberOfCellsInHeader(1, 4);
        verifyNumberOfCellsInHeader(2, 7);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 2);
        verifyHeaderCellColspan(1, 3, 3);
        verifyHeaderCellColspan(2, 1, 1);

        toggleHideColumnAPI(5);

        verifyNumberOfCellsInHeader(0, 6);
        verifyNumberOfCellsInHeader(1, 4);
        verifyNumberOfCellsInHeader(2, 6);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 2);
        verifyHeaderCellColspan(1, 3, 2);
        verifyHeaderCellColspan(2, 1, 1);

        toggleHideColumnAPI(5);
        toggleHideColumnAPI(2);

        verifySpannedCellsFixtureStart();
    }

    @Test
    public void testSpannedCells_spanningCellOverHiddenColumn_rendersSpannedCellCorrectly() {
        selectMenuPath("Component", "State", "Width", "1000px");
        appendHeaderRow();
        toggleHideColumnAPI(4);
        toggleHideColumnAPI(8);
        toggleHideColumnAPI(9);
        toggleHideColumnAPI(10);
        toggleHideColumnAPI(11);
        assertColumnHeaderOrder(0, 1, 2, 3, 5, 6, 7);
        verifyNumberOfCellsInHeader(1, 7);

        mergeHeaderCellsTwoThreeFour(2);

        verifyNumberOfCellsInHeader(1, 6);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellColspan(1, 3, 2);
    }

    @Test
    public void testSpannedCells_spanningCellAllHiddenColumns_rendersSpannedCellCorrectly() {
        selectMenuPath("Component", "State", "Width", "1000px");
        appendHeaderRow();
        toggleHideColumnAPI(3);
        toggleHideColumnAPI(4);
        toggleHideColumnAPI(5);
        toggleHideColumnAPI(8);
        toggleHideColumnAPI(9);
        toggleHideColumnAPI(10);
        toggleHideColumnAPI(11);
        assertColumnHeaderOrder(0, 1, 2, 6, 7);
        verifyNumberOfCellsInHeader(1, 5);

        mergeHeaderCellsTwoThreeFour(2);

        verifyNumberOfCellsInHeader(1, 5);
        verifyHeaderCellColspan(1, 0, 1);
        verifyHeaderCellColspan(1, 1, 1);
        verifyHeaderCellColspan(1, 2, 1);
        verifyHeaderCellColspan(1, 3, 1);
        verifyHeaderCellColspan(1, 4, 1);
    }

    @Test
    public void testColumnHiding_detailsRowIsOpen_renderedCorrectly() {
        selectMenuPath("Component", "Row details", "Set generator");
        selectMenuPath("Component", "Row details", "Toggle details for...",
                "Row 1");
        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        Assert.assertNotNull("Details not found", getGridElement()
                .getDetails(1));

        toggleHideColumnAPI(0);

        assertColumnHeaderOrder(1, 2, 3, 4);
        Assert.assertNotNull("Details not found", getGridElement()
                .getDetails(1));

        toggleHideColumnAPI(0);

        assertColumnHeaderOrder(0, 1, 2, 3, 4);
        Assert.assertNotNull("Details not found", getGridElement()
                .getDetails(1));
    }

    @Test
    public void testHideShowAllColumns() {
        selectMenuPath("Component", "State", "Width", "1000px");
        int colCount = 12;
        for (int i = 0; i < colCount; i++) {
            toggleHidableColumnAPI(i);
        }
        clickSidebarOpenButton();
        for (int i = 0; i < colCount; i++) {
            getColumnHidingToggle(i).click();
        }

        clickSidebarOpenButton();
        // All columns hidden
        assertEquals(0, getGridHeaderRowCells().size());
        clickSidebarOpenButton();
        for (int i = 0; i < colCount; i++) {
            getColumnHidingToggle(i).click();
        }

        assertEquals(colCount, getGridHeaderRowCells().size());
    }

    @Test
    public void testColumnHidingPopupClosedWhenClickingOutside() {
        selectMenuPath("Component", "Columns", "Column 0", "Hidable");
        getSidebarOpenButton().click();
        verifySidebarOpened();
        // Click somewhere far from Grid.
        new Actions(getDriver())
                .moveToElement(findElement(By.className("v-app")), 600, 600)
                .click().perform();
        verifySidebarClosed();
    }

    @Test
    public void hideScrollAndShow() {
        toggleHidableColumnAPI(1);
        toggleHideColumnAPI(1);

        getGridElement().scrollToRow(500);
        Assert.assertEquals("(500, 0)", getGridElement().getCell(500, 0)
                .getText());
        Assert.assertEquals("(500, 2)", getGridElement().getCell(500, 1)
                .getText());

        toggleHideColumnAPI(1);

        Assert.assertEquals("(500, 0)", getGridElement().getCell(500, 0)
                .getText());
        Assert.assertEquals("(500, 1)", getGridElement().getCell(500, 1)
                .getText());
    }

    @Test
    public void scrollHideAndShow() {
        toggleHidableColumnAPI(0);
        toggleHidableColumnAPI(1);

        Assert.assertEquals("(500, 0)", getGridElement().getCell(500, 0)
                .getText());
        Assert.assertEquals("(500, 1)", getGridElement().getCell(500, 1)
                .getText());

        toggleHideColumnAPI(0);
        toggleHideColumnAPI(1);

        Assert.assertEquals("(500, 2)", getGridElement().getCell(500, 0)
                .getText());
        Assert.assertEquals("(500, 3)", getGridElement().getCell(500, 1)
                .getText());

        toggleHideColumnAPI(0);
        toggleHideColumnAPI(1);

        Assert.assertEquals("(500, 0)", getGridElement().getCell(500, 0)
                .getText());
        Assert.assertEquals("(500, 1)", getGridElement().getCell(500, 1)
                .getText());
    }

    private void loadSpannedCellsFixture() {
        selectMenuPath("Component", "State", "Width", "1000px");
        appendHeaderRow();
        appendHeaderRow();
        appendHeaderRow();
        mergeHeaderCellsTwoThreeFour(2);
        mergeHeaderCellsZeroOne(2);
        mergeHeaderCellsOneTwo(3);
        mergeHeaderCellsAll(4);
        toggleHideColumnAPI(8);
        toggleHideColumnAPI(9);
        toggleHideColumnAPI(10);
        toggleHideColumnAPI(11);
    }

    private void verifySpannedCellsFixtureStart() {
        assertColumnHeaderOrder(0, 1, 2, 3, 4, 5, 6, 7);
        verifyNumberOfCellsInHeader(0, 8);
        verifyNumberOfCellsInHeader(1, 5);
        verifyNumberOfCellsInHeader(2, 7);
        verifyNumberOfCellsInHeader(3, 1);
        verifyHeaderCellContent(1, 0, CAPTION_0_1);
        verifyHeaderCellContent(1, 3, CAPTION_3_4_5);
        verifyHeaderCellContent(2, 1, CAPTION_1_2);
        verifyHeaderCellContent(3, 0, CAPTION_ALL);
        verifyHeaderCellColspan(1, 0, 2);
        verifyHeaderCellColspan(1, 3, 3);
        verifyHeaderCellColspan(2, 1, 2);
    }

    private void toggleFrozenColumns(int count) {
        selectMenuPath("Component", "State", "Frozen column count", count
                + " columns");
    }

    private void verifyHeaderCellColspan(int row, int column, int colspan) {
        try {
            assertEquals(Integer.valueOf(colspan), Integer.valueOf(Integer
                    .parseInt(getGridElement().getHeaderCell(row, column)
                            .getAttribute("colspan"))));
        } catch (NumberFormatException nfe) {
            // IE8 has colSpan
            assertEquals(Integer.valueOf(colspan), Integer.valueOf(Integer
                    .parseInt(getGridElement().getHeaderCell(row, column)
                            .getAttribute("colSpan"))));
        }
    }

    private void verifyNumberOfCellsInHeader(int row, int numberOfCells) {
        int size = 0;
        for (TestBenchElement cell : getGridElement().getHeaderCells(row)) {
            if (cell.isDisplayed()) {
                size++;
            }
        }
        assertEquals(numberOfCells, size);
    }

    private void verifyHeaderCellContent(int row, int column, String content) {
        GridCellElement headerCell = getGridElement()
                .getHeaderCell(row, column);
        assertEquals(content.toLowerCase(), headerCell.getText().toLowerCase());
        assertTrue(headerCell.isDisplayed());
    }

    private void verifyColumnIsFrozen(int index) {
        assertTrue(getGridElement().getHeaderCell(0, index).isFrozen());
    }

    private void verifyColumnIsNotFrozen(int index) {
        assertFalse(getGridElement().getHeaderCell(0, index).isFrozen());
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
        assertTrue(sidebar.getAttribute("class").contains("open"));
    }

    private void verifySidebarClosed() {
        WebElement sidebar = getSidebar();
        assertFalse(sidebar.getAttribute("class").contains("open"));
    }

    private void verifySidebarNotVisible() {
        WebElement sidebar = getSidebar();
        assertNull(sidebar);
    }

    private void verifySidebarVisible() {
        WebElement sidebar = getSidebar();
        assertNotNull(sidebar);
    }

    @Override
    protected WebElement getSidebar() {
        List<WebElement> elements = findElements(By.className("v-grid-sidebar"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    @Override
    protected WebElement getSidebarOpenButton() {
        List<WebElement> elements = findElements(By
                .className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    /**
     * Returns the toggle inside the sidebar for hiding the column at the given
     * index, or null if not found.
     */
    @Override
    protected WebElement getColumnHidingToggle(int columnIndex) {
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

    private void appendHeaderRow() {
        selectMenuPath("Component", "Header", "Append row");
    }

    private void mergeHeaderCellsZeroOne(int row) {
        selectMenuPath("Component", "Header", "Row " + row, CAPTION_0_1);
    }

    private void mergeHeaderCellsOneTwo(int row) {
        selectMenuPath("Component", "Header", "Row " + row, CAPTION_1_2);
    }

    private void mergeHeaderCellsTwoThreeFour(int row) {
        selectMenuPath("Component", "Header", "Row " + row, CAPTION_3_4_5);
    }

    private void mergeHeaderCellsAll(int row) {
        selectMenuPath("Component", "Header", "Row " + row, CAPTION_ALL);
    }

}
