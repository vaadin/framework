package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

/**
 * Test UI for Grid initial rendering performance profiling.
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridManyColumns extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.setSizeFull();
        for (int i = 0; i < 80; i++) {
            grid.addColumn(row -> "novalue").setCaption("Column_" + i)
                    .setWidth(200);
        }
        grid.setItems(IntStream.range(0, 10).boxed().map(i -> ""));
        addComponent(grid);
    }
}
