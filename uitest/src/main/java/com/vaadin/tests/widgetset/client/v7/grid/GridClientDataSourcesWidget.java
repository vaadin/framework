package com.vaadin.tests.widgetset.client.v7.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.v7.client.renderers.TextRenderer;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.SelectionMode;

public class GridClientDataSourcesWidget
        extends PureGWTTestApplication<Grid<String[]>> {

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
         */
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

                Scheduler.get()
                        .scheduleDeferred(() -> callback.onResponse(result));
            }

            private List<String[]> fetchRows(int firstRowIndex,
                    int numberOfRows) {
                List<String[]> rows = new ArrayList<>();
                for (int i = 0; i < numberOfRows; i++) {
                    String id = String.valueOf(firstRowIndex + i);
                    rows.add(new String[] { id,
                            "cell " + id + " #" + modCount });
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

            backend.query(firstRowIndex, numberOfRows,
                    result -> callback.onResponse(result.rows, result.size));
        }

        @Override
        public Object getRowKey(String[] row) {
            return row[0];
        }
    }

    private final Grid<String[]> grid;

    private RestishDataSource restishDataSource;

    public GridClientDataSourcesWidget() {
        super(new Grid<String[]>());
        grid = getTestedWidget();

        grid.getElement().getStyle().setZIndex(0);
        grid.setHeight("400px");
        grid.setSelectionMode(SelectionMode.NONE);
        addNorth(grid, 400);

        addMenuCommand("Use", () -> {
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
        }, "DataSources", "RESTish");

        addMenuCommand("Next request +10",
                () -> restishDataSource.backend.addRows(10), "DataSources",
                "RESTish");
        addMenuCommand("Next request -10",
                () -> restishDataSource.backend.addRows(-10), "DataSources",
                "RESTish");
        addMenuCommand("Push data change",
                () -> restishDataSource.backend.pushRowChanges(), "DataSources",
                "RESTish");
        addMenuCommand("Push data change +10",
                () -> restishDataSource.backend.pushRowChanges(10),
                "DataSources", "RESTish");
        addMenuCommand("Push data change -10",
                () -> restishDataSource.backend.pushRowChanges(-10),
                "DataSources", "RESTish");
    }
}
