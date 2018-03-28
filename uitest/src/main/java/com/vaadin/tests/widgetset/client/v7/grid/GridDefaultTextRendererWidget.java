package com.vaadin.tests.widgetset.client.v7.grid;

import com.vaadin.v7.client.widget.grid.datasources.ListDataSource;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.Column;
import com.vaadin.v7.client.widgets.Grid.SelectionMode;
import com.vaadin.v7.shared.ui.grid.HeightMode;

public class GridDefaultTextRendererWidget
        extends PureGWTTestApplication<Grid<String>> {
    /*
     * This can't be null, because grid thinks that a row object of null means
     * "data is still being fetched".
     */
    private static final String NULL_STRING = "";

    private Grid<String> grid;

    public GridDefaultTextRendererWidget() {
        super(new Grid<String>());
        grid = getTestedWidget();

        grid.setDataSource(new ListDataSource<>(NULL_STRING, "string"));
        grid.addColumn(new Column<String, String>() {
            @Override
            public String getValue(String row) {
                if (!NULL_STRING.equals(row)) {
                    return row;
                } else {
                    return null;
                }
            }
        });

        grid.addColumn(new Column<String, String>() {

            @Override
            public String getValue(String row) {
                return "foo";
            }

        });

        grid.setHeightByRows(2);
        grid.setHeightMode(HeightMode.ROW);
        grid.setSelectionMode(SelectionMode.NONE);
        addNorth(grid, 500);
    }
}
