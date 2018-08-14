package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridEditorScrollSync extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Create a grid
        Grid grid = new Grid();
        grid.setEditorEnabled(true);
        grid.setEditorBuffered(false);

        // Define some columns
        grid.addColumn("name", String.class);
        grid.addColumn("born", Integer.class);

        grid.addColumn("name1", String.class);
        grid.addColumn("born1", Integer.class);

        grid.addColumn("name2", String.class);
        grid.addColumn("born2", Integer.class);

        grid.addColumn("name3", String.class);
        grid.addColumn("born3", Integer.class);

        grid.addColumn("name4", String.class);
        grid.addColumn("born4", Integer.class);

        grid.setWidth("450px");

        // Add some data rows
        grid.addRow("Nicolaus Copernicus", 1543, "Nicolaus Copernicus", 1543,
                "Nicolaus Copernicus", 1543, "Nicolaus Copernicus", 1543,
                "Nicolaus Copernicus", 1543);

        grid.addRow("Galileo Galilei", 1564, "Galileo Galilei", 1564,
                "Galileo Galilei", 1564, "s", 55, "Nicolaus Copernicus", 1543);

        grid.addRow("Johannes Kepler", 1571, "Johannes Kepler", 1571,
                "Johannes Kepler", 1571, "Nicolaus Copernicus", 1543,
                "Nicolaus Copernicus", 1543);

        getLayout().addComponent(grid);
    }

}
