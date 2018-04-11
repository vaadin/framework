package com.vaadin.v7.tests.components.grid.basicfeatures;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

@Title("Server Grid height by row on init")
public class GridHeightByRowOnInit extends UI {

    private static final String PROPERTY = "Property";

    @Override
    protected void init(VaadinRequest request) {
        final Grid grid = new Grid();
        Container.Indexed container = grid.getContainerDataSource();
        container.addContainerProperty(PROPERTY, String.class, "");

        container.addItem("A").getItemProperty(PROPERTY).setValue("A");
        container.addItem("B").getItemProperty(PROPERTY).setValue("B");
        container.addItem("C").getItemProperty(PROPERTY).setValue("C");
        container.addItem("D").getItemProperty(PROPERTY).setValue("D");
        container.addItem("E").getItemProperty(PROPERTY).setValue("E");

        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(5);

        setContent(grid);
    }
}
