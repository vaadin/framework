package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridColumnAutoExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        addComponent(layout);

        Grid<String> grid = new Grid<>();
        grid.setCaption("Broken Grid with Caption");
        grid.setWidth("100%");
        grid.setHeight("100px");

        grid.addColumn(ValueProvider.identity()).setCaption("Col1")
                .setWidth(100);
        grid.addColumn(ValueProvider.identity()).setCaption("Col2")
                .setMinimumWidth(100).setExpandRatio(1);

        layout.addComponent(grid);
    }
}
