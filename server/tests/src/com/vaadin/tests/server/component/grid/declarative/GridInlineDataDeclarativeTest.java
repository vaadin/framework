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
package com.vaadin.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.ui.Grid;

public class GridInlineDataDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSimpleInlineData() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Col1' />"
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Foo</tr>" //
                + "<tr><td>Bar</tr>" //
                + "<tr><td>Baz</tr>" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("Col1", String.class);
        grid.addRow("Foo");
        grid.addRow("Bar");
        grid.addRow("Baz");

        // Remove default header
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid, true);
        testRead(design, grid, true, true);
    }

    @Test
    public void testMultipleColumnsInlineData() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Col1' />"
                + "   <col sortable=true property-id='Col2' />"
                + "   <col sortable=true property-id='Col3' />" //
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Foo<td>Bar<td>Baz</tr>" //
                + "<tr><td>My<td>Summer<td>Car</tr>" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("Col1", String.class);
        grid.addColumn("Col2", String.class);
        grid.addColumn("Col3", String.class);
        grid.addRow("Foo", "Bar", "Baz");
        grid.addRow("My", "Summer", "Car");

        // Remove default header
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid, true);
        testRead(design, grid, true, true);
    }

    @Test
    public void testMultipleColumnsInlineDataReordered() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Col2' />"
                + "   <col sortable=true property-id='Col3' />"
                + "   <col sortable=true property-id='Col1' />" //
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Bar<td>Baz<td>Foo</tr>" //
                + "<tr><td>Summer<td>Car<td>My</tr>" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("Col1", String.class);
        grid.addColumn("Col2", String.class);
        grid.addColumn("Col3", String.class);
        grid.addRow("Foo", "Bar", "Baz");
        grid.addRow("My", "Summer", "Car");
        grid.setColumnOrder("Col2", "Col3", "Col1");

        // Remove default header
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid, true);
        testRead(design, grid, true, true);
    }
}
