package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridColumnWidthWithoutHeader extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity()).setExpandRatio(1)
                .setMinimumWidth(250);
        grid.addColumn(String::valueOf).setWidth(150);
        grid.setItems("a", "b");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();

        grid.removeHeaderRow(0);
        addComponent(grid);
    }
}
