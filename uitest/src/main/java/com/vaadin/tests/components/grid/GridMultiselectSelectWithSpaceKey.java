package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridMultiselectSelectWithSpaceKey extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<String> grid = new Grid<>();
        grid.setItems("Foo 1", "Foo 2", "Foo 3", "Foo 4");
        grid.addColumn(item -> item);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        addComponent(grid);
    }
}
