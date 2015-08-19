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

public class GridColumnDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testSimpleGridColumns() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true width='100' property-id='Column1'>"
                + "   <col sortable=false max-width='200' expand='2' property-id='Column2'>"
                + "   <col sortable=true editable=false min-width='15' expand='1' property-id='Column3'>"
                + "   <col sortable=true hidable=true hiding-toggle-caption='col 4' property-id='Column4'>"
                + "   <col sortable=true hidden=true property-id='Column5'>"
                + "</colgroup>" //
                + "<thead />" //
                + "</table></v-grid>";
        Grid grid = new Grid();
        grid.addColumn("Column1", String.class).setWidth(100);
        grid.addColumn("Column2", String.class).setMaximumWidth(200)
                .setExpandRatio(2).setSortable(false);
        grid.addColumn("Column3", String.class).setMinimumWidth(15)
                .setExpandRatio(1).setEditable(false);
        grid.addColumn("Column4", String.class).setHidable(true)
                .setHidingToggleCaption("col 4");
        grid.addColumn("Column5", String.class).setHidden(true);

        // Remove the default header
        grid.removeHeaderRow(grid.getDefaultHeaderRow());

        // Use the read grid component to do another pass on write.
        testRead(design, grid, true);
        testWrite(design, grid);
    }

    @Test
    public void testReadColumnsWithoutPropertyId() {
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true width='100' property-id='Column1'>"
                + "   <col sortable=true max-width='200' expand='2'>" // property-id="property-1"
                + "   <col sortable=true min-width='15' expand='1' property-id='Column3'>"
                + "   <col sortable=true hidden=true hidable=true hiding-toggle-caption='col 4'>" // property-id="property-3"
                + "</colgroup>" //
                + "</table></v-grid>";
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
        String design = "<v-grid><table>"//
                + "<colgroup>"
                + "   <col sortable=true expand />"
                + "</colgroup>" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("property-0", String.class).setExpandRatio(1);

        testRead(design, grid);
    }

    @Test
    public void testReadColumnWithNoAttributes() {
        String design = "<v-grid><table>"//
                + "<colgroup>" //
                + "   <col />" //
                + "</colgroup>" //
                + "</table></v-grid>";

        Grid grid = new Grid();
        grid.addColumn("property-0", String.class);

        testRead(design, grid);
    }
}
