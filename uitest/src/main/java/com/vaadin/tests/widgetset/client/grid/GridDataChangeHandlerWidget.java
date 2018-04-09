package com.vaadin.tests.widgetset.client.grid;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.client.widgets.Grid;

public class GridDataChangeHandlerWidget extends Composite {

    private final SimplePanel panel = new SimplePanel();
    private final Grid<String> grid = new Grid<>();

    public static class DelayedDataSource extends ListDataSource<String> {

        public DelayedDataSource(List<String> datasource) {
            super(datasource);
        }

        @Override
        public void ensureAvailability(final int firstRowIndex,
                final int numberOfRows) {
            new Timer() {

                @Override
                public void run() {
                    actualEnsureAvailability(firstRowIndex, numberOfRows);
                }
            }.schedule(500);
        }

        private void actualEnsureAvailability(int firstRowIndex,
                int numberOfRows) {
            super.ensureAvailability(firstRowIndex, numberOfRows);
        }
    }

    public static class RemoteDelayedDataSource
            extends AbstractRemoteDataSource<String> {

        private List<String> rows;

        public RemoteDelayedDataSource(List<String> datasource) {
            super();
            rows = datasource;
            // Make Grid request not exceed actual size.
            resetDataAndSize(rows.size());
        }

        @Override
        protected void requestRows(final int firstRowIndex,
                final int numberOfRows,
                final RequestRowsCallback<String> callback) {
            new Timer() {

                @Override
                public void run() {
                    List<String> requested = rows.subList(firstRowIndex,
                            numberOfRows);
                    callback.onResponse(requested, requested.size());
                }
            }.schedule(500);
        }

        @Override
        public Object getRowKey(String row) {
            return row;
        }
    }

    public GridDataChangeHandlerWidget() {
        initWidget(panel);

        panel.setWidget(grid);
        grid.setDataSource(new RemoteDelayedDataSource(
                Arrays.asList("A", "B", "C", "D", "E")));
        grid.addColumn(new Grid.Column<String, String>("letter") {
            @Override
            public String getValue(String row) {
                return row;
            }
        });
        Scheduler.get().scheduleFinally(new RepeatingCommand() {

            boolean run = false;

            @Override
            public boolean execute() {
                grid.setDataSource(
                        new DelayedDataSource(Arrays.asList("X", "Y", "Z")));
                if (run) {
                    return false;
                }
                run = true;
                return true;
            }
        });
    }
}
