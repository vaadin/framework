package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridUndefinedHeight extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        Grid<String> grid = new Grid<>();
        grid.setItems("Foo", "Bar", "Baz");
        grid.setHeightMode(HeightMode.UNDEFINED);
        grid.addColumn(Object::toString).setCaption("toString()");

        com.vaadin.v7.ui.Grid oldGrid = new com.vaadin.v7.ui.Grid();
        oldGrid.addColumn("toString", String.class);
        oldGrid.addRow("Foo");
        oldGrid.addRow("Bar");
        oldGrid.addRow("Baz");
        oldGrid.setHeightMode(
                com.vaadin.v7.shared.ui.grid.HeightMode.UNDEFINED);

        layout.addComponents(grid, oldGrid, new Button("Add header row", e -> {
            grid.appendHeaderRow();
            oldGrid.appendHeaderRow();
        }));
        layout.setHeight("600px");
        layout.setExpandRatio(grid, 1.0f);
        layout.setExpandRatio(oldGrid, 1.0f);
        addComponent(layout);
    }

}
