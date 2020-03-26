package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

public class GridUnhideColumnsWithFrozen extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();
        for (int i = 0; i < 15; i++) {
            String columnId = String.valueOf(i);
            Grid.Column<Integer, Component> column = addColumn(grid, columnId);
            column.setHidable(true);
            if (i == 3 || i == 4) {
                column.setHidden(true);
            }
            column.setCaption(columnId);
            column.setId(columnId);
        }
        grid.setFrozenColumnCount(4);
        grid.setItems(0);
        addComponent(grid);
    }

    private Grid.Column<Integer, Component> addColumn(Grid<Integer> grid,
            String columnId) {
        return grid.addComponentColumn(i -> new Label(columnId));
    }

    @Override
    protected String getTestDescription() {
        return "Columns 0-3 have been set frozen, unhiding column 4 before column 3"
                + " should not make column 4 frozen.";
    }

}
