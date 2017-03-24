package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridAddColumn extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        Column<String, String> col0 = grid.addColumn(ValueProvider.identity())
                .setCaption("First column");
        grid.getDefaultHeaderRow().getCell(col0)
                .setComponent(new Label("Label Header"));
        grid.addColumn(String::length, new NumberRenderer());
        grid.addColumn(String::length);
        grid.addColumn(string -> -string.length());
        grid.addColumn(string -> new Object());
        grid.setItems("a", "aa", "aaa");
        addComponent(grid);
    }
}
