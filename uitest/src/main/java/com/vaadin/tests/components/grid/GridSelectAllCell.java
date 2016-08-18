package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.LegacyGrid;

@Theme("valo")
public class GridSelectAllCell extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        LegacyGrid grid = new LegacyGrid();

        grid.addColumn("foo", String.class);
        grid.addRow("bar");

        grid.setSelectionMode(LegacyGrid.SelectionMode.MULTI);

        addComponent(grid);
    }
}
