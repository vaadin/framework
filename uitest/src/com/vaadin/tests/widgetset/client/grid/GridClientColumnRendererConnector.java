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
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.GridColumn;
import com.vaadin.client.ui.grid.Renderer;
import com.vaadin.client.ui.grid.datasources.ListDataSource;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;
import com.vaadin.client.ui.grid.renderers.DateRenderer;
import com.vaadin.client.ui.grid.renderers.HtmlRenderer;
import com.vaadin.client.ui.grid.renderers.NumberRenderer;
import com.vaadin.client.ui.grid.renderers.TextRenderer;
import com.vaadin.client.ui.grid.renderers.WidgetRenderer;
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
        private int firstRowIndex;
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
    }

    @Override
    protected void init() {
        Grid<String> grid = getWidget();
        grid.setColumnHeadersVisible(false);

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
        grid.addColumn(createColumnWithRenderer(Renderers.TEXT_RENDERER));

        // Handle RPC calls
        registerRpc(GridClientColumnRendererRpc.class,
                new GridClientColumnRendererRpc() {

                    @Override
                    public void addColumn(Renderers renderer) {

                        if (renderer == Renderers.NUMBER_RENDERER) {
                            getWidget().addColumn(
                                    createNumberColumnWithRenderer(renderer));
                        } else if (renderer == Renderers.DATE_RENDERER) {
                            getWidget().addColumn(
                                    createDateColumnWithRenderer(renderer));

                        } else {
                            getWidget().addColumn(
                                    createColumnWithRenderer(renderer));
                        }
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
                    return new Button("", new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            Window.alert("Click");
                        }
                    });
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
