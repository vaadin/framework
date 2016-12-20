package com.vaadin.tests.widgetset.client.v7.grid;

import java.util.Arrays;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.v7.client.widget.grid.datasources.ListDataSource;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.widgets.Grid.Column;
import com.vaadin.v7.shared.ui.grid.HeightMode;

public class GridHeightByRowOnInitWidget extends Composite {
    private final SimplePanel panel = new SimplePanel();
    private final Grid<String> grid = new Grid<>();

    public GridHeightByRowOnInitWidget() {
        initWidget(panel);

        panel.setWidget(grid);
        grid.setDataSource(
                new ListDataSource<>(Arrays.asList("A", "B", "C", "D", "E")));
        grid.addColumn(new Column<String, String>("letter") {
            @Override
            public String getValue(String row) {
                return row;
            }
        });

        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(5.0d);
    }
}
