package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridColumnShrinkSmallerThanContents extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createGrid(true));
        addComponent(createGrid(false));
    }

    private Component createGrid(boolean minimumWidthFromContent) {
        Grid<Object> grid = new Grid<>();
        grid.addColumn(item -> "Contents in column 1");
        grid.addColumn(
                item -> "Contents in column 2. Contents in column 2. Contents in column 2. Contents in column 2. Contents in column 2.")
                .setExpandRatio(1)
                .setMinimumWidthFromContent(minimumWidthFromContent);
        grid.setItems(new Object(), new Object());
        grid.setWidth("500px");
        return grid;
    }

}
