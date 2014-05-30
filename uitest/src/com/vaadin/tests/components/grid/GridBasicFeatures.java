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
package com.vaadin.tests.components.grid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.components.grid.ColumnGroup;
import com.vaadin.ui.components.grid.ColumnGroupRow;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridColumn;

/**
 * Tests the basic features like columns, footers and headers
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridBasicFeatures extends AbstractComponentTest<Grid> {

    private final int COLUMNS = 10;

    private int columnGroupRows = 0;

    private final int ROWS = 1000;

    private IndexedContainer ds;

    @Override
    protected Grid constructComponent() {

        // Build data source
        ds = new IndexedContainer() {
            @Override
            public List<Object> getItemIds(int startIndex, int numberOfIds) {
                log("Requested items " + startIndex + " - "
                        + (startIndex + numberOfIds));
                return super.getItemIds(startIndex, numberOfIds);
            }
        };

        for (int col = 0; col < COLUMNS; col++) {
            ds.addContainerProperty(getColumnProperty(col), String.class, "");
        }

        for (int row = 0; row < ROWS; row++) {
            Item item = ds.addItem(Integer.valueOf(row));
            for (int col = 0; col < COLUMNS; col++) {
                item.getItemProperty(getColumnProperty(col)).setValue(
                        "(" + row + ", " + col + ")");
            }
        }

        // Create grid
        Grid grid = new Grid(ds);

        // Add footer values (header values are automatically created)
        for (int col = 0; col < COLUMNS; col++) {
            grid.getColumn(getColumnProperty(col)).setFooterCaption(
                    "Footer " + col);
        }

        // Set varying column widths
        for (int col = 0; col < COLUMNS; col++) {
            grid.getColumn("Column" + col).setWidth(100 + col * 50);
        }

        createGridActions();

        createColumnActions();

        createHeaderActions();

        createFooterActions();

        createColumnGroupActions();

        createRowActions();

        addHeightByRowActions();

        return grid;
    }

    protected void createGridActions() {
        LinkedHashMap<String, String> primaryStyleNames = new LinkedHashMap<String, String>();
        primaryStyleNames.put("v-grid", "v-grid");
        primaryStyleNames.put("v-escalator", "v-escalator");
        primaryStyleNames.put("my-grid", "my-grid");

        createMultiClickAction("Primary style name", "State",
                primaryStyleNames, new Command<Grid, String>() {

                    @Override
                    public void execute(Grid grid, String value, Object data) {
                        grid.setPrimaryStyleName(value);

                    }
                }, primaryStyleNames.get("v-grid"));
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
            createCategory(getColumnProperty(c), "Columns");

            createBooleanAction("Visible", getColumnProperty(c), true,
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

            createClickAction("Remove", getColumnProperty(c),
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.getContainerDatasource()
                                    .removeContainerProperty("Column" + data);
                        }
                    }, null, c);

            createClickAction("Freeze", getColumnProperty(c),
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.setLastFrozenPropertyId("Column" + data);
                        }
                    }, null, c);

            createCategory("Column" + c + " Width", getColumnProperty(c));

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

    private static String getColumnProperty(int c) {
        return "Column" + c;
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

    protected void createRowActions() {
        createCategory("Body rows", null);

        createClickAction("Add first row", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Item item = ds.addItemAt(0, new Object());
                        for (int i = 0; i < COLUMNS; i++) {
                            item.getItemProperty(getColumnProperty(i))
                                    .setValue("newcell: " + i);
                        }
                    }
                }, null);

        createClickAction("Remove first row", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        ds.removeItem(firstItemId);
                    }
                }, null);

        createClickAction("Modify first row (getItemProperty)", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        Item item = ds.getItem(firstItemId);
                        for (int i = 0; i < COLUMNS; i++) {
                            item.getItemProperty(getColumnProperty(i))
                                    .setValue("modified: " + i);
                        }
                    }
                }, null);

        createClickAction("Modify first row (getContainerProperty)",
                "Body rows", new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        for (Object containerPropertyId : ds
                                .getContainerPropertyIds()) {
                            ds.getContainerProperty(firstItemId,
                                    containerPropertyId).setValue(
                                    "modified: " + containerPropertyId);
                        }
                    }
                }, null);
    }

    @SuppressWarnings("boxing")
    protected void addHeightByRowActions() {
        createCategory("Height by Rows", "Size");

        createBooleanAction("HeightMode Row", "Size", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid c, Boolean heightModeByRows,
                            Object data) {
                        c.setHeightMode(heightModeByRows ? HeightMode.ROW
                                : HeightMode.CSS);
                    }
                }, null);

        addActionForHeightByRows(1d / 3d);
        addActionForHeightByRows(2d / 3d);

        for (double i = 1; i < 5; i++) {
            addActionForHeightByRows(i);
            addActionForHeightByRows(i + 1d / 3d);
            addActionForHeightByRows(i + 2d / 3d);
        }
    }

    private void addActionForHeightByRows(final Double i) {
        DecimalFormat df = new DecimalFormat("0.00");
        createClickAction(df.format(i) + " rows", "Height by Rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.setHeightByRows(i);
                    }
                }, null);
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
