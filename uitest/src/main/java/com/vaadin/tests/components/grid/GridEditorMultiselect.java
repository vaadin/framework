package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridEditorMultiselect extends AbstractTestUI {

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
    public String getDescription() {
        return "Grid Multiselect: Edit mode allows invalid selection";
    }
}
