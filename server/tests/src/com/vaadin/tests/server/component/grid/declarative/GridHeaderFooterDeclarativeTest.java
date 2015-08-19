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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Label;

public class GridHeaderFooterDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSingleDefaultHeader() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead>" //
                + "   <tr default='true'><th plain-text=''>Column1<th plain-text=''>Column2<th plain-text=''>Column3</tr>" //
                + "</thead>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testSingleDefaultHTMLHeader() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead>" //
                + "   <tr default='true'><th>Column1<th>Column2<th>Column3</tr>" //
                + "</thead>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        HeaderRow row = grid.getDefaultHeaderRow();
        for (Column c : grid.getColumns()) {
            row.getCell(c.getPropertyId()).setHtml(c.getHeaderCaption());
        }

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testNoHeaderRows() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "</colgroup>" //
                + "<thead />" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testMultipleHeadersWithColSpans() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead>" //
                + "   <tr><th colspan=3>Baz</tr>"
                + "   <tr default='true'><th>Column1<th>Column2<th>Column3</tr>" //
                + "   <tr><th>Foo<th colspan=2>Bar</tr>" //
                + "</thead>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        HeaderRow row = grid.getDefaultHeaderRow();
        for (Column c : grid.getColumns()) {
            row.getCell(c.getPropertyId()).setHtml(c.getHeaderCaption());
        }

        grid.prependHeaderRow().join("Column1", "Column2", "Column3")
                .setHtml("Baz");
        row = grid.appendHeaderRow();
        row.getCell("Column1").setHtml("Foo");
        row.join("Column2", "Column3").setHtml("Bar");

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testSingleDefaultFooter() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tfoot>" //
                + "   <tr><td plain-text=''>Column1<td plain-text=''>Column2<td plain-text=''>Column3</tr>" //
                + "</tfoot>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        FooterRow row = grid.appendFooterRow();
        for (Column c : grid.getColumns()) {
            row.getCell(c.getPropertyId()).setText(c.getHeaderCaption());
        }

        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testSingleDefaultHTMLFooter() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tfoot>" //
                + "   <tr><td>Column1<td>Column2<td>Column3</tr>" //
                + "</tfoot>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        FooterRow row = grid.appendFooterRow();
        for (Column c : grid.getColumns()) {
            row.getCell(c.getPropertyId()).setHtml(c.getHeaderCaption());
        }

        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testMultipleFootersWithColSpans() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "   <col sortable=true property-id='Column2'>"
                + "   <col sortable=true property-id='Column3'>"
                + "</colgroup>" //
                + "<thead />" // No headers read or written.
                + "<tfoot>" //
                + "   <tr><td colspan=3>Baz</tr>"
                + "   <tr><td>Column1<td>Column2<td>Column3</tr>" //
                + "   <tr><td>Foo<td colspan=2>Bar</tr>" //
                + "</tfoot>" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        FooterRow row = grid.appendFooterRow();
        for (Column c : grid.getColumns()) {
            row.getCell(c.getPropertyId()).setHtml(c.getHeaderCaption());
        }

        grid.prependFooterRow().join("Column1", "Column2", "Column3")
                .setHtml("Baz");
        row = grid.appendFooterRow();
        row.getCell("Column1").setHtml("Foo");
        row.join("Column2", "Column3").setHtml("Bar");

        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testComponentInGridHeader() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "</colgroup>" //
                + "<thead>" //
                + "<tr default=true><th><v-label><b>Foo</b></v-label></tr>"
                + "</thead>"//
                + "</table></v-grid>";

        Label component = new Label("<b>Foo</b>");
        component.setContentMode(ContentMode.HTML);

        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.getDefaultHeaderRow().getCell("Column1").setComponent(component);

        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testComponentInGridFooter() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true property-id='Column1'>"
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tfoot>" //
                + "<tr><td><v-label><b>Foo</b></v-label></tr>"//
                + "</tfoot>" //
                + "</table></v-grid>";

        Label component = new Label("<b>Foo</b>");
        component.setContentMode(ContentMode.HTML);

        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.prependFooterRow().getCell("Column1").setComponent(component);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testRead(design, grid, true);
        testWrite(design, grid);
    }
}
