package com.vaadin.tests.smoke;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;

public class GridSmoke extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Grid grid = new Grid();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addColumn("firstName");
        grid.addColumn("age", Integer.class);

        grid.addRow("Lorem", Integer.valueOf(1));
        grid.addRow("Ipsum", Integer.valueOf(2));

        addComponent(grid);

        addComponent(new Button("Add new row",
                event -> grid.addRow("Dolor", Integer.valueOf(3))));
    }

}
