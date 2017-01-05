package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Grid;

public class GridEditorMultiselect extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();

        grid.addColumn("name");
        grid.addColumn("age", Integer.class);

        for (int i = 0; i < 30; i++) {
            grid.addRow("name " + i, i);
        }

        grid.setEditorEnabled(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17132;
    }

    @Override
    protected String getTestDescription() {
        return "Grid Multiselect: Edit mode allows invalid selection";
    }
}
