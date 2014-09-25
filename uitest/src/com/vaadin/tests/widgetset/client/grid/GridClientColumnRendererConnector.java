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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.client.ui.grid.Renderer;
import com.vaadin.client.ui.grid.datasources.ListDataSource;
import com.vaadin.client.ui.grid.datasources.ListSorter;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;
import com.vaadin.client.ui.grid.renderers.DateRenderer;
import com.vaadin.client.ui.grid.renderers.HtmlRenderer;
import com.vaadin.client.ui.grid.renderers.NumberRenderer;
import com.vaadin.client.ui.grid.renderers.TextRenderer;
import com.vaadin.client.ui.grid.renderers.WidgetRenderer;
import com.vaadin.client.ui.grid.sort.Sort;
import com.vaadin.client.ui.grid.sort.SortEvent;
import com.vaadin.client.ui.grid.sort.SortEventHandler;
import com.vaadin.client.ui.grid.sort.SortOrder;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers;

@Connect(GridClientColumnRenderers.GridController.class)
public class GridClientColumnRendererConnector extends
        AbstractComponentConnector {

    public static enum Renderers {
        TEXT_RENDERER, WIDGET_RENDERER, HTML_RENDERER, NUMBER_RENDERER, DATE_RENDERER, CPLX_RENDERER;
    }

    /**
     * Datasource for simulating network latency
     */
    private class DelayedDataSource implements DataSource<String> {

        private DataSource<String> ds;
        private int firstRowIndex = -1;
        private int numberOfRows;
        private DataChangeHandler dataChangeHandler;
        private int latency;

        public DelayedDataSource(DataSource<String> ds, int latency) {
            this.ds = ds;
            this.latency = latency;
        }

        @Override
        public void ensureAvailability(final int firstRowIndex,
                final int numberOfRows) {
            new Timer() {

                @Override
                public void run() {
                    DelayedDataSource.this.firstRowIndex = firstRowIndex;
                    DelayedDataSource.this.numberOfRows = numberOfRows;
                    dataChangeHandler.dataUpdated(firstRowIndex, numberOfRows);
                    dataChangeHandler
                            .dataAvailable(firstRowIndex, numberOfRows);
                }
            }.schedule(latency);
        }

        @Override
        public String getRow(int rowIndex) {
            if (rowIndex >= firstRowIndex
                    && rowIndex <= firstRowIndex + numberOfRows) {
                return ds.getRow(rowIndex);
            }
            return null;
        }

        @Override
        public int getEstimatedSize() {
            return ds.getEstimatedSize();
        }

        @Override
        public void setDataChangeHandler(DataChangeHandler dataChangeHandler) {
            this.dataChangeHandler = dataChangeHandler;
        }

        @Override
        public RowHandle<String> getHandle(String row) {
            // TODO Auto-generated method stub (henrik paul: 17.6.)
            return null;
        }
    }

    @Override
    protected void init() {
        Grid<String> grid = getWidget();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Generated some column data
        List<String> columnData = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            columnData.add(String.valueOf(i));
        }

        // Provide data as data source
        if (Location.getParameter("latency") != null) {
            grid.setDataSource(new DelayedDataSource(
                    new ListDataSource<String>(columnData), Integer
                            .parseInt(Location.getParameter("latency"))));
        } else {
            grid.setDataSource(new ListDataSource<String>(columnData));
        }

        // Add a column to display the data in
        GridColumn<String, String> c = createColumnWithRenderer(Renderers.TEXT_RENDERER);
        grid.addColumn(c);
        grid.getHeader().getDefaultRow().getCell(c).setText("Column 1");

        // Add another column with a custom complex renderer
        c = createColumnWithRenderer(Renderers.CPLX_RENDERER);
        grid.addColumn(c);
        grid.getHeader().getDefaultRow().getCell(c).setText("Column 2");

        // Add method for testing sort event firing
        grid.addSortHandler(new SortEventHandler<String>() {
            @Override
            public void sort(SortEvent<String> event) {
                Element console = Document.get().getElementById(
                        "testDebugConsole");
                String text = "Client-side sort event received<br>"
                        + "Columns: " + event.getOrder().size() + ", order: ";
                for (SortOrder order : event.getOrder()) {
                    String columnHeader = getWidget().getHeader()
                            .getDefaultRow().getCell(order.getColumn())
                            .getText();
                    text += columnHeader + ": "
                            + order.getDirection().toString();
                }
                console.setInnerHTML(text);
            }
        });

        // Handle RPC calls
        registerRpc(GridClientColumnRendererRpc.class,
                new GridClientColumnRendererRpc() {

                    @Override
                    public void addColumn(Renderers renderer) {

                        GridColumn<?, String> column;
                        if (renderer == Renderers.NUMBER_RENDERER) {
                            column = createNumberColumnWithRenderer(renderer);
                        } else if (renderer == Renderers.DATE_RENDERER) {
                            column = createDateColumnWithRenderer(renderer);
                        } else {
                            column = createColumnWithRenderer(renderer);
                        }
                        getWidget().addColumn(column);

                        getWidget()
                                .getHeader()
                                .getDefaultRow()
                                .getCell(column)
                                .setText(
                                        "Column "
                                                + String.valueOf(getWidget()
                                                        .getColumnCount() + 1));
                    }

                    @Override
                    public void detachAttach() {

                        // Detach
                        HasWidgets parent = (HasWidgets) getWidget()
                                .getParent();
                        parent.remove(getWidget());

                        // Re-attach
                        parent.add(getWidget());
                    }

                    @Override
                    public void triggerClientSorting() {
                        getWidget().sort(Sort.by(getWidget().getColumn(0)));
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void triggerClientSortingTest() {
                        Grid<String> grid = getWidget();
                        ListSorter<String> sorter = new ListSorter<String>(grid);

                        // Make sorter sort the numbers in natural order
                        sorter.setComparator(
                                (GridColumn<String, String>) grid.getColumn(0),
                                new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return Integer.parseInt(o1)
                                                - Integer.parseInt(o2);
                                    }
                                });

                        // Sort along column 0 in ascending order
                        grid.sort(grid.getColumn(0));

                        // Remove the sorter once we're done
                        sorter.removeFromGrid();
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void shuffle() {
                        Grid<String> grid = getWidget();
                        ListSorter<String> shuffler = new ListSorter<String>(
                                grid);

                        // Make shuffler return random order
                        shuffler.setComparator(
                                (GridColumn<String, String>) grid.getColumn(0),
                                new Comparator<String>() {
                                    @Override
                                    public int compare(String o1, String o2) {
                                        return com.google.gwt.user.client.Random
                                                .nextInt(3) - 1;
                                    }
                                });

                        // "Sort" (actually shuffle) along column 0
                        grid.sort(grid.getColumn(0));

                        // Remove the shuffler when we're done so that it
                        // doesn't interfere with further operations
                        shuffler.removeFromGrid();
                    }
                });
    }

    /**
     * Creates a a renderer for a {@link Renderers}
     */
    private Renderer createRenderer(Renderers renderer) {
        switch (renderer) {
        case TEXT_RENDERER:
            return new TextRenderer();

        case WIDGET_RENDERER:
            return new WidgetRenderer<String, Button>() {

                @Override
                public Button createWidget() {
                    final Button button = new Button("");
                    button.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            button.setText("Clicked");
                        }
                    });
                    return button;
                }

                @Override
                public void render(FlyweightCell cell, String data,
                        Button button) {
                    button.setHTML(data);
                }
            };

        case HTML_RENDERER:
            return new HtmlRenderer() {

                @Override
                public void render(FlyweightCell cell, String htmlString) {
                    super.render(cell, "<b>" + htmlString + "</b>");
                }
            };

        case NUMBER_RENDERER:
            return new NumberRenderer<Long>();

        case DATE_RENDERER:
            return new DateRenderer();

        case CPLX_RENDERER:
            return new ComplexRenderer<String>() {

                @Override
                public void init(FlyweightCell cell) {
                }

                @Override
                public void render(FlyweightCell cell, String data) {
                    cell.getElement().setInnerHTML("<span>" + data + "</span>");
                    cell.getElement().getStyle().clearBackgroundColor();
                }

                @Override
                public void setContentVisible(FlyweightCell cell,
                        boolean hasData) {

                    // Visualize content visible property
                    cell.getElement().getStyle()
                            .setBackgroundColor(hasData ? "green" : "red");

                    super.setContentVisible(cell, hasData);
                }

                @Override
                public boolean onActivate(Cell cell) {
                    cell.getElement().setInnerHTML("<span>Activated!</span>");
                    return true;
                }
            };

        default:
            return new TextRenderer();
        }
    }

    private GridColumn<String, String> createColumnWithRenderer(
            Renderers renderer) {
        return new GridColumn<String, String>(createRenderer(renderer)) {

            @Override
            public String getValue(String row) {
                return row;
            }
        };
    }

    private GridColumn<Number, String> createNumberColumnWithRenderer(
            Renderers renderer) {
        return new GridColumn<Number, String>(createRenderer(renderer)) {

            @Override
            public Number getValue(String row) {
                return Long.parseLong(row);
            }
        };
    }

    private GridColumn<Date, String> createDateColumnWithRenderer(
            Renderers renderer) {
        return new GridColumn<Date, String>(createRenderer(renderer)) {

            @Override
            public Date getValue(String row) {
                return new Date();
            }
        };
    }

    @Override
    public Grid<String> getWidget() {
        return (Grid<String>) super.getWidget();
    }
}
