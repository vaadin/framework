package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.ResizeTerrorizer;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

@Widgetset(TestingWidgetSet.NAME)
public class GridResizeTerror extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();

        IntStream.range(0, 10).forEach(i -> grid.addColumn(item -> "Data" + i));

        grid.setItems(IntStream.range(0, 500).boxed());

        ResizeTerrorizer terrorizer = new ResizeTerrorizer(grid);
        setContent(terrorizer);
    }
}
