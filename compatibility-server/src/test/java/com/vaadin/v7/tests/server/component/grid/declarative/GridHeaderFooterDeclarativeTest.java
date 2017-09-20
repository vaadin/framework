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
package com.vaadin.v7.tests.server.component.grid.declarative;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.FooterRow;
import com.vaadin.v7.ui.Grid.HeaderRow;

public class GridHeaderFooterDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSingleDefaultHeader() {
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>"
                + "</colgroup>"
                + "<thead>"
                + "   <tr default><th plain-text>Column1<th plain-text>Column2<th plain-text>Column3</tr>"
                + "</thead>"
                + "</table></vaadin7-grid>";
        //@formatter:on
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.addColumn("Column2", String.class);
        grid.addColumn("Column3", String.class);

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testSingleDefaultHTMLHeader() {
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>" + "</colgroup>"
                + "<thead>"
                + "   <tr default><th>Column1<th>Column2<th>Column3</tr>"
                + "</thead>"
                + "</table></vaadin7-grid>";
        //@formatter:on
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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "</colgroup>"
                + "<thead />"
                + "</table></vaadin7-grid>";
        //@formatter:on
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testWrite(design, grid);
        testRead(design, grid, true);
    }

    @Test
    public void testMultipleHeadersWithColSpans() {
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>"
                + "</colgroup>"
                + "<thead>"
                + "   <tr><th colspan=3>Baz</tr>"
                + "   <tr default><th>Column1<th>Column2<th>Column3</tr>"
                + "   <tr><th>Foo<th colspan=2>Bar</tr>"
                + "</thead>"
                + "</table></vaadin7-grid>";
        //@formatter:on
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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>"
                + "</colgroup>"
                + "<thead />" // No headers read or written
                + "<tfoot>"
                + "   <tr><td plain-text>Column1<td plain-text>Column2<td plain-text>Column3</tr>"
                + "</tfoot>"
                + "</table></vaadin7-grid>";
        //@formatter:on
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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>" + "</colgroup>"
                + "<thead />" // No headers read or written
                + "<tfoot>"
                + "   <tr><td>Column1<td>Column2<td>Column3</tr>"
                + "</tfoot>"
                + "</table></vaadin7-grid>";
        //@formatter:on

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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "   <col sortable property-id='Column2'>"
                + "   <col sortable property-id='Column3'>"
                + "</colgroup>"
                + "<thead />" // No headers read or written.
                + "<tfoot>"
                + "   <tr><td colspan=3>Baz</tr>"
                + "   <tr><td>Column1<td>Column2<td>Column3</tr>"
                + "   <tr><td>Foo<td colspan=2>Bar</tr>"
                + "</tfoot>"
                + "</table></vaadin7-grid>";
        //@formatter:on

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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "</colgroup>"
                + "<thead>"
                + "<tr default><th><vaadin-label><b>Foo</b></vaadin-label></tr>"
                + "</thead>"
                + "</table></vaadin7-grid>";
        //@formatter:on
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
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "   <col sortable property-id='Column1'>"
                + "</colgroup>"
                + "<thead />" // No headers read or written
                + "<tfoot>"
                + "<tr><td><vaadin-label><b>Foo</b></vaadin-label></tr>"
                + "</tfoot>"
                + "</table></vaadin7-grid>";
        //@formatter:on

        Label component = new Label("<b>Foo</b>");
        component.setContentMode(ContentMode.HTML);

        Grid grid = new Grid();
        grid.addColumn("Column1", String.class);
        grid.prependFooterRow().getCell("Column1").setComponent(component);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testHtmlEntitiesinGridHeaderFooter() {
        //@formatter:off
        String design = "<vaadin7-grid><table>"
                + "<colgroup>"
                + "  <col sortable=\"true\" property-id=\"> test\">"
                + "</colgroup>"
                + "<thead>"
                + "  <tr><th plain-text=\"true\">&gt; Test</th></tr>"
                + "</thead>"
                + "<tfoot>"
                + "  <tr><td plain-text=\"true\">&gt; Test</td></tr>"
                + "</tfoot>"
                + "<tbody />"
                + "</table></vaadin7-grid>";
        //@formatter:on

        Grid grid = read(design);
        String actualHeader = grid.getHeaderRow(0).getCell("> test").getText();
        String actualFooter = grid.getFooterRow(0).getCell("> test").getText();
        String expected = "> Test";

        Assert.assertEquals(expected, actualHeader);
        Assert.assertEquals(expected, actualFooter);

        design = design.replace("plain-text=\"true\"", "");
        grid = read(design);
        actualHeader = grid.getHeaderRow(0).getCell("> test").getHtml();
        actualFooter = grid.getFooterRow(0).getCell("> test").getHtml();
        expected = "&gt; Test";

        Assert.assertEquals(expected, actualHeader);
        Assert.assertEquals(expected, actualFooter);

        grid = new Grid();
        grid.setColumns("test");
        HeaderRow header = grid.addHeaderRowAt(0);
        FooterRow footer = grid.addFooterRowAt(0);
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        // entities should be encoded when writing back, not interpreted as HTML
        header.getCell("test").setText("&amp; Test");
        footer.getCell("test").setText("&amp; Test");

        Element root = new Element(Tag.valueOf("vaadin7-grid"), "");
        grid.writeDesign(root, new DesignContext());

        Assert.assertEquals("&amp;amp; Test",
                root.getElementsByTag("th").get(0).html());
        Assert.assertEquals("&amp;amp; Test",
                root.getElementsByTag("td").get(0).html());

        header = grid.addHeaderRowAt(0);
        footer = grid.addFooterRowAt(0);

        // entities should not be encoded, this is already given as HTML
        header.getCell("test").setHtml("&amp; Test");
        footer.getCell("test").setHtml("&amp; Test");

        root = new Element(Tag.valueOf("vaadin7-grid"), "");
        grid.writeDesign(root, new DesignContext());

        Assert.assertEquals("&amp; Test",
                root.getElementsByTag("th").get(0).html());
        Assert.assertEquals("&amp; Test",
                root.getElementsByTag("td").get(0).html());

    }
}
