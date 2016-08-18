package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.Grid;

@Theme("valo")
public class GridSelectAllCell extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();

        grid.addColumn("foo", String.class);
        grid.addRow("bar");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        addComponent(grid);
    }
}
