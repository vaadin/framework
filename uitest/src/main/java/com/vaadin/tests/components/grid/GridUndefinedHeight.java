package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridUndefinedHeight extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.setItems("Foo", "Bar", "Baz");
        grid.setHeightMode(HeightMode.UNDEFINED);
        grid.addColumn(Object::toString).setCaption("toString()");
        addComponent(grid);
    }

}
