package com.vaadin.tests.components.grid;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

import java.util.ArrayList;
import java.util.List;

public class GridHeightByRow extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            data.add("Data " + i);

        Grid<String> grid = new Grid<>();
        grid.addColumn(String::toString).setCaption("Test");
        ListDataProvider<String> provider = DataProvider.ofCollection(data);
        grid.setDataProvider(provider);

        grid.setHeightMode(HeightMode.UNDEFINED);
        grid.setRowHeight(50);

        Button addButton = new Button("Add Data");
        addButton.addClickListener(event -> {
            data.add("Data");
            grid.getDataProvider().refreshAll();
        });

        Button removeButton = new Button("Remove Data");
        removeButton.addClickListener(event -> {
            if (data.isEmpty())
                return;

            data.remove(0);
            grid.getDataProvider().refreshAll();
        });

        addComponents(addButton, removeButton, grid);
    }
}
