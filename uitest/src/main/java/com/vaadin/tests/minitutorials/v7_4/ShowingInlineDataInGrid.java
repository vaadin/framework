package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

public class ShowingInlineDataInGrid extends UI {

    @Override
    protected void init(VaadinRequest request) {
        final Grid grid = new Grid();

        grid.addColumn("Name").setSortable(true);
        grid.addColumn("Score", Integer.class);

        grid.addRow("Alice", 15);
        grid.addRow("Bob", -7);
        grid.addRow("Carol", 8);
        grid.addRow("Dan", 0);
        grid.addRow("Eve", 20);

        grid.select(2);

        grid.setHeightByRows(grid.getContainerDataSource().size());
        grid.setHeightMode(HeightMode.ROW);

        setContent(grid);
    }
}
