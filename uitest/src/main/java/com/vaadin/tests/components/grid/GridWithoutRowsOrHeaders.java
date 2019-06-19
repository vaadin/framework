package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * There is no corresponding TB test as this problem can only be reproduced
 * using SuperDevMode.
 */
public class GridWithoutRowsOrHeaders extends AbstractTestUI {

    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        List<Integer> data = new ArrayList<>();

        Grid<Integer> grid = new Grid<>();
        grid.addColumn(Integer::valueOf).setCaption("ID").setId("id")
                .setMaximumWidth(50d);
        grid.addColumn(Integer::valueOf).setCaption("FOO").setId("foo")
                .setMinimumWidth(50d);
        grid.removeHeaderRow(grid.getHeaderRow(0));
        grid.setItems(data);

        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("250px");
        grid.setHeightByRows(3);
        addComponent(grid);

        addComponent(new Button("Add header row", e -> {
            grid.addHeaderRowAt(0);
        }));
        addComponent(new Button("Add body row", e -> {
            data.add(counter);
            ++counter;
            grid.getDataProvider().refreshAll();
        }));
        addComponent(new Button("Add footer row", e -> {
            grid.addFooterRowAt(0);
        }));
    }

    @Override
    protected String getTestDescription() {
        return "There should be no client-side assertion error from "
                + "adding the Grid without contents (requires SuperDevMode).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11557;
    }
}
