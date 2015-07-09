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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.GridStaticCellType;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.ColumnReorderEvent;
import com.vaadin.ui.Grid.ColumnReorderListener;
import com.vaadin.ui.Grid.ColumnVisibilityChangeEvent;
import com.vaadin.ui.Grid.ColumnVisibilityChangeListener;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Grid.RowStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

/**
 * Tests the basic features like columns, footers and headers
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicFeatures extends AbstractComponentTest<Grid> {

    public static final String ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4 = "Row numbers for 3/4";
    public static final String ROW_STYLE_GENERATOR_NONE = "None";
    public static final String ROW_STYLE_GENERATOR_ROW_NUMBERS = "Row numbers";
    public static final String ROW_STYLE_GENERATOR_EMPTY = "Empty string";
    public static final String ROW_STYLE_GENERATOR_NULL = "Null";
    public static final String CELL_STYLE_GENERATOR_NONE = "None";
    public static final String CELL_STYLE_GENERATOR_PROPERTY_TO_STRING = "Property to string";
    public static final String CELL_STYLE_GENERATOR_SPECIAL = "Special for 1/4 Column 1";
    public static final String CELL_STYLE_GENERATOR_EMPTY = "Empty string";
    public static final String CELL_STYLE_GENERATOR_NULL = "Null";
    private static final int MANUALLY_FORMATTED_COLUMNS = 5;
    public static final int COLUMNS = 12;
    public static final int EDITABLE_COLUMNS = COLUMNS - 1;
    public static final int ROWS = 1000;

    private int containerDelay = 0;

    private boolean singleSelectAllowDeselect = true;

    private IndexedContainer ds;
    private Grid grid;
    private SelectionListener selectionListener = new SelectionListener() {

        @Override
        public void select(SelectionEvent event) {
            Iterator<Object> iter = event.getAdded().iterator();
            Object addedRow = (iter.hasNext() ? iter.next() : "none");
            iter = event.getRemoved().iterator();
            Object removedRow = (iter.hasNext() ? iter.next() : "none");
            log("SelectionEvent: Added " + addedRow + ", Removed " + removedRow);
        }
    };

    private ItemClickListener itemClickListener = new ItemClickListener() {

        @Override
        public void itemClick(ItemClickEvent event) {
            log("Item " + (event.isDoubleClick() ? "double " : "")
                    + "click on " + event.getPropertyId() + ", item "
                    + event.getItemId());
        }
    };

    private ColumnReorderListener columnReorderListener = new ColumnReorderListener() {

        @Override
        public void columnReorder(ColumnReorderEvent event) {
            log("Columns reordered, userOriginated: "
                    + event.isUserOriginated());
        }
    };

    private ColumnVisibilityChangeListener columnVisibilityListener = new ColumnVisibilityChangeListener() {
        @Override
        public void columnVisibilityChanged(ColumnVisibilityChangeEvent event) {
            log("Visibility changed: "//
                    + "propertyId: " + event.getColumn().getPropertyId() //
                    + ", isHidden: " + event.getColumn().isHidden() //
                    + ", userOriginated: " + event.isUserOriginated());
        }
    };

    private Panel detailsPanel;

    private final DetailsGenerator detailedDetailsGenerator = new DetailsGenerator() {
        @Override
        public Component getDetails(final RowReference rowReference) {
            CssLayout cssLayout = new CssLayout();
            cssLayout.setHeight("200px");
            cssLayout.setWidth("100%");

            Item item = rowReference.getItem();
            for (Object propertyId : item.getItemPropertyIds()) {
                Property<?> prop = item.getItemProperty(propertyId);
                String string = prop.getValue().toString();
                cssLayout.addComponent(new Label(string));
            }

            final int rowIndex = grid.getContainerDataSource().indexOfId(
                    rowReference.getItemId());
            ClickListener clickListener = new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    Notification.show("You clicked on the "
                            + "button in the details for " + "row " + rowIndex);
                }
            };
            cssLayout.addComponent(new Button("Press me", clickListener));
            return cssLayout;
        }
    };

    private final DetailsGenerator watchingDetailsGenerator = new DetailsGenerator() {
        private int id = 0;

        @Override
        public Component getDetails(RowReference rowReference) {
            return new Label("You are watching item id "
                    + rowReference.getItemId() + " (" + (id++) + ")");
        }
    };

    private final DetailsGenerator hierarchicalDetailsGenerator = new DetailsGenerator() {
        @Override
        public Component getDetails(RowReference rowReference) {
            detailsPanel = new Panel();
            detailsPanel.setContent(new Label("One"));
            return detailsPanel;
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    protected Grid constructComponent() {

        // Build data source
        ds = new IndexedContainer() {
            @Override
            public List<Object> getItemIds(int startIndex, int numberOfIds) {
                log("Requested items " + startIndex + " - "
                        + (startIndex + numberOfIds));
                if (containerDelay > 0) {
                    try {
                        Thread.sleep(containerDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
                item.getItemProperty(getColumnProperty(1)).setReadOnly(true);

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
            int col = grid.getContainerDataSource().getContainerPropertyIds()
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
        grid.appendFooterRow();
        grid.setFooterVisible(false);

        // Add footer values (header values are automatically created)
        for (int col = 0; col < COLUMNS; col++) {
            grid.getFooterRow(0).getCell(getColumnProperty(col))
                    .setText("Footer " + col);
        }

        // Set varying column widths
        for (int col = 0; col < COLUMNS; col++) {
            Column column = grid.getColumn(getColumnProperty(col));
            column.setWidth(100 + col * 50);
            column.setHidable(isColumnHidableByDefault(col));
        }

        grid.addSortListener(new SortListener() {
            @Override
            public void sort(SortEvent event) {

                log("SortOrderChangeEvent: isUserOriginated? "
                        + event.isUserOriginated());
            }
        });

        grid.setSelectionMode(SelectionMode.NONE);

        grid.getColumn(getColumnProperty(2)).getEditorField().setReadOnly(true);
        grid.getColumn(getColumnProperty(3)).setEditable(false);

        createGridActions();

        createColumnActions();

        createPropertyActions();

        createHeaderActions();

        createFooterActions();

        createRowActions();

        createEditorActions();

        addHeightActions();

        addFilterActions();

        addInternalActions();

        createDetailsActions();

        this.grid = grid;
        return grid;
    }

    protected boolean isColumnHidableByDefault(int col) {
        return false;
    }

    protected boolean isColumnHiddenByDefault(int col) {
        return false;
    }

    private void addInternalActions() {
        createClickAction("Update column order without updating client",
                "Internals", new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid grid, Void value, Object data) {
                        List<Column> columns = grid.getColumns();
                        grid.setColumnOrder(columns.get(1).getPropertyId(),
                                columns.get(0).getPropertyId());
                        grid.getUI().getConnectorTracker().markClean(grid);
                    }
                }, null);
    }

    private void addFilterActions() {
        createClickAction("Column 1 starts with \"(23\"", "Filter",
                new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid grid, Void value, Object data) {
                        ds.addContainerFilter(new Filter() {

                            @Override
                            public boolean passesFilter(Object itemId, Item item)
                                    throws UnsupportedOperationException {
                                return item.getItemProperty("Column 1")
                                        .getValue().toString()
                                        .startsWith("(23");
                            }

                            @Override
                            public boolean appliesToProperty(Object propertyId) {
                                return propertyId.equals("Column 1");
                            }
                        });
                    }
                }, null);

        createClickAction("Add impassable filter", "Filter",
                new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid c, Void value, Object data) {
                        ds.addContainerFilter(new Filter() {
                            @Override
                            public boolean passesFilter(Object itemId, Item item)
                                    throws UnsupportedOperationException {
                                return false;
                            }

                            @Override
                            public boolean appliesToProperty(Object propertyId) {
                                return true;
                            }
                        });
                    }
                }, null);
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
                        if (selectionMode == SelectionMode.SINGLE) {
                            grid.addSelectionListener(selectionListener);

                            ((SelectionModel.Single) grid.getSelectionModel())
                                    .setDeselectAllowed(singleSelectAllowDeselect);
                        } else {
                            grid.removeSelectionListener(selectionListener);
                        }
                    }
                });

        LinkedHashMap<String, Integer> selectionLimits = new LinkedHashMap<String, Integer>();
        selectionLimits.put("2", Integer.valueOf(2));
        selectionLimits.put("1000", Integer.valueOf(1000));
        selectionLimits.put("Integer.MAX_VALUE",
                Integer.valueOf(Integer.MAX_VALUE));
        createSelectAction("Selection limit", "State", selectionLimits, "1000",
                new Command<Grid, Integer>() {
                    @Override
                    public void execute(Grid grid, Integer limit, Object data) {
                        if (!(grid.getSelectionModel() instanceof MultiSelectionModel)) {
                            grid.setSelectionMode(SelectionMode.MULTI);
                        }

                        ((MultiSelectionModel) grid.getSelectionModel())
                                .setSelectionLimit(limit.intValue());
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

        createBooleanAction("Reverse Grid Columns", "State", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        List<Object> ids = new ArrayList<Object>();
                        ids.addAll(ds.getContainerPropertyIds());
                        if (!value) {
                            c.setColumnOrder(ids.toArray());
                        } else {
                            Object[] idsArray = new Object[ids.size()];
                            for (int i = 0; i < ids.size(); ++i) {
                                idsArray[i] = ids.get((ids.size() - 1) - i);
                            }
                            c.setColumnOrder(idsArray);
                        }
                    }
                });

        LinkedHashMap<String, CellStyleGenerator> cellStyleGenerators = new LinkedHashMap<String, CellStyleGenerator>();
        LinkedHashMap<String, RowStyleGenerator> rowStyleGenerators = new LinkedHashMap<String, RowStyleGenerator>();
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_NONE, null);
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_ROW_NUMBERS,
                new RowStyleGenerator() {
                    @Override
                    public String getStyle(RowReference rowReference) {
                        return "row" + rowReference.getItemId();
                    }
                });
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_ROW_NUMBERS_FOR_3_OF_4,
                new RowStyleGenerator() {
                    @Override
                    public String getStyle(RowReference rowReference) {
                        int rowIndex = ((Integer) rowReference.getItemId())
                                .intValue();

                        if (rowIndex % 4 == 0) {
                            return null;
                        } else {
                            return "row" + rowReference.getItemId();
                        }
                    }
                });
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_EMPTY,
                new RowStyleGenerator() {

                    @Override
                    public String getStyle(RowReference rowReference) {
                        return "";
                    }
                });
        rowStyleGenerators.put(ROW_STYLE_GENERATOR_NULL,
                new RowStyleGenerator() {

                    @Override
                    public String getStyle(RowReference rowReference) {
                        return null;
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_NONE, null);
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_PROPERTY_TO_STRING,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        return cellReference.getPropertyId().toString()
                                .replace(' ', '-');
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_SPECIAL,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        int rowIndex = ((Integer) cellReference.getItemId())
                                .intValue();
                        Object propertyId = cellReference.getPropertyId();
                        if (rowIndex % 4 == 1) {
                            return null;
                        } else if (rowIndex % 4 == 3
                                && "Column 1".equals(propertyId)) {
                            return null;
                        }
                        return propertyId.toString().replace(' ', '_');
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_EMPTY,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        return "";
                    }
                });
        cellStyleGenerators.put(CELL_STYLE_GENERATOR_NULL,
                new CellStyleGenerator() {
                    @Override
                    public String getStyle(CellReference cellReference) {
                        return null;
                    }
                });

        createSelectAction("Row style generator", "State", rowStyleGenerators,
                CELL_STYLE_GENERATOR_NONE,
                new Command<Grid, RowStyleGenerator>() {
                    @Override
                    public void execute(Grid grid, RowStyleGenerator generator,
                            Object data) {
                        grid.setRowStyleGenerator(generator);
                    }
                });

        createSelectAction("Cell style generator", "State",
                cellStyleGenerators, CELL_STYLE_GENERATOR_NONE,
                new Command<Grid, CellStyleGenerator>() {
                    @Override
                    public void execute(Grid grid,
                            CellStyleGenerator generator, Object data) {
                        grid.setCellStyleGenerator(generator);
                    }
                });

        LinkedHashMap<String, Integer> frozenOptions = new LinkedHashMap<String, Integer>();
        for (int i = -1; i <= COLUMNS; i++) {
            frozenOptions.put(String.valueOf(i), Integer.valueOf(i));
        }
        /*
         * This line below is a workaround for a FF24 bug regarding submenu
         * handling - it makes the sub menu wider.
         */
        frozenOptions.put("-1 for unfreezing selection column", -1);
        createSelectAction("Frozen column count", "State", frozenOptions, "0",
                new Command<Grid, Integer>() {
                    @Override
                    public void execute(Grid c, Integer value, Object data) {
                        c.setFrozenColumnCount(value.intValue());
                    }
                });

        LinkedHashMap<String, Integer> containerDelayValues = new LinkedHashMap<String, Integer>();
        for (int delay : new int[] { 0, 500, 2000, 10000 }) {
            containerDelayValues.put(String.valueOf(delay),
                    Integer.valueOf(delay));
        }

        createSelectAction("Container delay", "State", containerDelayValues,
                "0", new Command<Grid, Integer>() {
                    @Override
                    public void execute(Grid grid, Integer delay, Object data) {
                        containerDelay = delay.intValue();
                    }
                });

        createBooleanAction("ItemClickListener", "State", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        if (!value) {
                            c.removeItemClickListener(itemClickListener);
                        } else {
                            c.addItemClickListener(itemClickListener);
                        }
                    }

                });
        createBooleanAction("ColumnReorderListener", "State", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        if (value) {
                            grid.addColumnReorderListener(columnReorderListener);
                        } else {
                            grid.removeColumnReorderListener(columnReorderListener);
                        }
                    }
                });
        createBooleanAction("ColumnVisibilityChangeListener", "State", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        if (value) {
                            grid.addColumnVisibilityChangeListener(columnVisibilityListener);
                        } else {
                            grid.removeColumnVisibilityChangeListener(columnVisibilityListener);
                        }
                    }
                });

        createBooleanAction("Single select allow deselect", "State",
                singleSelectAllowDeselect, new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        singleSelectAllowDeselect = value.booleanValue();

                        SelectionModel model = c.getSelectionModel();
                        if (model instanceof SelectionModel.Single) {
                            ((SelectionModel.Single) model)
                                    .setDeselectAllowed(singleSelectAllowDeselect);
                        }
                    }
                });
        createBooleanAction("Column Reordering Allowed", "State", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        c.setColumnReorderingAllowed(value);
                    }
                });

        createClickAction("Select all", "State", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                SelectionModel selectionModel = c.getSelectionModel();
                if (selectionModel instanceof SelectionModel.Multi) {
                    ((SelectionModel.Multi) selectionModel).selectAll();
                }
            }
        }, null);

        createClickAction("Select none", "State", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                SelectionModel selectionModel = c.getSelectionModel();
                if (selectionModel instanceof SelectionModel.Multi) {
                    ((SelectionModel.Multi) selectionModel).deselectAll();
                }
            }
        }, null);
    }

    protected void createHeaderActions() {
        createCategory("Header", null);

        createBooleanAction("Visible", "Header", true,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setHeaderVisible(value);
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
                        if (value.equals("Top")) {
                            defaultRow = grid.getHeaderRow(0);
                        } else if (value.equals("Bottom")) {
                            defaultRow = grid.getHeaderRow(grid
                                    .getHeaderRowCount() - 1);
                        }
                        grid.setDefaultHeaderRow(defaultRow);
                    }

                }, defaultRows.get("Top"));

        createClickAction("Prepend row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.prependHeaderRow();
            }

        }, null);
        createClickAction("Append row", "Header", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.appendHeaderRow();
            }

        }, null);

        createClickAction("Remove top row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeHeaderRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Header",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeHeaderRow(grid.getHeaderRowCount() - 1);
                    }

                }, null);
    }

    protected void createFooterActions() {
        createCategory("Footer", null);

        createBooleanAction("Visible", "Footer", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid grid, Boolean value, Object data) {
                        grid.setFooterVisible(value);
                    }
                });

        createClickAction("Prepend row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.prependFooterRow();
            }

        }, null);
        createClickAction("Append row", "Footer", new Command<Grid, Object>() {

            @Override
            public void execute(Grid grid, Object value, Object data) {
                grid.appendFooterRow();
            }

        }, null);

        createClickAction("Remove top row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeFooterRow(0);
                    }

                }, null);
        createClickAction("Remove bottom row", "Footer",
                new Command<Grid, Object>() {

                    @Override
                    public void execute(Grid grid, Object value, Object data) {
                        grid.removeFooterRow(grid.getFooterRowCount() - 1);
                    }

                }, null);
    }

    @SuppressWarnings("boxing")
    protected void createColumnActions() {
        createCategory("Columns", null);
        for (int c = 0; c < COLUMNS; c++) {
            final int index = c;
            createCategory(getColumnProperty(c), "Columns");

            createClickAction("Add / Remove", getColumnProperty(c),
                    new Command<Grid, String>() {

                        boolean wasHidable;
                        boolean wasHidden;
                        String wasColumnHidingToggleCaption;

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            String columnProperty = getColumnProperty((Integer) data);
                            Column column = grid.getColumn(columnProperty);
                            if (column == null) {
                                column = grid.addColumn(columnProperty);
                                column.setHidable(wasHidable);
                                column.setHidden(wasHidden);
                                column.setHidingToggleCaption(wasColumnHidingToggleCaption);
                            } else {
                                wasHidable = column.isHidable();
                                wasHidden = column.isHidden();
                                wasColumnHidingToggleCaption = column
                                        .getHidingToggleCaption();
                                grid.removeColumn(columnProperty);
                            }
                        }
                    }, null, c);
            createClickAction("Move left", getColumnProperty(c),
                    new Command<Grid, String>() {

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            final String columnProperty = getColumnProperty((Integer) data);
                            List<Column> cols = grid.getColumns();
                            List<Object> reordered = new ArrayList<Object>();
                            boolean addAsLast = false;
                            for (int i = 0; i < cols.size(); i++) {
                                Column col = cols.get(i);
                                if (col.getPropertyId().equals(columnProperty)) {
                                    if (i == 0) {
                                        addAsLast = true;
                                    } else {
                                        reordered.add(i - 1, columnProperty);
                                    }
                                } else {
                                    reordered.add(col.getPropertyId());
                                }
                            }
                            if (addAsLast) {
                                reordered.add(columnProperty);
                            }
                            grid.setColumnOrder(reordered.toArray());
                        }
                    }, null, c);

            createBooleanAction("Sortable", getColumnProperty(c), true,
                    new Command<Grid, Boolean>() {

                        @Override
                        public void execute(Grid grid, Boolean value,
                                Object columnIndex) {
                            Object propertyId = getColumnProperty((Integer) columnIndex);
                            Column column = grid.getColumn(propertyId);
                            column.setSortable(value);
                        }
                    }, c);

            createBooleanAction("Hidable", getColumnProperty(c),
                    isColumnHidableByDefault(c), new Command<Grid, Boolean>() {
                        @Override
                        public void execute(Grid c, Boolean hidable,
                                Object propertyId) {
                            grid.getColumn(propertyId).setHidable(hidable);
                        }
                    }, getColumnProperty(c));

            createBooleanAction("Hidden", getColumnProperty(c),
                    isColumnHiddenByDefault(c), new Command<Grid, Boolean>() {
                        @Override
                        public void execute(Grid c, Boolean hidden,
                                Object propertyId) {
                            grid.getColumn(propertyId).setHidden(hidden);
                        }
                    }, getColumnProperty(c));
            createClickAction("Change hiding toggle caption",
                    getColumnProperty(c), new Command<Grid, String>() {
                        int count = 0;

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            final String columnProperty = getColumnProperty((Integer) data);
                            grid.getColumn(columnProperty)
                                    .setHidingToggleCaption(
                                            columnProperty + " caption "
                                                    + count++);
                        }
                    }, null, c);

            createClickAction("Change header caption", getColumnProperty(c),
                    new Command<Grid, String>() {
                        int count = 0;

                        @Override
                        public void execute(Grid grid, String value, Object data) {
                            final String columnProperty = getColumnProperty((Integer) data);
                            grid.getColumn(columnProperty).setHeaderCaption(
                                    columnProperty + " header " + count++);
                        }
                    }, null, c);

            createCategory("Column " + c + " Width", getColumnProperty(c));

            createClickAction("Auto", "Column " + c + " Width",
                    new Command<Grid, Integer>() {

                        @Override
                        public void execute(Grid grid, Integer value,
                                Object columnIndex) {
                            Object propertyId = getColumnProperty((Integer) columnIndex);
                            Column column = grid.getColumn(propertyId);
                            column.setWidthUndefined();
                        }
                    }, -1, c);

            createClickAction("25.5px", "Column " + c + " Width",
                    new Command<Grid, Void>() {
                        @Override
                        public void execute(Grid grid, Void value,
                                Object columnIndex) {
                            grid.getColumns().get((Integer) columnIndex)
                                    .setWidth(25.5);
                        }
                    }, null, c);

            for (int w = 50; w < 300; w += 50) {
                createClickAction(w + "px", "Column " + c + " Width",
                        new Command<Grid, Integer>() {

                            @Override
                            public void execute(Grid grid, Integer value,
                                    Object columnIndex) {
                                Object propertyId = getColumnProperty((Integer) columnIndex);
                                Column column = grid.getColumn(propertyId);
                                column.setWidth(value);
                            }
                        }, w, c);
            }

            LinkedHashMap<String, GridStaticCellType> defaultRows = new LinkedHashMap<String, GridStaticCellType>();
            defaultRows.put("Text Header", GridStaticCellType.TEXT);
            defaultRows.put("Html Header ", GridStaticCellType.HTML);
            defaultRows.put("Widget Header", GridStaticCellType.WIDGET);

            createMultiClickAction("Header Type", getColumnProperty(c),
                    defaultRows, new Command<Grid, GridStaticCellType>() {

                        @Override
                        public void execute(Grid grid,
                                GridStaticCellType value, Object columnIndex) {
                            final Object propertyId = getColumnProperty((Integer) columnIndex);
                            final HeaderCell cell = grid.getDefaultHeaderRow()
                                    .getCell(propertyId);
                            switch (value) {
                            case TEXT:
                                cell.setText("Text Header");
                                break;
                            case HTML:
                                cell.setHtml("HTML Header");
                                break;
                            case WIDGET:
                                cell.setComponent(new Button("Button Header",
                                        new ClickListener() {

                                            @Override
                                            public void buttonClick(
                                                    ClickEvent event) {
                                                log("Button clicked!");
                                            }
                                        }));
                            default:
                                break;
                            }
                        }

                    }, c);

            defaultRows = new LinkedHashMap<String, GridStaticCellType>();
            defaultRows.put("Text Footer", GridStaticCellType.TEXT);
            defaultRows.put("Html Footer", GridStaticCellType.HTML);
            defaultRows.put("Widget Footer", GridStaticCellType.WIDGET);

            createMultiClickAction("Footer Type", getColumnProperty(c),
                    defaultRows, new Command<Grid, GridStaticCellType>() {

                        @Override
                        public void execute(Grid grid,
                                GridStaticCellType value, Object columnIndex) {
                            final Object propertyId = getColumnProperty((Integer) columnIndex);
                            final FooterCell cell = grid.getFooterRow(0)
                                    .getCell(propertyId);
                            switch (value) {
                            case TEXT:
                                cell.setText("Text Footer");
                                break;
                            case HTML:
                                cell.setHtml("HTML Footer");
                                break;
                            case WIDGET:
                                cell.setComponent(new Button("Button Footer",
                                        new ClickListener() {

                                            @Override
                                            public void buttonClick(
                                                    ClickEvent event) {
                                                log("Button clicked!");
                                            }
                                        }));
                            default:
                                break;
                            }
                        }

                    }, c);
        }
        createBooleanAction("All columns hidable", "Columns", false,
                new Command<Grid, Boolean>() {

                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        for (Column col : grid.getColumns()) {
                            col.setHidable(value);
                        }

                    }
                });
    }

    private static String getColumnProperty(int c) {
        return "Column " + c;
    }

    protected void createPropertyActions() {
        createCategory("Properties", null);

        createBooleanAction("Prepend property", "Properties", false,
                new Command<Grid, Boolean>() {
                    private final Object propertyId = new Object();

                    @Override
                    public void execute(Grid c, Boolean enable, Object data) {
                        if (enable.booleanValue()) {
                            ds.addContainerProperty(propertyId, String.class,
                                    "property value");
                            grid.getColumn(propertyId).setHeaderCaption(
                                    "new property");
                            grid.setColumnOrder(propertyId);
                        } else {
                            ds.removeContainerProperty(propertyId);
                        }
                    }
                }, null);
    }

    protected void createRowActions() {
        createCategory("Body rows", null);

        class NewRowCommand implements Command<Grid, String> {
            private final int index;

            public NewRowCommand() {
                this(0);
            }

            public NewRowCommand(int index) {
                this.index = index;
            }

            @Override
            public void execute(Grid c, String value, Object data) {
                Item item = ds.addItemAt(index, new Object());
                for (int i = 0; i < COLUMNS; i++) {
                    Class<?> type = ds.getType(getColumnProperty(i));
                    if (String.class.isAssignableFrom(type)) {
                        Property<String> itemProperty = getProperty(item, i);
                        itemProperty.setValue("newcell: " + i);
                    } else if (Integer.class.isAssignableFrom(type)) {
                        Property<Integer> itemProperty = getProperty(item, i);
                        itemProperty.setValue(Integer.valueOf(i));
                    } else {
                        // let the default value be taken implicitly.
                    }
                }
            }

            private <T extends Object> Property<T> getProperty(Item item, int i) {
                @SuppressWarnings("unchecked")
                Property<T> itemProperty = item
                        .getItemProperty(getColumnProperty(i));
                return itemProperty;
            }
        }
        final NewRowCommand newRowCommand = new NewRowCommand();

        createClickAction("Add 18 rows", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        for (int i = 0; i < 18; i++) {
                            newRowCommand.execute(c, value, data);
                        }
                    }
                }, null);

        createClickAction("Add first row", "Body rows", newRowCommand, null);

        createClickAction("Add third row", "Body rows", new NewRowCommand(2),
                null);

        createClickAction("Remove first row", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        Object firstItemId = ds.getIdByIndex(0);
                        ds.removeItem(firstItemId);
                    }
                }, null);

        createClickAction("Remove 18 first rows", "Body rows",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        for (int i = 0; i < 18; i++) {
                            Object firstItemId = ds.getIdByIndex(0);
                            ds.removeItem(firstItemId);
                        }
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
                                .getContainerDataSource().firstItemId();
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

    protected void createEditorActions() {
        createBooleanAction("Enabled", "Editor", false,
                new Command<Grid, Boolean>() {
                    @Override
                    public void execute(Grid c, Boolean value, Object data) {
                        c.setEditorEnabled(value);
                    }
                });

        createClickAction("Edit item 5", "Editor", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                c.editItem(5);
            }
        }, null);

        createClickAction("Edit item 100", "Editor",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.editItem(100);
                    }
                }, null);
        createClickAction("Save", "Editor", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                try {
                    c.saveEditor();
                } catch (CommitException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, null);
        createClickAction("Cancel edit", "Editor", new Command<Grid, String>() {
            @Override
            public void execute(Grid c, String value, Object data) {
                c.cancelEditor();
            }
        }, null);

        createClickAction("Change save caption", "Editor",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.setEditorSaveCaption("ǝʌɐS");
                    }
                }, null);

        createClickAction("Change cancel caption", "Editor",
                new Command<Grid, String>() {
                    @Override
                    public void execute(Grid c, String value, Object data) {
                        c.setEditorCancelCaption("ʃǝɔuɐↃ");
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

    private void createDetailsActions() {
        Command<Grid, DetailsGenerator> swapDetailsGenerator = new Command<Grid, DetailsGenerator>() {
            @Override
            public void execute(Grid c, DetailsGenerator generator, Object data) {
                grid.setDetailsGenerator(generator);
            }
        };

        Command<Grid, Boolean> openOrCloseItemId = new Command<Grid, Boolean>() {
            @Override
            @SuppressWarnings("boxing")
            public void execute(Grid g, Boolean visible, Object itemId) {
                g.setDetailsVisible(itemId, visible);
            }
        };

        createCategory("Generators", "Details");
        createClickAction("NULL", "Generators", swapDetailsGenerator,
                DetailsGenerator.NULL);
        createClickAction("\"Watching\"", "Generators", swapDetailsGenerator,
                watchingDetailsGenerator);
        createClickAction("Detailed", "Generators", swapDetailsGenerator,
                detailedDetailsGenerator);
        createClickAction("Hierarchical", "Generators", swapDetailsGenerator,
                hierarchicalDetailsGenerator);

        createClickAction("- Change Component", "Generators",
                new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid c, Void value, Object data) {
                        Label label = (Label) detailsPanel.getContent();
                        if (label.getValue().equals("One")) {
                            detailsPanel.setContent(new Label("Two"));
                        } else {
                            detailsPanel.setContent(new Label("One"));
                        }
                    }
                }, null);

        createClickAction("Toggle firstItemId", "Details",
                new Command<Grid, Void>() {
                    @Override
                    public void execute(Grid g, Void value, Object data) {
                        Object firstItemId = g.getContainerDataSource()
                                .firstItemId();
                        boolean toggle = g.isDetailsVisible(firstItemId);
                        g.setDetailsVisible(firstItemId, !toggle);
                        g.setDetailsVisible(firstItemId, toggle);
                    }
                }, null);

        createBooleanAction("Open firstItemId", "Details", false,
                openOrCloseItemId, ds.firstItemId());

        createBooleanAction("Open 1", "Details", false, openOrCloseItemId,
                ds.getIdByIndex(1));

        createBooleanAction("Open 995", "Details", false, openOrCloseItemId,
                ds.getIdByIndex(995));
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
