package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.DataSource;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridApplyFilterWhenScrolledDown extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();

        grid.addColumn("Name", Function.identity());

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add("Name " + i);
        }

        data.add("Test");
        grid.setItems(data);

        addComponent(grid);
        Button button = new Button("Filter Test item",
                event -> filter(grid.getDataSource(), data));
        addComponent(button);
    }

    private void filter(DataSource<String> dataSource, List<String> data) {
        String last = data.get(data.size() - 1);
        data.clear();
        data.add(last);
        dataSource.refreshAll();
    }

}