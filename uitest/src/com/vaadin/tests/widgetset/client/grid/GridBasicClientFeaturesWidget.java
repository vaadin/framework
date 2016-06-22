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
package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.data.DataSource.RowHandle;
import com.vaadin.client.renderers.DateRenderer;
import com.vaadin.client.renderers.HtmlRenderer;
import com.vaadin.client.renderers.NumberRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.TextRenderer;
import com.vaadin.client.ui.VLabel;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.CellStyleGenerator;
import com.vaadin.client.widget.grid.DetailsGenerator;
import com.vaadin.client.widget.grid.EditorHandler;
import com.vaadin.client.widget.grid.EventCellReference;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.RowReference;
import com.vaadin.client.widget.grid.RowStyleGenerator;
import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.client.widget.grid.datasources.ListSorter;
import com.vaadin.client.widget.grid.events.BodyKeyDownHandler;
import com.vaadin.client.widget.grid.events.BodyKeyPressHandler;
import com.vaadin.client.widget.grid.events.BodyKeyUpHandler;
import com.vaadin.client.widget.grid.events.ColumnReorderEvent;
import com.vaadin.client.widget.grid.events.ColumnReorderHandler;
import com.vaadin.client.widget.grid.events.ColumnVisibilityChangeEvent;
import com.vaadin.client.widget.grid.events.ColumnVisibilityChangeHandler;
import com.vaadin.client.widget.grid.events.FooterKeyDownHandler;
import com.vaadin.client.widget.grid.events.FooterKeyPressHandler;
import com.vaadin.client.widget.grid.events.FooterKeyUpHandler;
import com.vaadin.client.widget.grid.events.GridKeyDownEvent;
import com.vaadin.client.widget.grid.events.GridKeyPressEvent;
import com.vaadin.client.widget.grid.events.GridKeyUpEvent;
import com.vaadin.client.widget.grid.events.HeaderKeyDownHandler;
import com.vaadin.client.widget.grid.events.HeaderKeyPressHandler;
import com.vaadin.client.widget.grid.events.HeaderKeyUpHandler;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widget.grid.selection.SelectionModel.None;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
import com.vaadin.client.widgets.Grid.FooterRow;
import com.vaadin.client.widgets.Grid.HeaderRow;
import com.vaadin.client.widgets.Grid.SelectionMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeaturesWidget.Data;

/**
 * Grid basic client features test application.
 * 
 * @author Vaadin Ltd
 */
public class GridBasicClientFeaturesWidget extends
        PureGWTTestApplication<Grid<List<Data>>> {
    public static final String ROW_STYLE_GENERATOR_NONE = "None";
    public static final String ROW_STYLE_GENERATOR_ROW_INDEX = "Row numbers";
    public static final String ROW_STYLE_GENERATOR_EVERY_THIRD = "Every third";

    public static final String CELL_STYLE_GENERATOR_NONE = "None";
    public static final String CELL_STYLE_GENERATOR_SIMPLE = "Simple";
    public static final String CELL_STYLE_GENERATOR_COL_INDEX = "Column index";

    public static enum Renderers {
        TEXT_RENDERER, HTML_RENDERER, NUMBER_RENDERER, DATE_RENDERER;
    }

    private class TestEditorHandler implements EditorHandler<List<Data>> {

        private Map<Grid.Column<?, ?>, TextBox> widgets = new HashMap<Grid.Column<?, ?>, TextBox>();

        private Label log = new Label();

        {
            log.addStyleName("grid-editor-log");
            addSouth(log, 20);
        }

        @Override
        public void bind(EditorRequest<List<Data>> request) {
            List<Data> rowData = ds.getRow(request.getRowIndex());

            boolean hasSelectionColumn = !(grid.getSelectionModel() instanceof None);
            for (int i = 0; i < rowData.size(); i++) {
                int columnIndex = hasSelectionColumn ? i + 1 : i;
                getWidget(columnIndex).setText(rowData.get(i).value.toString());
            }
            request.success();
        }

        @Override
        public void cancel(EditorRequest<List<Data>> request) {
            log.setText("Row " + request.getRowIndex() + " edit cancelled");
        }

        @Override
        public void save(EditorRequest<List<Data>> request) {
            if (secondEditorError) {
                request.failure(
                        "Syntethic fail of editor in column 2. "
                                + "This message is so long that it doesn't fit into its box",
                        Collections.<Column<?, List<Data>>> singleton(grid
                                .getColumn(2)));
                return;
            }
            try {
                log.setText("Row " + request.getRowIndex() + " edit committed");
                List<Data> rowData = ds.getRow(request.getRowIndex());

                int i = 0;
                for (; i < COLUMNS - MANUALLY_FORMATTED_COLUMNS; i++) {
                    rowData.get(i).value = getWidget(i).getText();
                }

                rowData.get(i).value = Integer
                        .valueOf(getWidget(i++).getText());
                rowData.get(i).value = new Date(getWidget(i++).getText());
                rowData.get(i).value = getWidget(i++).getText();
                rowData.get(i).value = Integer
                        .valueOf(getWidget(i++).getText());
                rowData.get(i).value = Integer
                        .valueOf(getWidget(i++).getText());

                // notify data source of changes
                ds.asList().set(request.getRowIndex(), rowData);
                request.success();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).warning(e.toString());
                request.failure(null, null);
            }
        }

        @Override
        public TextBox getWidget(Grid.Column<?, List<Data>> column) {
            if (grid.getColumns().indexOf(column) == 0
                    && !(grid.getSelectionModel() instanceof None)) {
                return null;
            }

            TextBox w = widgets.get(column);
            if (w == null) {
                w = new TextBox();
                w.getElement().getStyle().setMargin(0, Unit.PX);
                widgets.put(column, w);
            }
            return w;
        }

        private TextBox getWidget(int i) {
            return getWidget(grid.getColumn(i));
        }
    }

    private static final int MANUALLY_FORMATTED_COLUMNS = 5;
    public static final int COLUMNS = 12;
    public static final int ROWS = 1000;

    private final Grid<List<Data>> grid;
    private List<List<Data>> data;
    private final ListDataSource<List<Data>> ds;
    private final ListSorter<List<Data>> sorter;

    private boolean secondEditorError = false;

    /**
     * Our basic data object
     */
    public final static class Data {
        Object value;
    }

    /**
     * @return
     */
    private List<List<Data>> createData(int rowCount) {
        List<List<Data>> dataList = new ArrayList<List<Data>>();
        Random rand = new Random();
        rand.setSeed(13334);
        long timestamp = 0;
        for (int row = 0; row < rowCount; row++) {

            List<Data> datarow = createDataRow(COLUMNS);
            dataList.add(datarow);
            Data d;

            int col = 0;
            for (; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {
                d = datarow.get(col);
                d.value = "(" + row + ", " + col + ")";
            }

            d = datarow.get(col++);
            d.value = Integer.valueOf(row);

            d = datarow.get(col++);
            d.value = new Date(timestamp);
            timestamp += 91250000; // a bit over a day, just to get
                                   // variation

            d = datarow.get(col++);
            d.value = "<b>" + row + "</b>";

            d = datarow.get(col++);
            d.value = Integer.valueOf(rand.nextInt());

            d = datarow.get(col++);
            d.value = Integer.valueOf(rand.nextInt(5));
        }

        return dataList;
    }

    /**
     * Convenience method for creating a list of Data objects to be used as a
     * Row in the data source
     * 
     * @param cols
     *            number of columns (items) to include in the row
     * @return
     */
    private List<Data> createDataRow(int cols) {
        List<Data> list = new ArrayList<Data>(cols);
        for (int i = 0; i < cols; ++i) {
            list.add(new Data());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public GridBasicClientFeaturesWidget() {
        super(new Grid<List<Data>>());

        // Initialize data source
        data = createData(ROWS);

        ds = new ListDataSource<List<Data>>(data);
        grid = getTestedWidget();
        grid.getElement().setId("testComponent");
        grid.setDataSource(ds);
        grid.addSelectAllHandler(ds.getSelectAllHandler());
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setEditorHandler(new TestEditorHandler());

        sorter = new ListSorter<List<Data>>(grid);

        // Create a bunch of grid columns

        // Data source layout:
        // text (String) * (COLUMNS - MANUALLY_FORMATTED_COLUMNS + 1) |
        // rownumber (Integer) | some date (Date) | row number as HTML (String)
        // | random value (Integer)

        int col = 0;

        // Text times COLUMNS - MANUALLY_FORMATTED_COLUMNS
        for (col = 0; col < COLUMNS - MANUALLY_FORMATTED_COLUMNS; ++col) {

            final int c = col;

            Grid.Column<String, List<Data>> column = new Grid.Column<String, List<Data>>(
                    createRenderer(Renderers.TEXT_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            };

            column.setWidth(50 + c * 25);
            column.setHeaderCaption("Header (0," + c + ")");

            grid.addColumn(column);
        }

        // Integer row number
        {
            final int c = col++;
            Grid.Column<Integer, List<Data>> column = new Grid.Column<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            };
            grid.addColumn(column);
            column.setHeaderCaption("Header (0," + c + ")");
        }

        // Some date
        {
            final int c = col++;
            Grid.Column<Date, List<Data>> column = new Grid.Column<Date, List<Data>>(
                    createRenderer(Renderers.DATE_RENDERER)) {
                @Override
                public Date getValue(List<Data> row) {
                    return (Date) row.get(c).value;
                }
            };
            grid.addColumn(column);
            column.setHeaderCaption("Header (0," + c + ")");
        }

        // Row number as a HTML string
        {
            final int c = col++;
            Grid.Column<String, List<Data>> column = new Grid.Column<String, List<Data>>(
                    createRenderer(Renderers.HTML_RENDERER)) {
                @Override
                public String getValue(List<Data> row) {
                    return (String) row.get(c).value;
                }
            };
            grid.addColumn(column);
            column.setHeaderCaption("Header (0," + c + ")");
        }

        // Random integer value
        {
            final int c = col++;
            Grid.Column<Integer, List<Data>> column = new Grid.Column<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            };
            grid.addColumn(column);
            column.setHeaderCaption("Header (0," + c + ")");
        }

        // Random integer value between 0 and 5
        {
            final int c = col++;
            Grid.Column<Integer, List<Data>> column = new Grid.Column<Integer, List<Data>>(
                    createRenderer(Renderers.NUMBER_RENDERER)) {
                @Override
                public Integer getValue(List<Data> row) {
                    return (Integer) row.get(c).value;
                }
            };
            grid.addColumn(column);
            column.setHeaderCaption("Header (0," + c + ")");
        }

        grid.getColumn(3).setEditable(false);

        HeaderRow row = grid.getDefaultHeaderRow();
        for (int i = 0; i < col; ++i) {
            String caption = "Header (0," + i + ")";
            Grid.Column<?, ?> column = grid.getColumn(i);
            // Lets use some different cell types
            if (i % 3 == 0) {
                // No-op
            } else if (i % 2 == 0) {
                row.getCell(column).setHtml("<b>" + caption + "</b>");
            } else {
                row.getCell(column).setWidget(new HTML(caption));
            }
        }
        ++headerCounter;

        //
        // Populate the menu
        //

        createStateMenu();
        createColumnsMenu();
        createHeaderMenu();
        createFooterMenu();
        createEditorMenu();
        createInternalsMenu();
        createDataSourceMenu();
        createDetailsMenu();
        createSidebarMenu();

        grid.getElement().getStyle().setZIndex(0);

        //
        // Composite wrapping for grid.
        //
        boolean isComposite = Window.Location.getParameter("composite") != null;
        if (isComposite) {
            addNorth(new Composite() {
                {
                    initWidget(grid);
                }
            }, 400);
        } else {
            addNorth(grid, 400);
        }

        createKeyHandlers();
    }

    private void createInternalsMenu() {
        String[] listenersPath = { "Component", "Internals", "Listeners" };
        final Label label = new Label();
        addSouth(label, 20);

        addMenuCommand("Add scroll listener", new ScheduledCommand() {
            private HandlerRegistration scrollHandler = null;

            @Override
            public void execute() {
                if (scrollHandler != null) {
                    return;
                }
                scrollHandler = grid.addScrollHandler(new ScrollHandler() {
                    @Override
                    public void onScroll(ScrollEvent event) {
                        @SuppressWarnings("hiding")
                        final Grid<?> grid = (Grid<?>) event.getSource();
                        label.setText("scrollTop: " + grid.getScrollTop()
                                + ", scrollLeft: " + grid.getScrollLeft());
                    }
                });
            }
        }, listenersPath);
        addMenuCommand("Add ColumnReorder listener", new ScheduledCommand() {
            private HandlerRegistration columnReorderHandler = null;

            @Override
            public void execute() {
                if (columnReorderHandler != null) {
                    return;
                }
                final Label columnOrderLabel = new Label();
                columnOrderLabel.getElement().setId("columnreorder");
                addLineEnd(columnOrderLabel, 300);
                columnReorderHandler = grid
                        .addColumnReorderHandler(new ColumnReorderHandler<List<Data>>() {

                            private int eventIndex = 0;

                            @Override
                            public void onColumnReorder(
                                    ColumnReorderEvent<List<Data>> event) {
                                columnOrderLabel.getElement().setAttribute(
                                        "columns", "" + (++eventIndex));
                            }
                        });
            }
        }, listenersPath);
        addMenuCommand("Add Column Visibility Change listener",
                new ScheduledCommand() {
                    private HandlerRegistration columnVisibilityHandler = null;

                    @Override
                    public void execute() {
                        if (columnVisibilityHandler != null) {
                            return;
                        }
                        final Label columnOrderLabel = new Label();
                        columnOrderLabel.getElement().setId("columnvisibility");
                        addLineEnd(columnOrderLabel, 250);
                        ColumnVisibilityChangeHandler handler = new ColumnVisibilityChangeHandler<List<Data>>() {

                            private int eventIndex = 0;

                            @Override
                            public void onVisibilityChange(
                                    ColumnVisibilityChangeEvent<List<Data>> event) {
                                columnOrderLabel.getElement().setAttribute(
                                        "counter", "" + (++eventIndex));
                                columnOrderLabel.getElement().setAttribute(
                                        "useroriginated",
                                        (Boolean.toString(event
                                                .isUserOriginated())));
                                columnOrderLabel.getElement().setAttribute(
                                        "ishidden",
                                        (Boolean.toString(event.isHidden())));
                                columnOrderLabel.getElement().setAttribute(
                                        "columnindex",
                                        ""
                                                + grid.getColumns().indexOf(
                                                        event.getColumn()));
                            }
                        };

                        columnVisibilityHandler = grid
                                .addColumnVisibilityChangeHandler(handler);
                    }
                }, listenersPath);
        addMenuCommand("Add context menu listener", new ScheduledCommand() {

            HandlerRegistration handler = null;
            ContextMenuHandler contextMenuHandler = new ContextMenuHandler() {

                @Override
                public void onContextMenu(ContextMenuEvent event) {
                    event.preventDefault();
                    final String location;
                    EventCellReference<?> cellRef = grid.getEventCell();
                    if (cellRef.isHeader()) {
                        location = "header";
                    } else if (cellRef.isBody()) {
                        location = "body";
                    } else if (cellRef.isFooter()) {
                        location = "footer";
                    } else {
                        location = "somewhere";
                    }

                    getLogger().info(
                            "Prevented opening a context menu in grid "
                                    + location);
                }
            };

            @Override
            public void execute() {
                if (handler != null) {
                    grid.unsinkEvents(Event.ONCONTEXTMENU);
                    handler.removeHandler();
                } else {
                    grid.sinkEvents(Event.ONCONTEXTMENU);
                    handler = grid.addDomHandler(contextMenuHandler,
                            ContextMenuEvent.getType());
                }
            }

        }, listenersPath);
    }

    private void createStateMenu() {
        String[] selectionModePath = { "Component", "State", "Selection mode" };
        String[] primaryStyleNamePath = { "Component", "State",
                "Primary Stylename" };
        String[] rowStyleGeneratorNamePath = { "Component", "State",
                "Row style generator" };
        String[] cellStyleGeneratorNamePath = { "Component", "State",
                "Cell style generator" };

        addMenuCommand("multi", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.MULTI);
            }
        }, selectionModePath);

        addMenuCommand("single", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.SINGLE);
            }
        }, selectionModePath);

        addMenuCommand("single (no deselect)", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.SINGLE);
                ((SelectionModel.Single<?>) grid.getSelectionModel())
                        .setDeselectAllowed(false);
            }
        }, selectionModePath);

        addMenuCommand("none", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSelectionMode(SelectionMode.NONE);
            }
        }, selectionModePath);

        addMenuCommand("v-grid", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-grid");

            }
        }, primaryStyleNamePath);

        addMenuCommand("v-escalator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-escalator");

            }
        }, primaryStyleNamePath);

        addMenuCommand("v-custom-style", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setStylePrimaryName("v-custom-style");

            }
        }, primaryStyleNamePath);

        addMenuCommand("Edit and refresh Row 0", new ScheduledCommand() {
            @Override
            public void execute() {
                DataSource<List<Data>> ds = grid.getDataSource();
                RowHandle<List<Data>> rowHandle = ds.getHandle(ds.getRow(0));
                rowHandle.getRow().get(0).value = "Foo";
                rowHandle.updateRow();
            }
        }, "Component", "State");

        addMenuCommand("Delayed edit of Row 0", new ScheduledCommand() {
            @Override
            public void execute() {
                DataSource<List<Data>> ds = grid.getDataSource();
                final RowHandle<List<Data>> rowHandle = ds.getHandle(ds
                        .getRow(0));

                new Timer() {
                    @Override
                    public void run() {
                        rowHandle.getRow().get(0).value = "Bar";
                        rowHandle.updateRow();
                    }

                }.schedule(5000);
            }
        }, "Component", "State");

        addMenuCommand(ROW_STYLE_GENERATOR_NONE, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setRowStyleGenerator(null);
            }
        }, rowStyleGeneratorNamePath);

        addMenuCommand(ROW_STYLE_GENERATOR_EVERY_THIRD, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setRowStyleGenerator(new RowStyleGenerator<List<Data>>() {

                    @Override
                    public String getStyle(RowReference<List<Data>> rowReference) {
                        if (rowReference.getRowIndex() % 3 == 0) {
                            return "third";
                        } else {
                            // First manual col is integer
                            Integer value = (Integer) rowReference.getRow()
                                    .get(COLUMNS - MANUALLY_FORMATTED_COLUMNS).value;
                            return value.toString();
                        }
                    }
                });

            }
        }, rowStyleGeneratorNamePath);

        addMenuCommand(ROW_STYLE_GENERATOR_ROW_INDEX, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setRowStyleGenerator(new RowStyleGenerator<List<Data>>() {

                    @Override
                    public String getStyle(RowReference<List<Data>> rowReference) {
                        return Integer.toString(rowReference.getRowIndex());
                    }
                });

            }
        }, rowStyleGeneratorNamePath);

        addMenuCommand(CELL_STYLE_GENERATOR_NONE, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setCellStyleGenerator(null);
            }
        }, cellStyleGeneratorNamePath);

        addMenuCommand(CELL_STYLE_GENERATOR_SIMPLE, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setCellStyleGenerator(new CellStyleGenerator<List<Data>>() {

                    @Override
                    public String getStyle(
                            CellReference<List<Data>> cellReference) {
                        Grid.Column<?, List<Data>> column = cellReference
                                .getColumn();
                        if (column == grid.getColumn(2)) {
                            return "two";
                        } else if (column == grid.getColumn(COLUMNS
                                - MANUALLY_FORMATTED_COLUMNS)) {
                            // First manual col is integer
                            Integer value = (Integer) column
                                    .getValue(cellReference.getRow());
                            return value.toString();

                        } else {
                            return null;
                        }
                    }
                });
            }
        }, cellStyleGeneratorNamePath);
        addMenuCommand(CELL_STYLE_GENERATOR_COL_INDEX, new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setCellStyleGenerator(new CellStyleGenerator<List<Data>>() {

                    @Override
                    public String getStyle(
                            CellReference<List<Data>> cellReference) {
                        return cellReference.getRowIndex()
                                + "_"
                                + grid.getColumns().indexOf(
                                        cellReference.getColumn());
                    }
                });
            }
        }, cellStyleGeneratorNamePath);

        for (int i = -1; i <= COLUMNS; i++) {
            final int index = i;
            // Including dummy "columns" prefix because TB fails to select item
            // if it's too narrow
            addMenuCommand(Integer.toString(index) + " columns",
                    new ScheduledCommand() {
                        @Override
                        public void execute() {
                            grid.setFrozenColumnCount(index);
                        }
                    }, "Component", "State", "Frozen column count");
        }

        addMenuCommand("Enabled", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setEnabled(!grid.isEnabled());
            }
        }, "Component", "State");
        addMenuCommand("Reverse grid columns", new ScheduledCommand() {

            @Override
            public void execute() {
                List<Column> columns = new ArrayList<Column>(grid.getColumns());
                Collections.reverse(columns);
                grid.setColumnOrder(columns.toArray(new Column[columns.size()]));
            }
        }, "Component", "State");
        addMenuCommand("Column Reordering", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setColumnReorderingAllowed(!grid
                        .isColumnReorderingAllowed());
            }
        }, "Component", "State");
        addMenuCommand("250px", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setWidth("250px");
            }
        }, "Component", "State", "Width");
        addMenuCommand("500px", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setWidth("500px");
            }
        }, "Component", "State", "Width");
        addMenuCommand("750px", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setWidth("750px");
            }
        }, "Component", "State", "Width");
        addMenuCommand("1000px", new ScheduledCommand() {

            @Override
            public void execute() {
                grid.setWidth("1000px");
            }
        }, "Component", "State", "Width");

        createScrollToRowMenu();
    }

    private void createScrollToRowMenu() {
        String[] menupath = new String[] { "Component", "State",
                "Scroll to...", null };

        for (int i = 0; i < ROWS; i += 100) {
            menupath[3] = "Row " + i + "...";
            for (final ScrollDestination scrollDestination : ScrollDestination
                    .values()) {
                final int row = i;
                addMenuCommand("Destination " + scrollDestination,
                        new ScheduledCommand() {
                            @Override
                            public void execute() {
                                grid.scrollToRow(row, scrollDestination);
                            }
                        }, menupath);
            }
        }

        int i = ROWS - 1;
        menupath[3] = "Row " + i + "...";
        for (final ScrollDestination scrollDestination : ScrollDestination
                .values()) {
            final int row = i;
            addMenuCommand("Destination " + scrollDestination,
                    new ScheduledCommand() {
                        @Override
                        public void execute() {
                            grid.scrollToRow(row, scrollDestination);
                        }
                    }, menupath);
        }

    }

    private void createColumnsMenu() {

        for (int i = 0; i < COLUMNS; i++) {
            final int index = i;
            final Grid.Column<?, List<Data>> column = grid.getColumn(index);
            addMenuCommand("Sortable", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setSortable(!column.isSortable());
                }
            }, "Component", "Columns", "Column " + i);
            addMenuCommand("Hidden", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setHidden(!column.isHidden());
                }
            }, "Component", "Columns", "Column " + i);
            addMenuCommand("Hidable", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setHidable(!column.isHidable());
                }
            }, "Component", "Columns", "Column " + i);
            addMenuCommand("auto", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setWidth(-1);
                }
            }, "Component", "Columns", "Column " + i, "Width");
            addMenuCommand("50px", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setWidth(50);
                }
            }, "Component", "Columns", "Column " + i, "Width");
            addMenuCommand("200px", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setWidth(200);
                }
            }, "Component", "Columns", "Column " + i, "Width");

            // Header types
            addMenuCommand("Text Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    column.setHeaderCaption("Text Header");
                }
            }, "Component", "Columns", "Column " + i, "Header Type");
            addMenuCommand("HTML Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getHeaderRow(0).getCell(column)
                            .setHtml("<b>HTML Header</b>");
                }
            }, "Component", "Columns", "Column " + i, "Header Type");
            addMenuCommand("Widget Header", new ScheduledCommand() {
                @Override
                public void execute() {
                    final Button button = new Button("Button Header");
                    button.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            button.setText("Clicked");
                        }
                    });
                    grid.getHeaderRow(0).getCell(column).setWidget(button);
                }
            }, "Component", "Columns", "Column " + i, "Header Type");

            // Footer types
            addMenuCommand("Text Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getFooterRow(0).getCell(column).setText("Text Footer");
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");
            addMenuCommand("HTML Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    grid.getFooterRow(0).getCell(column)
                            .setHtml("<b>HTML Footer</b>");
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");
            addMenuCommand("Widget Footer", new ScheduledCommand() {
                @Override
                public void execute() {
                    final Button button = new Button("Button Footer");
                    button.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            button.setText("Clicked");
                        }
                    });
                    grid.getFooterRow(0).getCell(column).setWidget(button);
                }
            }, "Component", "Columns", "Column " + i, "Footer Type");

            // Renderer throwing exceptions
            addMenuCommand("Broken renderer", new ScheduledCommand() {
                @Override
                public void execute() {
                    final Renderer<Object> originalRenderer = (Renderer<Object>) column
                            .getRenderer();

                    column.setRenderer(new Renderer<Object>() {
                        @Override
                        public void render(RendererCellReference cell,
                                Object data) {
                            if (cell.getRowIndex() == cell.getColumnIndex()) {
                                throw new RuntimeException("I'm broken");
                            }
                            originalRenderer.render(cell, data);
                        }
                    });
                }
            }, "Component", "Columns", "Column " + i);
            addMenuCommand("Move column left", new ScheduledCommand() {

                @SuppressWarnings("unchecked")
                @Override
                public void execute() {
                    List<Column<?, List<Data>>> cols = grid.getColumns();
                    ArrayList<Column> reordered = new ArrayList<Column>(cols);
                    final int index = cols.indexOf(column);
                    if (index == 0) {
                        Column<?, List<Data>> col = reordered.remove(0);
                        reordered.add(col);
                    } else {
                        Column<?, List<Data>> col = reordered.remove(index);
                        reordered.add(index - 1, col);
                    }
                    grid.setColumnOrder(reordered.toArray(new Column[reordered
                            .size()]));
                }
            }, "Component", "Columns", "Column " + i);
        }
    }

    private int headerCounter = 0;
    private int footerCounter = 0;

    private void setHeaderTexts(HeaderRow row) {
        for (int i = 0; i < COLUMNS; ++i) {
            String caption = "Header (" + headerCounter + "," + i + ")";

            // Lets use some different cell types
            if (i % 3 == 0) {
                row.getCell(grid.getColumn(i)).setText(caption);
            } else if (i % 2 == 0) {
                row.getCell(grid.getColumn(i))
                        .setHtml("<b>" + caption + "</b>");
            } else {
                row.getCell(grid.getColumn(i)).setWidget(new HTML(caption));
            }
        }
        headerCounter++;
    }

    private void setFooterTexts(FooterRow row) {
        for (int i = 0; i < COLUMNS; ++i) {
            String caption = "Footer (" + footerCounter + "," + i + ")";

            // Lets use some different cell types
            if (i % 3 == 0) {
                row.getCell(grid.getColumn(i)).setText(caption);
            } else if (i % 2 == 0) {
                row.getCell(grid.getColumn(i))
                        .setHtml("<b>" + caption + "</b>");
            } else {
                row.getCell(grid.getColumn(i)).setWidget(new HTML(caption));
            }
        }
        footerCounter++;
    }

    private void createHeaderMenu() {
        final String[] menuPath = { "Component", "Header" };

        addMenuCommand("Visible", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setHeaderVisible(!grid.isHeaderVisible());
            }
        }, menuPath);

        addMenuCommand("Top", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDefaultHeaderRow(grid.getHeaderRow(0));
            }
        }, "Component", "Header", "Default row");
        addMenuCommand("Bottom", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDefaultHeaderRow(grid.getHeaderRow(grid
                        .getHeaderRowCount() - 1));
            }
        }, "Component", "Header", "Default row");
        addMenuCommand("Unset", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDefaultHeaderRow(null);
            }
        }, "Component", "Header", "Default row");

        addMenuCommand("Prepend row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureHeaderRow(grid.prependHeaderRow());
            }
        }, menuPath);
        addMenuCommand("Append row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureHeaderRow(grid.appendHeaderRow());
            }
        }, menuPath);
        addMenuCommand("Remove top row", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.removeHeaderRow(0);
            }
        }, menuPath);
        addMenuCommand("Remove bottom row", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.removeHeaderRow(grid.getHeaderRowCount() - 1);
            }
        }, menuPath);

    }

    private void configureHeaderRow(final HeaderRow row) {
        setHeaderTexts(row);
        String rowTitle = "Row " + grid.getHeaderRowCount();
        final String[] menuPath = { "Component", "Header", rowTitle };

        addMenuCommand("Join column cells 0, 1", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(row.getCell(grid.getColumn(0)),
                        row.getCell(grid.getColumn(1))).setText(
                        "Join column cells 0, 1");

            }
        }, menuPath);

        addMenuCommand("Join columns 1, 2", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(1), grid.getColumn(2)).setText(
                        "Join columns 1, 2");
                ;

            }
        }, menuPath);

        addMenuCommand("Join columns 3, 4, 5", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(3), grid.getColumn(4),
                        grid.getColumn(5)).setText("Join columns 3, 4, 5");

            }
        }, menuPath);

        addMenuCommand("Join all columns", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(
                        grid.getColumns().toArray(
                                new Grid.Column[grid.getColumnCount()]))
                        .setText("Join all columns");
                ;

            }
        }, menuPath);
    }

    private void createFooterMenu() {
        final String[] menuPath = { "Component", "Footer" };

        addMenuCommand("Visible", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setFooterVisible(!grid.isFooterVisible());
            }
        }, menuPath);

        addMenuCommand("Prepend row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureFooterRow(grid.prependFooterRow());
            }
        }, menuPath);
        addMenuCommand("Append row", new ScheduledCommand() {
            @Override
            public void execute() {
                configureFooterRow(grid.appendFooterRow());
            }
        }, menuPath);
        addMenuCommand("Remove top row", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.removeFooterRow(0);
            }
        }, menuPath);
        addMenuCommand("Remove bottom row", new ScheduledCommand() {
            @Override
            public void execute() {
                assert grid.getFooterRowCount() > 0;
                grid.removeFooterRow(grid.getFooterRowCount() - 1);
            }
        }, menuPath);
    }

    private void createEditorMenu() {
        addMenuCommand("Enabled", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setEditorEnabled(!grid.isEditorEnabled());
            }
        }, "Component", "Editor");

        addMenuCommand("Edit row 5", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.editRow(5);
            }
        }, "Component", "Editor");

        addMenuCommand("Edit row 100", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.editRow(100);
            }
        }, "Component", "Editor");

        addMenuCommand("Save", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.saveEditor();
            }
        }, "Component", "Editor");

        addMenuCommand("Cancel edit", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.cancelEditor();
            }
        }, "Component", "Editor");

        addMenuCommand("Change Save Caption", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setEditorSaveCaption("ǝʌɐS");
            }
        }, "Component", "Editor");

        addMenuCommand("Change Cancel Caption", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setEditorCancelCaption("ʃǝɔuɐↃ");
            }
        }, "Component", "Editor");

        addMenuCommand("Toggle second editor error", new ScheduledCommand() {
            @Override
            public void execute() {
                secondEditorError = !secondEditorError;
            }
        }, "Component", "Editor");
    }

    private void configureFooterRow(final FooterRow row) {
        setFooterTexts(row);
        String rowTitle = "Row " + grid.getFooterRowCount();
        final String[] menuPath = { "Component", "Footer", rowTitle };

        addMenuCommand("Join column cells 0, 1", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(row.getCell(grid.getColumn(0)),
                        row.getCell(grid.getColumn(1))).setText(
                        "Join column cells 0, 1");

            }
        }, menuPath);

        addMenuCommand("Join columns 1, 2", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(grid.getColumn(1), grid.getColumn(2)).setText(
                        "Join columns 1, 2");
                ;

            }
        }, menuPath);

        addMenuCommand("Join all columns", new ScheduledCommand() {

            @Override
            public void execute() {
                row.join(
                        grid.getColumns().toArray(
                                new Grid.Column[grid.getColumnCount()]))
                        .setText("Join all columns");
                ;

            }
        }, menuPath);
    }

    private void createDataSourceMenu() {
        final String[] menuPath = { "Component", "DataSource" };

        addMenuCommand("Reset with 100 rows of Data", new ScheduledCommand() {
            @Override
            public void execute() {
                ds.asList().clear();
                data = createData(100);
                ds.asList().addAll(data);
            }
        }, menuPath);

        addMenuCommand("Reset with " + ROWS + " rows of Data",
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        ds.asList().clear();
                        data = createData(ROWS);
                        ds.asList().addAll(data);
                    }
                }, menuPath);
    }

    /**
     * Creates a renderer for a {@link Renderers}
     */
    @SuppressWarnings("rawtypes")
    private final Renderer createRenderer(Renderers renderer) {
        switch (renderer) {
        case TEXT_RENDERER:
            return new TextRenderer();

        case HTML_RENDERER:
            return new HtmlRenderer() {

                @Override
                public void render(RendererCellReference cell, String htmlString) {
                    super.render(cell, "<b>" + htmlString + "</b>");
                }
            };

        case NUMBER_RENDERER:
            return new NumberRenderer();

        case DATE_RENDERER:
            return new DateRenderer();

        default:
            return new TextRenderer();
        }
    }

    /**
     * Creates a collection of handlers for all the grid key events
     */
    private void createKeyHandlers() {
        final List<VLabel> labels = new ArrayList<VLabel>();
        for (int i = 0; i < 9; ++i) {
            VLabel tmp = new VLabel();
            addNorth(tmp, 20);
            labels.add(tmp);
        }

        // Key Down Events
        grid.addBodyKeyDownHandler(new BodyKeyDownHandler() {
            private final VLabel label = labels.get(0);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addHeaderKeyDownHandler(new HeaderKeyDownHandler() {
            private final VLabel label = labels.get(1);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addFooterKeyDownHandler(new FooterKeyDownHandler() {
            private final VLabel label = labels.get(2);

            @Override
            public void onKeyDown(GridKeyDownEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        // Key Up Events
        grid.addBodyKeyUpHandler(new BodyKeyUpHandler() {
            private final VLabel label = labels.get(3);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addHeaderKeyUpHandler(new HeaderKeyUpHandler() {
            private final VLabel label = labels.get(4);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addFooterKeyUpHandler(new FooterKeyUpHandler() {
            private final VLabel label = labels.get(5);

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        // Key Press Events
        grid.addBodyKeyPressHandler(new BodyKeyPressHandler() {
            private final VLabel label = labels.get(6);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addHeaderKeyPressHandler(new HeaderKeyPressHandler() {
            private final VLabel label = labels.get(7);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

        grid.addFooterKeyPressHandler(new FooterKeyPressHandler() {
            private final VLabel label = labels.get(8);

            @Override
            public void onKeyPress(GridKeyPressEvent event) {
                CellReference<?> focused = event.getFocusedCell();
                updateLabel(label, event.toDebugString(),
                        focused.getRowIndex(), focused.getColumnIndex());
            }
        });

    }

    private void updateLabel(VLabel label, String output, int object, int column) {
        String coords = "(" + object + ", " + column + ")";
        label.setText(coords + " " + output);
    }

    private void createDetailsMenu() {
        String[] menupath = new String[] { "Component", "Row details" };
        addMenuCommand("Set generator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDetailsGenerator(new DetailsGenerator() {
                    @Override
                    public Widget getDetails(int rowIndex) {
                        FlowPanel panel = new FlowPanel();

                        final Label label = new Label("Row: " + rowIndex + ".");
                        Button button = new Button("Button",
                                new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent event) {
                                        label.setText("clicked");
                                    }
                                });

                        panel.add(label);
                        panel.add(button);
                        return panel;
                    }
                });
            }
        }, menupath);

        addMenuCommand("Set faulty generator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDetailsGenerator(new DetailsGenerator() {
                    @Override
                    public Widget getDetails(int rowIndex) {
                        throw new RuntimeException("This is by design.");
                    }
                });
            }
        }, menupath);

        addMenuCommand("Set empty generator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setDetailsGenerator(new DetailsGenerator() {
                    /*
                     * While this is functionally equivalent to the NULL
                     * generator, it's good to be explicit, since the behavior
                     * isn't strictly tied between them. NULL generator might be
                     * changed to render something different by default, and an
                     * empty generator might behave differently also in the
                     * future.
                     */

                    @Override
                    public Widget getDetails(int rowIndex) {
                        return null;
                    }
                });
            }
        }, menupath);

        String[] togglemenupath = new String[] { menupath[0], menupath[1],
                "Toggle details for..." };
        for (int i : new int[] { 0, 1, 100, 200, 300, 400, 500, 600, 700, 800,
                900, 999 }) {
            final int rowIndex = i;
            addMenuCommand("Row " + rowIndex, new ScheduledCommand() {
                boolean visible = false;

                @Override
                public void execute() {
                    visible = !visible;
                    grid.setDetailsVisible(rowIndex, visible);
                }
            }, togglemenupath);
        }

    }

    private static Logger getLogger() {
        return Logger.getLogger(GridBasicClientFeaturesWidget.class.getName());
    }

    private void createSidebarMenu() {
        String[] menupath = new String[] { "Component", "Sidebar" };

        final List<MenuItem> customMenuItems = new ArrayList<MenuItem>();
        final List<MenuItemSeparator> separators = new ArrayList<MenuItemSeparator>();

        addMenuCommand("Add item to end", new ScheduledCommand() {
            @Override
            public void execute() {
                MenuItem item = createSidebarMenuItem(customMenuItems.size());
                customMenuItems.add(item);
                grid.getSidebarMenu().addItem(item);
            }
        }, menupath);

        addMenuCommand("Add item before index 1", new ScheduledCommand() {
            @Override
            public void execute() {
                MenuItem item = createSidebarMenuItem(customMenuItems.size());
                customMenuItems.add(item);
                grid.getSidebarMenu().insertItem(item, 1);
            }
        }, menupath);

        addMenuCommand("Remove last added item", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.getSidebarMenu().removeItem(
                        customMenuItems.remove(customMenuItems.size() - 1));
            }
        }, menupath);

        addMenuCommand("Add separator to end", new ScheduledCommand() {
            @Override
            public void execute() {
                MenuItemSeparator separator = new MenuItemSeparator();
                separators.add(separator);
                grid.getSidebarMenu().addSeparator(separator);
            }
        }, menupath);

        addMenuCommand("Add separator before index 1", new ScheduledCommand() {
            @Override
            public void execute() {
                MenuItemSeparator separator = new MenuItemSeparator();
                separators.add(separator);
                grid.getSidebarMenu().insertSeparator(separator, 1);
            }
        }, menupath);

        addMenuCommand("Remove last added separator", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.getSidebarMenu().removeSeparator(
                        separators.remove(separators.size() - 1));
            }
        }, menupath);

        addMenuCommand("Toggle sidebar visibility", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSidebarOpen(!grid.isSidebarOpen());
            }
        }, menupath);

        addMenuCommand("Open sidebar and disable grid", new ScheduledCommand() {
            @Override
            public void execute() {
                grid.setSidebarOpen(true);

                Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        if(grid.isSidebarOpen()) {
                            grid.setEnabled(false);

                            return false;
                        }

                        return true;
                    }
                }, 250);
            }
        }, menupath);
    }

    private MenuItem createSidebarMenuItem(final int index) {
        final MenuItem menuItem = new MenuItem("Custom menu item " + index,
                new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (index % 2 == 0) {
                            grid.setSidebarOpen(false);
                        }
                        getLogger().info("Menu item " + index + " selected");
                    }
                });
        return menuItem;
    }
}
