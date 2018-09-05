package com.vaadin.v7.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.v7.ui.Grid;

public class GridColumnDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSimpleGridColumns() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>"
                + "   <col sortable width='100' property-id='Column1'>"
                + "   <col sortable=false max-width='200' expand='2' property-id='Column2'>"
                + "   <col sortable editable=false resizable=false min-width='15' expand='1' property-id='Column3'>"
                + "   <col sortable hidable hiding-toggle-caption='col 4' property-id='Column4'>"
                + "   <col sortable hidden property-id='Column5'>"
                + "</colgroup>" //
                + "<thead />" //
                + "</table></vaadin7-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class).setWidth(100);
        grid.addColumn("Column2", String.class).setMaximumWidth(200)
                .setExpandRatio(2).setSortable(false);
        grid.addColumn("Column3", String.class).setMinimumWidth(15)
                .setExpandRatio(1).setEditable(false).setResizable(false);
        grid.addColumn("Column4", String.class).setHidable(true)
                .setHidingToggleCaption("col 4").setResizable(true);
        grid.addColumn("Column5", String.class).setHidden(true);

        // Remove the default header
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        // Use the read grid component to do another pass on write.
        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testReadColumnsWithoutPropertyId() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true width='100' property-id='Column1'>"
                + "   <col sortable=true max-width='200' expand='2'>" // property-id="property-1"
                + "   <col sortable=true min-width='15' expand='1' property-id='Column3'>"
                + "   <col sortable=true hidden=true hidable=true hiding-toggle-caption='col 4'>" // property-id="property-3"
                + "</colgroup>" //
                + "</table></vaadin7-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class).setWidth(100);
        grid.addColumn("property-1", String.class).setMaximumWidth(200)
                .setExpandRatio(2);
        grid.addColumn("Column3", String.class).setMinimumWidth(15)
                .setExpandRatio(1);
        grid.addColumn("property-3", String.class).setHidable(true)
                .setHidden(true).setHidingToggleCaption("col 4");

        testRead(design, grid);
    }

    @Test
    public void testReadEmptyExpand() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" + "   <col sortable=true expand />"
                + "</colgroup>" //
                + "</table></vaadin7-grid>";

        Grid grid = new Grid();
        grid.addColumn("property-0", String.class).setExpandRatio(1);

        testRead(design, grid);
    }

    @Test
    public void testReadColumnWithNoAttributes() {
        String design = "<vaadin7-grid><table>"//
                + "<colgroup>" //
                + "   <col />" //
                + "</colgroup>" //
                + "</table></vaadin7-grid>";

        Grid grid = new Grid();
        grid.addColumn("property-0", String.class);

        testRead(design, grid);
    }
}
