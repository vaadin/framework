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
package com.vaadin.tests.components.grid.basicfeatures;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.SortDirection;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.Grid.SelectionMode;
import com.vaadin.ui.components.grid.GridColumn;
import com.vaadin.ui.components.grid.GridFooter;
import com.vaadin.ui.components.grid.GridHeader;
import com.vaadin.ui.components.grid.GridHeader.HeaderRow;
import com.vaadin.ui.components.grid.SortOrderChangeEvent;
import com.vaadin.ui.components.grid.SortOrderChangeListener;
import com.vaadin.ui.components.grid.renderers.DateRenderer;
import com.vaadin.ui.components.grid.renderers.HtmlRenderer;
import com.vaadin.ui.components.grid.renderers.NumberRenderer;
import com.vaadin.ui.components.grid.sort.Sort;
import com.vaadin.ui.components.grid.sort.SortOrder;

/**
 * Tests the basic features like columns, footers and headers
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicFeatures extends AbstractComponentTest<Grid> {

    private static final int MANUALLY_FORMATTED_COLUMNS = 5;
    public static final int COLUMNS = 12;
    public static final int ROWS = 1000;

    private int columnGroupRows = 0;
    private IndexedContainer ds;

    @Override
    @SuppressWarnings("unchecked")
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

        {
            int col = 0;
            for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; col++) {
                ds.addContainerProperty(getColumnProperty(col), String.class,
                        "");
            }

            ds.addContainerProperty(getColumnProperty(col++), Integer.class,
                    Integer.valueOf(0));
            ds.addContainerProperty(getColumnProperty(col++), Date.class,
                    new Date());
            ds.addContainerProperty(getColumnProperty(col++), String.class, "");

            // Random numbers
            ds.addContainerProperty(getColumnProperty(col++), Integer.class, 0);
            ds.addContainerProperty(getColumnProperty(col++), Integer.class, 0);

        }

        {
            Random rand = new Random();
            rand.setSeed(13334);
            long timestamp = 0;
            for (int row = 0; row < ROWS; row++) {
                Item item = ds.addItem(Integer.valueOf(row));
                int col = 0;
                for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; col++) {
                    item.getItemProperty(getColumnProperty(col)).setValue(
                            "(" + row + ", " + col + ")");
                }
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        Integer.valueOf(row));
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        new Date(timestamp));
                timestamp += 91250000; // a bit over a day, just to get
                                       // variation
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        "<b>" + row + "</b>");

                // Random numbers
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        rand.nextInt());
                // Random between 0 - 5 to test multisorting
                item.getItemProperty(getColumnProperty(col++)).setValue(
                        rand.nextInt(5));
            }
        }

        // Create grid
        Grid grid = new Grid(ds);

        {
            int col = grid.getContainerDatasource().getContainerPropertyIds()
                    .size()
                    - MANUALLY_FORMATTED_COLUMNS;
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer(new DecimalFormat("0,000.00",
                            DecimalFormatSymbols.getInstance(new Locale("fi",
                                    "FI")))));
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new DateRenderer(new SimpleDateFormat("dd.MM.yy HH:mm")));
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new HtmlRenderer());
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer());
            grid.getColumn(getColumnProperty(col++)).setRenderer(
                    new NumberRenderer());
        }

        // Create footer
        GridFooter footer = grid.getFooter();
        footer.appendRow();
        footer.setVisible(false);

        // Add footer values (header values are automatically created)
        for (int col = 0; col < COLUMNS; col++) {
            footer.getRow(0).getCell(getColumnProperty(col))
                    .setText("Footer " + col);
        }

        // Set varying column widths
        for (int col = 0; col < COLUMNS; col++) {
            grid.getColumn(getColumnProperty(col)).setWidth(100 + col * 50);
        }

        grid.addSortOrderChangeListener(new SortOrderChangeListener() {
            @Override
            public void sortOrderChange(SortOrderChangeEvent event) {
                log("Sort order: " + event.getSortOrder());
            }
        });

        grid.setSelectionMode(SelectionMode.NONE);

        createGridActions();

        createColumnActions();

        createHeaderActions();

        createFooterActions();

        createRowActions();

        addHeightActions();

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

        LinkedHashMap<String, SelectionMode> selectionModes = new LinkedHashMap<String, Grid.SelectionMode>();
        selectionModes.put("single", SelectionMode.SINGLE);
        selectionModes.put("multi", SelectionMode.MULTI);
        selectionModes.put("none", SelectionMode.NONE);
        createSelectAction("Selection mode", "State", selectionModes, "none",
                new Command<Grid, Grid.SelectionMode>() {
                    @Override
                    public void execute(Grid grid, SelectionMode selectionMode,
                            Object data) {
                        grid.setSelectionMode(selectionMode);
                    }
                });

        LinkedHashMap<String, List<SortOrder>> sortableProperties = new LinkedHashMap<String, List<SortOrder>>();
        for (Object propertyId : ds.getSortableContainerPropertyIds()) {
            sortableProperties.put(propertyId + ", ASC", Sort.by(propertyId)
                    .build());
            sortableProperties.put(propertyId + ", DESC",
                    Sort.by(propertyId, SortDirection.DESCENDING).build());
        }
        createSelectAction("Sort by column", "State", sortableProperties,
                "Column 9, ascending", new Command<Grid, List<SortOrder>>() {
                    @Override
                    public void execute(Grid grid, List<SortOrder> sortOrder,
                            Object data) {
                        grid.setSortOrder(sortOrder);
                    }
                });
    }

    protected void createHeaderActions() {
        createCategory("Header", null);

        createBooleanAction("Visible", "Header", true,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.getHeader().setVisible(value);
                    }
                });

        LinkedHashMap<String, String> defaultRows = new LinkedHashMap<String, String>();
        defaultRows.put("Top", "Top");
        defaultRows.put("Bottom", "Bottom");
        defaultRows.put("Unset", "Unset");

        createMultiClickAction("Default row", "Header", defaultRows,
                new Command<Grid, String>() {

                    @Override
                    public void execute(Grid grid, String value, Object data) {
                        HeaderRow defaultRow = null;
                        GridHeader header = grid.getHeader();
                        if (value.equals("Top")) {
                            defaultRow = header.getRow(0);
                        } else if (value.equals("Bottom")) {
                            defaultRow = header.getRow(header.getRowCount() - 1);
                        }
                        header.setDefaultRow(defaultRow);
                    }

                }, defaultRows.get("Top"));

        createClickAction("Prepend row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.getHeader().prependRow();
            }

        }, null);
        createClickAction("Append row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.getHeader().appendRow();
            }

        }, null);

        createClickAction("Remove top row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.getHeader().removeRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.getHeader().removeRow(
                                grid.getHeader().getRowCount() - 1);
                    }

                }, null);
    }

    protected void createFooterActions() {
        createCategory("Footer", null);

        createBooleanAction("Visible", "Footer", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.getFooter().setVisible(value);
                    }
                });

        createClickAction("Prepend row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.getFooter().prependRow();
            }

        }, null);
        createClickAction("Append row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.getFooter().appendRow();
            }

        }, null);

        createClickAction("Remove top row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.getFooter().removeRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.getFooter().removeRow(
                                grid.getFooter().getRowCount() - 1);
                    }

                }, null);
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
                                    .removeContainerProperty(
                                            getColumnProperty((Integer) data));
                        }
                    }, null, c);

            createClickAction("Freeze", getColumnProperty(c),
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            grid.setLastFrozenPropertyId(getColumnProperty((Integer) data));
                        }
                    }, null, c);

            createBooleanAction("Sortable", getColumnProperty(c), true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = (new ArrayList(grid
                                    .getContainerDatasource()
                                    .getContainerPropertyIds())
                                    .get((Integer) columnIndex));
                            GridColumn column = grid.getColumn(propertyId);
                            column.setSortable(value);
                        }
                    }, c);

            createCategory("Column " + c + " Width", getColumnProperty(c));

            createClickAction("Auto", "Column " + c + " Width",
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
                createClickAction(w + "px", "Column " + c + " Width",
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
        return "Column " + c;
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
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        Item item = ds.getItem(firstItemId);
                        for (int i = 0; i < COLUMNS; i++) {
                            Property<?> property = item
                                    .getItemProperty(getColumnProperty(i));
                            if (property.getType().equals(String.class)) {
                                ((Property<String>) property)
                                        .setValue("modified: " + i);
                            }
                        }
                    }
                }, null);

        createClickAction("Modify first row (getContainerProperty)",
                "Body rows", new Command<Grid, String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        for (Object containerPropertyId : ds
                                .getContainerPropertyIds()) {
                            Property<?> property = ds.getContainerProperty(
                                    firstItemId, containerPropertyId);
                            if (property.getType().equals(String.class)) {
                                ((Property<String>) property)
                                        .setValue("modified: "
                                                + containerPropertyId);
                            }
                        }
                    }
                }, null);

        createBooleanAction("Select first row", "Body rows", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid grid, Boolean select, Object data) {
                        final Object firstItemId = grid
                                .getContainerDatasource().firstItemId();
                        if (select.booleanValue()) {
                            grid.select(firstItemId);
                        } else {
                            grid.deselect(firstItemId);
                        }
                    }
                });

        createClickAction("Remove all rows", "Body rows",
                new Command<Grid, String>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        ds.removeAllItems();
                    }
                }, null);
    }

    @SuppressWarnings("boxing")
    protected void addHeightActions() {
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

        Command<Grid, String> sizeCommand = new Command<Grid, String>() {
            @Override
            public void execute(Grid grid, String height, Object data) {
                grid.setHeight(height);
            }
        };

        createCategory("Height", "Size");
        // header 20px + scrollbar 16px = 36px baseline
        createClickAction("86px (no drag scroll select)", "Height",
                sizeCommand, "86px");
        createClickAction("96px (drag scroll select limit)", "Height",
                sizeCommand, "96px");
        createClickAction("106px (drag scroll select enabled)", "Height",
                sizeCommand, "106px");
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
