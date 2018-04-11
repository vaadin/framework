package com.vaadin.v7.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Grid;

public class GridInlineDataDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSimpleInlineData() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" + "   <col sortable property-id='Col1' />"
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Foo</tr>" //
                + "<tr><td>Bar</tr>" //
                + "<tr><td>Baz</tr>" //
                + "</tbody>" //
                + "</table></vaadin7-grid>";

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
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" + "   <col sortable property-id='Col1' />"
                + "   <col sortable property-id='Col2' />"
                + "   <col sortable property-id='Col3' />" //
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Foo<td>Bar<td>Baz</tr>" //
                + "<tr><td>My<td>Summer<td>Car</tr>" //
                + "</tbody>" //
                + "</table></vaadin7-grid>";

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
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" + "   <col sortable property-id='Col2' />"
                + "   <col sortable property-id='Col3' />"
                + "   <col sortable property-id='Col1' />" //
                + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "<tr><td>Bar<td>Baz<td>Foo</tr>" //
                + "<tr><td>Summer<td>Car<td>My</tr>" //
                + "</tbody>" //
                + "</table></vaadin7-grid>";

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

    @Test
    public void testHtmlEntities() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" + "   <col property-id='test' />" + "</colgroup>" //
                + "<thead />" // No headers read or written
                + "<tbody>" //
                + "  <tr><td>&amp;Test</tr></td>" + "</tbody>"
                + "</table></vaadin7-grid>";

        Grid read = read(design);
        Container cds = read.getContainerDataSource();
        assertEquals("&amp;Test",
                cds.getItem(cds.getItemIds().iterator().next())
                        .getItemProperty("test").getValue());
    }
}
