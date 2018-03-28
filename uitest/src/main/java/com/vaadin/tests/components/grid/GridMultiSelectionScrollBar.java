package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridMultiSelectionScrollBar extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(string -> "X:" + string).setWidth(39.25d);
        grid.addColumn(string -> "Hello:" + string);
        grid.addColumn(string -> "World:" + string);
        grid.setFrozenColumnCount(1);
        addComponent(grid);
    }

}
