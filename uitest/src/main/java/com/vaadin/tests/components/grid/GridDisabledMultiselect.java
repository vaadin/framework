package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridDisabledMultiselect extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<String> grid = new Grid<>();
        grid.addColumn(string -> string);
        grid.setItems("bar");
        addComponent(grid);

        addButton("Multi", event -> grid.setSelectionMode(SelectionMode.MULTI));

        addButton("Disable", event -> grid.setEnabled(!grid.isEnabled()));
    }
}
