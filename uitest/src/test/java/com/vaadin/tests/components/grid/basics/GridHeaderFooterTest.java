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

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;

public class GridHeaderFooterTest extends GridBasicsTest {

    @Test
    public void initialState_defaultHeaderPresent() {
        assertEquals(1, getGridElement().getHeaderCount());

        final String[] captions = GridBasics.COLUMN_CAPTIONS;
        List<GridCellElement> headerCells = getGridElement().getHeaderCells(0);

        assertEquals(captions.length, headerCells.size());
        for (int i = 0; i < headerCells.size(); i++) {
            assertText(captions[i], headerCells.get(i));
        }
    }

    @Test
    public void appendHeaderRow_addedToBottom() {
        selectMenuPath("Component", "Header", "Append header row");

        assertEquals(2, getGridElement().getHeaderCount());
    }

    @Test
    public void prependHeaderRow_addedToBottom() {
        selectMenuPath("Component", "Header", "Prepend header row");

        assertEquals(2, getGridElement().getHeaderCount());
    }

    @Test
    public void removeDefaultHeaderRow_noHeaderRows() {
        selectMenuPath("Component", "Header", "Remove first header row");

        assertEquals(0, getGridElement().getHeaderCount());
    }

    protected static void assertText(String expected, GridCellElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        String actual = e.findElement(By.tagName("div")).getAttribute(
                "innerHTML");
        assertEquals(expected, actual);
    }
}
