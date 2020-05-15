package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.minitutorials.v7_4.GridExampleHelper;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.MultiSelectionModel;
import com.vaadin.v7.ui.Grid.SelectionMode;

public class CompatibilityGridToggleMultiSelectSort extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // container with at least 100 rows
        final Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setSelectionMode(SelectionMode.MULTI);
        addComponent(grid);

        Button button = new Button("Toggle multi-select", e -> {
            if (grid.getSelectionModel() instanceof MultiSelectionModel) {
                grid.setSelectionMode(SelectionMode.SINGLE);
            } else {
                grid.setSelectionMode(SelectionMode.MULTI);
            }
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Toggling multi-select off should not break sorting "
                + "first column to both directions.";
    }
}
