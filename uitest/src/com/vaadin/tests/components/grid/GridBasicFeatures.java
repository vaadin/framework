/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.grid;

import java.util.ArrayList;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridColumn;

/**
 * Tests the basic features like columns, footers and headers
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class GridBasicFeatures extends AbstractComponentTest<Grid> {

    private final int COLUMNS = 10;

    @Override
    protected Grid constructComponent() {

        // Build data source
        IndexedContainer ds = new IndexedContainer();

        for (int col = 0; col < COLUMNS; col++) {
            ds.addContainerProperty("Column" + col, String.class, "");
        }

        Grid grid = new Grid(ds);

        // Headers and footers
        for (int col = 0; col < COLUMNS; col++) {
            GridColumn column = grid.getColumn("Column" + col);
            column.setHeaderCaption("Column " + col);
            column.setFooterCaption("Footer " + col);
        }

        createColumnActions();

        return grid;
    }

    protected void createColumnActions() {
        createCategory("Columns", null);

        for (int c = 0; c < COLUMNS; c++) {
            createCategory("Column" + c, "Columns");

            createBooleanAction("Visible", "Column" + c, true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = (new ArrayList(grid
                                    .getContainerDatasource()
                                    .getContainerPropertyIds())
                                    .get((Integer) columnIndex));
                            GridColumn column = grid.getColumn(propertyId);
                            column.setVisible(!column.isVisible());
                        }
                    }, c);

            createBooleanAction("Footer", "Column" + c, true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = (new ArrayList(grid
                                    .getContainerDatasource()
                                    .getContainerPropertyIds())
                                    .get((Integer) columnIndex));
                            GridColumn column = grid.getColumn(propertyId);
                            String footer = column.getFooterCaption();
                            if (footer == null) {
                                column.setFooterCaption("Footer " + columnIndex);
                            } else {
                                column.setFooterCaption(null);
                            }
                        }
                    }, c);

            createBooleanAction("Header", "Column" + c, true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = (new ArrayList(grid
                                    .getContainerDatasource()
                                    .getContainerPropertyIds())
                                    .get((Integer) columnIndex));
                            GridColumn column = grid.getColumn(propertyId);
                            String header = column.getHeaderCaption();
                            if (header == null) {
                                column.setHeaderCaption("Column " + columnIndex);
                            } else {
                                column.setHeaderCaption(null);
                            }
                        }
                    }, c);

            createClickAction("Remove", "Column" + c,
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.getContainerDatasource()
                                    .removeContainerProperty("Column" + data);
                        }
                    }, null, c);

        }

    }

    @Override
    protected Integer getTicketNumber() {
        return 12829;
    }

    @Override
    protected Class<Grid> getTestClass() {
        return Grid.class;
    }

}
