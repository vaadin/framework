/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;

public class GridHeaderFooterTest extends GridBasicsTest {

    protected static final String[] HEADER_TEXTS = IntStream
            .range(0, GridBasics.COLUMN_CAPTIONS.length)
            .mapToObj(i -> "Header cell " + i).toArray(String[]::new);

    protected static final String[] FOOTER_TEXTS = IntStream
            .range(0, GridBasics.COLUMN_CAPTIONS.length)
            .mapToObj(i -> "Footer cell " + i).toArray(String[]::new);

    @Override
    public void setUp() {
        super.setUp();

        selectMenuPath("Component", "Footer", "Add default footer row");
    }

    @Test
    public void initialState_defaultHeaderPresent() {
        assertEquals(1, getGridElement().getHeaderCount());
        assertHeaderTexts(0, GridBasics.COLUMN_CAPTIONS);
    }

    @Test
    public void appendHeaderRow_addedToBottom() {
        selectMenuPath("Component", "Header", "Append header row");

        assertEquals(2, getGridElement().getHeaderCount());
        assertHeaderTexts(0, GridBasics.COLUMN_CAPTIONS);
        assertHeaderTexts(1, HEADER_TEXTS);
    }

    @Test
    public void prependHeaderRow_addedToTop() {
        selectMenuPath("Component", "Header", "Prepend header row");

        assertEquals(2, getGridElement().getHeaderCount());
        assertHeaderTexts(0, HEADER_TEXTS);
        assertHeaderTexts(1, GridBasics.COLUMN_CAPTIONS);
    }

    @Test
    public void removeDefaultHeaderRow_noHeaderRows() {
        selectMenuPath("Component", "Header", "Remove first header row");

        assertEquals(0, getGridElement().getHeaderCount());
    }

    @Test
    public void setDefaultRow_headerCaptionsUpdated() {
        selectMenuPath("Component", "Header", "Prepend header row");
        selectMenuPath("Component", "Header", "Set first row as default");

        assertHeaderTexts(0, HEADER_TEXTS);
    }

    @Test
    public void clickDefaultHeaderCell_sortIndicatorPresent() {
        GridCellElement headerCell = getGridElement().getHeaderCell(0, 2);
        headerCell.click();

        assertSortIndicator(headerCell, "sort-asc");

        headerCell.click();
        assertNoSortIndicator(headerCell, "sort-asc");
        assertSortIndicator(headerCell, "sort-desc");

        GridCellElement anotherCell = getGridElement().getHeaderCell(0, 3);
        anotherCell.click();

        assertNoSortIndicator(headerCell, "sort-asc");
        assertNoSortIndicator(headerCell, "sort-desc");
        assertSortIndicator(anotherCell, "sort-asc");
    }

    @Test
    public void noDefaultRow_clickHeaderCell_sortIndicatorsNotPresent() {
        selectMenuPath("Component", "Header", "Set no default row");

        GridCellElement headerCell = getGridElement().getHeaderCell(0, 2);
        headerCell.click();

        assertNoSortIndicator(headerCell, "sort-asc");
        assertNoSortIndicator(headerCell, "sort-desc");
    }

    @Test
    public void initialState_defaultFooterPresent() {
        assertEquals(1, getGridElement().getFooterCount());
        assertFooterTexts(0, GridBasics.COLUMN_CAPTIONS);
    }

    @Test
    public void appendFooterRow_addedToBottom() {
        selectMenuPath("Component", "Footer", "Append footer row");

        assertEquals(2, getGridElement().getFooterCount());
        assertFooterTexts(0, GridBasics.COLUMN_CAPTIONS);
        assertFooterTexts(1, FOOTER_TEXTS);
    }

    @Test
    public void prependFooterRow_addedToTop() {
        selectMenuPath("Component", "Footer", "Prepend footer row");

        assertEquals(2, getGridElement().getFooterCount());
        assertFooterTexts(0, FOOTER_TEXTS);
        assertFooterTexts(1, GridBasics.COLUMN_CAPTIONS);
    }

    @Test
    public void testDynamicallyChangingHeaderCellType() throws Exception {
        selectMenuPath("Component", "Columns", "Column 0", "Header Type",
                "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        assertTrue(widgetCell.isElementPresent(By.className("v-button")));

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "HTML Header");
        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 1);
        assertEquals("<b>HTML Header</b>",
                htmlCell.findElement(
                        By.className("v-grid-column-header-content"))
                        .getAttribute("innerHTML"));

        selectMenuPath("Component", "Columns", "Column 2", "Header Type",
                "Text Header");
        GridCellElement textCell = getGridElement().getHeaderCell(0, 2);

        assertEquals("text header", textCell.getText().toLowerCase());
    }

    @Test
    public void testButtonInHeader() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getGridElement().findElements(By.className("v-button")).get(0).click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromHeader() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");
        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Text Header");
        assertTrue("No notifications should've been shown",
                !$(NotificationElement.class).exists());
        assertEquals("Header should've been reverted back to text header",
                "text header",
                getGridElement().getHeaderCell(0, 1).getText().toLowerCase());
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToHeader_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        assertEquals("column 1",
                getGridElement().getHeaderCell(0, 1).getText().toLowerCase());
        assertEquals("Column 1", getColumnHidingToggle(1).getText());

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "Widget Header");

        getSidebarOpenButton().click();
        assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    @Test
    public void testDynamicallyChangingFooterCellType() throws Exception {
        selectMenuPath("Component", "Columns", "Column 0", "Footer Type",
                "Widget Footer");
        GridCellElement widgetCell = getGridElement().getFooterCell(0, 0);
        assertTrue(widgetCell.isElementPresent(By.className("v-button")));

        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "HTML Footer");
        GridCellElement htmlCell = getGridElement().getFooterCell(0, 1);
        assertEquals("<b>HTML Footer</b>",
                htmlCell.findElement(
                        By.className("v-grid-column-footer-content"))
                        .getAttribute("innerHTML"));

        selectMenuPath("Component", "Columns", "Column 2", "Footer Type",
                "Text Footer");
        GridCellElement textCell = getGridElement().getFooterCell(0, 2);

        assertEquals("text footer", textCell.getText().toLowerCase());
    }

    @Test
    public void testButtonInFooter() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");

        getGridElement().findElements(By.className("v-button")).get(0).click();

        assertTrue("Button click should be logged",
                logContainsText("Button clicked!"));
    }

    @Test
    public void testRemoveComponentFromFooter() throws Exception {
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");
        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Text Footer");
        assertTrue("No notifications should've been shown",
                !$(NotificationElement.class).exists());
        assertEquals("Footer should've been reverted back to text footer",
                "text footer",
                getGridElement().getFooterCell(0, 1).getText().toLowerCase());
    }

    @Test
    public void testColumnHidingToggleCaption_settingWidgetToFooter_toggleCaptionStays() {
        toggleColumnHidable(1);
        getSidebarOpenButton().click();
        assertEquals("column 1",
                getGridElement().getHeaderCell(0, 1).getText().toLowerCase());
        assertEquals("Column 1", getColumnHidingToggle(1).getText());

        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "Widget Footer");

        getSidebarOpenButton().click();
        assertEquals("Column 1", getColumnHidingToggle(1).getText());
    }

    private void toggleColumnHidable(int index) {
        selectMenuPath("Component", "Columns", "Column " + index, "Hidable");
    }

    protected static void assertText(String expected, GridCellElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        String actual = e.findElement(By.tagName("div"))
                .getAttribute("innerHTML");
        assertEquals(expected, actual);
    }

    protected void assertHeaderTexts(int rowIndex, String[] texts) {
        List<GridCellElement> headerCells = getGridElement()
                .getHeaderCells(rowIndex);

        assertEquals(texts.length, headerCells.size());
        for (int i = 0; i < headerCells.size(); i++) {
            assertText(texts[i], headerCells.get(i));
        }
    }

    protected void assertFooterTexts(int rowIndex, String[] texts) {
        List<GridCellElement> footerCells = getGridElement()
                .getFooterCells(rowIndex);

        assertEquals(texts.length, footerCells.size());
        for (int i = 0; i < footerCells.size(); i++) {
            assertText(texts[i], footerCells.get(i));
        }
    }

    protected void assertSortIndicator(GridCellElement cell, String classname) {
        assertTrue("Header cell should have sort indicator " + classname,
                cell.getAttribute("class").contains(classname));
    }

    protected void assertNoSortIndicator(GridCellElement cell,
            String classname) {
        assertFalse("Header cell should not have sort indicator " + classname,
                cell.getAttribute("class").contains(classname));
    }
}
