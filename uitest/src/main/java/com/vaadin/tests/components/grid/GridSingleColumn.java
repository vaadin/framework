package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridSingleColumn extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.NONE);

        grid.setItems(IntStream.range(0, 100).mapToObj(indx -> "cell"));

        grid.addColumn(ValueProvider.identity()).setCaption("Header");

        addComponent(grid);
        grid.scrollTo(50);
    }

    @Override
    protected String getTestDescription() {
        return "Tests a single column grid";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
