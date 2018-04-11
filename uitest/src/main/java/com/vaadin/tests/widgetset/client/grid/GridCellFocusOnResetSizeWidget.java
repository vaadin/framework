package com.vaadin.tests.widgetset.client.grid;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.vaadin.client.data.DataChangeHandler;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.renderers.HtmlRenderer;
import com.vaadin.client.widget.grid.selection.SelectionModel;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.Registration;

public class GridCellFocusOnResetSizeWidget
        extends PureGWTTestApplication<Grid<String[]>> {

    private Grid<String[]> grid;

    private final class MyDataSource implements DataSource<String[]> {
        List<String[]> rows = new ArrayList<>();
        int ROWS_MAX = 10;
        int size = ROWS_MAX;
        DataChangeHandler handler = null;
        {
            for (int i = 0; i < ROWS_MAX; ++i) {
                rows.add(new String[] { "Foo " + i });
            }
        }

        @Override
        public void ensureAvailability(int firstRowIndex, int numberOfRows) {
            handler.dataAvailable(firstRowIndex, numberOfRows);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Registration addDataChangeHandler(
                DataChangeHandler dataChangeHandler) {
            handler = dataChangeHandler;
            return null;
        }

        @Override
        public RowHandle<String[]> getHandle(final String[] rowData) {
            return null;
        }

        @Override
        public String[] getRow(int rowIndex) {
            if (rowIndex < size && rowIndex >= 0) {
                return rows.get(rowIndex);
            }
            return null;
        }

        public void changeSize() {
            size--;
            if (size < ROWS_MAX / 2) {
                size = ROWS_MAX;
            }
            handler.resetDataAndSize(size);
        }

        @Override
        public boolean isWaitingForData() {
            return false;
        }
    }

    private class Col extends Grid.Column<String, String[]> {
        public Col(String header) {
            super(header);
            setRenderer(new HtmlRenderer());
        }

        @Override
        public String getValue(String[] row) {
            int index = grid.getColumns().indexOf(this);
            return "<span>" + String.valueOf(row[index]) + "</span>";
        }
    }

    public GridCellFocusOnResetSizeWidget() {
        super(new Grid<String[]>());
        grid = getTestedWidget();
        grid.setSelectionModel(new SelectionModel.NoSelectionModel<>());
        grid.setWidth("300px");
        grid.addColumn(new Col("Foo"));
        final MyDataSource dataSource = new MyDataSource();
        grid.setDataSource(dataSource);
        Button widget = new Button("Change Container Size");
        widget.addClickHandler(event -> dataSource.changeSize());
        addNorth(grid, 400);
        addNorth(widget, 50);
    }
}
