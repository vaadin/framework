package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridAddColumn extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.addColumn(String::length, new NumberRenderer());
        grid.addColumn(String::length);
        grid.addColumn(string -> -string.length());
        grid.addColumn(string -> new Object());
        grid.setItems("a", "aa", "aaa");
        addComponent(grid);
    }
}
