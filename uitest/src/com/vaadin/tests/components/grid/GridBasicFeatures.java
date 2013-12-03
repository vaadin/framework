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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.components.grid.ColumnGroup;
import com.vaadin.ui.components.grid.ColumnGroupRow;
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

    private int columnGroupRows = 0;

    private final int ROWS = 1000;

    @Override
    protected Grid constructComponent() {

        // Build data source
        IndexedContainer ds = new IndexedContainer();

        for (int col = 0; col < COLUMNS; col++) {
            ds.addContainerProperty("Column" + col, String.class, "");
        }

        for (int row = 0; row < ROWS; row++) {
            Item item = ds.addItem(Integer.valueOf(row));
            for (int col = 0; col < COLUMNS; col++) {
                item.getItemProperty("Column" + col).setValue(
                        "(" + row + ", " + col + ")");
            }
        }

        // Create grid
        Grid grid = new Grid(ds);

        // Add footer values (header values are automatically created)
        for (int col = 0; col < COLUMNS; col++) {
            grid.getColumn("Column" + col).setFooterCaption("Footer " + col);
        }

        createColumnActions();

        createHeaderActions();

        createFooterActions();

        createColumnGroupActions();

        return grid;
    }

    protected void createHeaderActions() {
        createCategory("Headers", null);

        createBooleanAction("Visible", "Headers", true,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setColumnHeadersVisible(value);
                    }
                });
    }

    protected void createFooterActions() {
        createCategory("Footers", null);

        createBooleanAction("Visible", "Footers", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setColumnFootersVisible(value);
                    }
                });
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

            createClickAction("Remove", "Column" + c,
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.getContainerDatasource()
                                    .removeContainerProperty("Column" + data);
                        }
                    }, null, c);

            createClickAction("Freeze", "Column" + c,
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.setLastFrozenPropertyId("Column" + data);
                        }
                    }, null, c);

            createCategory("Column" + c + " Width", "Column" + c);

            createClickAction("Auto", "Column" + c + " Width",
                    new Command<Grid, Integer>() {

                        @Override
                        public void execute(Grid grid, Integer value,
                                Object columnIndex) {
                            Object propertyId = (new ArrayList(grid
                                    .getContainerDatasource()
                                    .getContainerPropertyIds())
                                    .get((Integer) columnIndex));
                            GridColumn column = grid.getColumn(propertyId);
                            column.setWidthUndefined();
                        }
                    }, -1, c);

            for (int w = 50; w < 300; w += 50) {
                createClickAction(w + "px", "Column" + c + " Width",
                        new Command<Grid, Integer>() {

                            @Override
                            public void execute(Grid grid, Integer value,
                                    Object columnIndex) {
                                Object propertyId = (new ArrayList(grid
                                        .getContainerDatasource()
                                        .getContainerPropertyIds())
                                        .get((Integer) columnIndex));
                                GridColumn column = grid.getColumn(propertyId);
                                column.setWidth(value);
                            }
                        }, w, c);
            }
        }
    }

    protected void createColumnGroupActions() {
        createCategory("Column groups", null);

        createClickAction("Add group row", "Column groups",
                new Command<Grid, String>() {

                    @Override
                    public void execute(Grid grid, String value, Object data) {
                        final ColumnGroupRow row = grid.addColumnGroupRow();
                        columnGroupRows++;
                        createCategory("Column group row " + columnGroupRows,
                                "Column groups");

                        createBooleanAction("Header Visible",
                                "Column group row " + columnGroupRows, true,
                                new Command<Grid, Boolean>() {

                                    @Override
                                    public void execute(Grid grid,
                                            Boolean value, Object columnIndex) {
                                        row.setHeaderVisible(value);
                                    }
                                }, row);

                        createBooleanAction("Footer Visible",
                                "Column group row " + columnGroupRows, false,
                                new Command<Grid, Boolean>() {

                                    @Override
                                    public void execute(Grid grid,
                                            Boolean value, Object columnIndex) {
                                        row.setFooterVisible(value);
                                    }
                                }, row);

                        for (int i = 0; i < COLUMNS; i++) {
                            final int columnIndex = i;
                            createClickAction("Group Column " + columnIndex
                                    + " & " + (columnIndex + 1),
                                    "Column group row " + columnGroupRows,
                                    new Command<Grid, Integer>() {

                                        @Override
                                        public void execute(Grid c,
                                                Integer value, Object data) {
                                            final ColumnGroup group = row
                                                    .addGroup(
                                                            "Column" + value,
                                                            "Column"
                                                                    + (value + 1));

                                            group.setHeaderCaption("Column "
                                                    + value + " & "
                                                    + (value + 1));

                                            group.setFooterCaption("Column "
                                                    + value + " & "
                                                    + (value + 1));
                                        }
                                    }, i, row);
                        }
                    }
                }, null, null);

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
