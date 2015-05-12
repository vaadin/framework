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
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.renderers.TextRenderer;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.SelectionMode;

public class GridClientDataSourcesWidget extends
        PureGWTTestApplication<Grid<String[]>> {

    private interface RestCallback {
        void onResponse(RestishDataSource.Backend.Result result);
    }

    /**
     * This is an emulated datasource that has a back-end that changes size
     * constantly. The back-end is unable to actively push data to Grid.
     * Instead, with each row request, in addition to its row payload it tells
     * how many rows it contains in total.
     * 
     * A plausible response from this REST-like api would be:
     * 
     * <pre>
     * <code>
     * GET /foos/4..8
     * 
     * {
     *     "resultsize": 4,
     *     "data": [
     *         [4, "foo IV"],
     *         [5, "foo V"],
     *         [6, "foo VI"]
     *         [7, "foo VII"]
     *     ],
     *     "totalrows": 100
     * }
     * </code>
     * </pre>
     * 
     * In this case, the size of Grid needs to be updated to be able to show 100
     * rows in total (no more, no less).
     * 
     * This class
     * <ol>
     * <li>gets initialized
     * <li>asks for its size
     * <li>updates Grid once the reply is received
     * <li>as the Grid fetches more data, the total row count is dynamically
     * updated.
     * </ol>
     */
    private class RestishDataSource extends AbstractRemoteDataSource<String[]> {
        /**
         * Pretend like this class doesn't exist. It just simulates a backend
         * somewhere.
         * <p>
         * It's scoped inside the RDS class only because it's tied to that.
         * */
        private class Backend {
            public class Result {
                public int size;
                public List<String[]> rows;
            }

            private int size = 200;
            private int modCount = 0;

            public void query(int firstRowIndex, int numberOfRows,
                    final RestCallback callback) {
                final Result result = new Result();
                result.size = size;
                result.rows = fetchRows(firstRowIndex, numberOfRows);

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        callback.onResponse(result);
                    }
                });

            }

            private List<String[]> fetchRows(int firstRowIndex, int numberOfRows) {
                List<String[]> rows = new ArrayList<String[]>();
                for (int i = 0; i < numberOfRows; i++) {
                    String id = String.valueOf(firstRowIndex + i);
                    rows.add(new String[] { id, "cell " + id + " #" + modCount });
                }
                return rows;
            }

            public void pushRowChanges(int rows) {
                size += rows;
                pushRowChanges();
            }

            public void pushRowChanges() {
                modCount++;

                // push "something happened" to datasource "over the wire":
                resetDataAndSize(size);
            }

            public void addRows(int rowcount) {
                modCount++;
                size += rowcount;
            }
        }

        final Backend backend;

        public RestishDataSource() {
            backend = new Backend();
        }

        @Override
        protected void requestRows(int firstRowIndex, int numberOfRows,
                final RequestRowsCallback<String[]> callback) {

            backend.query(firstRowIndex, numberOfRows, new RestCallback() {
                @Override
                public void onResponse(Backend.Result result) {
                    callback.onResponse(result.rows, result.size);
                }
            });
        }

        @Override
        public Object getRowKey(String[] row) {
            return row[0];
        }
    }

    private final Grid<String[]> grid;

    private RestishDataSource restishDataSource;

    private final ScheduledCommand setRestishCommand = new ScheduledCommand() {
        @Override
        public void execute() {
            for (Grid.Column<?, String[]> column : grid.getColumns()) {
                grid.removeColumn(column);
            }

            restishDataSource = new RestishDataSource();
            grid.setDataSource(restishDataSource);
            grid.addColumn(new Grid.Column<String, String[]>("column",
                    new TextRenderer()) {

                @Override
                public String getValue(String[] row) {
                    return row[1];
                }
            });
        }
    };

    public GridClientDataSourcesWidget() {
        super(new Grid<String[]>());
        grid = getTestedWidget();

        grid.getElement().getStyle().setZIndex(0);
        grid.setHeight("400px");
        grid.setSelectionMode(SelectionMode.NONE);
        addNorth(grid, 400);

        addMenuCommand("Use", setRestishCommand, "DataSources", "RESTish");
        addMenuCommand("Next request +10", new ScheduledCommand() {
            @Override
            public void execute() {
                restishDataSource.backend.addRows(10);
            }
        }, "DataSources", "RESTish");
        addMenuCommand("Next request -10", new ScheduledCommand() {
            @Override
            public void execute() {
                restishDataSource.backend.addRows(-10);
            }
        }, "DataSources", "RESTish");
        addMenuCommand("Push data change", new ScheduledCommand() {
            @Override
            public void execute() {
                restishDataSource.backend.pushRowChanges();
            }
        }, "DataSources", "RESTish");
        addMenuCommand("Push data change +10", new ScheduledCommand() {
            @Override
            public void execute() {
                restishDataSource.backend.pushRowChanges(10);
            }
        }, "DataSources", "RESTish");
        addMenuCommand("Push data change -10", new ScheduledCommand() {
            @Override
            public void execute() {
                restishDataSource.backend.pushRowChanges(-10);
            }
        }, "DataSources", "RESTish");
    }
}
