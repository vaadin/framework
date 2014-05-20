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

/**
 * 
 */
package com.vaadin.tests.components.grid;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.components.grid.ColumnGroup;
import com.vaadin.ui.components.grid.ColumnGroupRow;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridColumn;

public class GridColumnGroups extends AbstractTestUI {

    private final int COLUMNS = 4;

    @Override
    protected void setup(VaadinRequest request) {

        // Setup grid
        IndexedContainer ds = new IndexedContainer();
        for (int col = 0; col < COLUMNS; col++) {
            ds.addContainerProperty("Column" + col, String.class, "");
        }
        Grid grid = new Grid(ds);
        addComponent(grid);

        /*-
         * ---------------------------------------------
         * |                   Header 1                | <- Auxiliary row 2 
         * |-------------------------------------------| 
         * |        Header 2     |        Header 3     | <- Auxiliary row 1 
         * |-------------------------------------------|
         * | Column 1 | Column 2 | Column 3 | Column 4 | <- Column headers
         * --------------------------------------------|
         * |    ...   |    ...   |    ...   |    ...   | 
         * |   ...    |    ...   |    ...   |    ...   |
         * --------------------------------------------|
         * | Column 1 | Column 2 | Column 3 | Column 4 | <- Column footers
         * --------------------------------------------|
         * |        Footer 2     |        Footer 3     | <- Auxiliary row 1 
         * --------------------------------------------|
         * |                  Footer 1                 | <- Auxiliary row 2 
         * ---------------------------------------------              
         -*/

        // Set column footers (headers are generated automatically)
        grid.setColumnFootersVisible(true);
        for (Object propertyId : ds.getContainerPropertyIds()) {
            GridColumn column = grid.getColumn(propertyId);
            column.setFooterCaption(String.valueOf(propertyId));
        }

        // First auxiliary row
        ColumnGroupRow auxRow1 = grid.addColumnGroupRow();

        // Using property id to create a column group
        ColumnGroup columns12 = auxRow1.addGroup("Column0", "Column1");
        columns12.setHeaderCaption("Header 2");
        columns12.setFooterCaption("Footer 2");

        // Using grid columns to create a column group
        GridColumn column3 = grid.getColumn("Column2");
        GridColumn column4 = grid.getColumn("Column3");
        ColumnGroup columns34 = auxRow1.addGroup(column3, column4);
        columns34.setHeaderCaption("Header 3");
        columns34.setFooterCaption("Footer 3");

        // Second auxiliary row
        ColumnGroupRow auxRow2 = grid.addColumnGroupRow();

        // Using previous groups to create a column group
        ColumnGroup columns1234 = auxRow2.addGroup(columns12, columns34);
        columns1234.setHeaderCaption("Header 1");
        columns1234.setFooterCaption("Footer 1");

    }

    @Override
    protected String getTestDescription() {
        return "Grid should support headers and footer groups";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12894;
    }

}
