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

public class GridHeaderFooterTest extends GridBasicsTest {

    protected static final String[] HEADER_TEXTS = IntStream
            .range(0, GridBasics.COLUMN_CAPTIONS.length)
            .mapToObj(i -> "Header cell " + i)
            .toArray(String[]::new);

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

        assertHeaderTexts(0, GridBasics.COLUMN_CAPTIONS);
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

    protected static void assertText(String expected, GridCellElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        String actual = e.findElement(By.tagName("div")).getAttribute(
                "innerHTML");
        assertEquals(expected, actual);
    }

    protected void assertHeaderTexts(int rowIndex, String[] texts) {
        List<GridCellElement> headerCells = getGridElement().getHeaderCells(
                rowIndex);

        assertEquals(texts.length, headerCells.size());
        for (int i = 0; i < headerCells.size(); i++) {
            assertText(texts[i], headerCells.get(i));
        }
    }

    protected void assertSortIndicator(GridCellElement cell, String classname) {
        assertTrue("Header cell should have sort indicator " + classname, cell
                .getAttribute("class").contains(classname));
    }

    protected void assertNoSortIndicator(GridCellElement cell,
            String classname) {
        assertFalse("Header cell should not have sort indicator " + classname,
                cell.getAttribute("class").contains(classname));
    }
}
