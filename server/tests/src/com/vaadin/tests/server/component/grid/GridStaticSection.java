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
package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridFooter;
import com.vaadin.ui.components.grid.GridFooter.FooterRow;
import com.vaadin.ui.components.grid.GridHeader;
import com.vaadin.ui.components.grid.GridHeader.HeaderRow;

public class GridStaticSection {

    private Indexed dataSource = new IndexedContainer();
    private Grid grid;

    @Before
    public void setUp() {
        dataSource.addContainerProperty("firstName", String.class, "");
        dataSource.addContainerProperty("lastName", String.class, "");
        dataSource.addContainerProperty("streetAddress", String.class, "");
        dataSource.addContainerProperty("zipCode", Integer.class, null);
        grid = new Grid(dataSource);
    }

    @Test
    public void testAddAndRemoveHeaders() {

        final GridHeader section = grid.getHeader();
        assertEquals(1, section.getRowCount());
        section.prependRow();
        assertEquals(2, section.getRowCount());
        section.removeRow(0);
        assertEquals(1, section.getRowCount());
        section.removeRow(0);
        assertEquals(0, section.getRowCount());
        assertEquals(null, section.getDefaultRow());
        HeaderRow row = section.appendRow();
        assertEquals(1, section.getRowCount());
        assertEquals(null, section.getDefaultRow());
        section.setDefaultRow(row);
        assertEquals(row, section.getDefaultRow());
    }

    @Test
    public void testAddAndRemoveFooters() {
        final GridFooter section = grid.getFooter();

        // By default there are no footer rows
        assertEquals(0, section.getRowCount());
        FooterRow row = section.appendRow();

        assertEquals(1, section.getRowCount());
        section.prependRow();
        assertEquals(2, section.getRowCount());
        assertEquals(row, section.getRow(1));
        section.removeRow(0);
        assertEquals(1, section.getRowCount());
        section.removeRow(0);
        assertEquals(0, section.getRowCount());
    }

    @Test
    public void testJoinHeaderCells() {
        final GridHeader section = grid.getHeader();
        HeaderRow mergeRow = section.prependRow();
        mergeRow.join("firstName", "lastName").setText("Name");
        mergeRow.join(mergeRow.getCell("streetAddress"),
                mergeRow.getCell("zipCode"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinHeaderCellsIncorrectly() {
        final GridHeader section = grid.getHeader();
        HeaderRow mergeRow = section.prependRow();
        mergeRow.join("firstName", "zipCode").setText("Name");
    }

    @Test
    public void testJoinAllFooterrCells() {
        final GridFooter section = grid.getFooter();
        FooterRow mergeRow = section.prependRow();
        mergeRow.join(dataSource.getContainerPropertyIds().toArray()).setText(
                "All the stuff.");
    }
}
