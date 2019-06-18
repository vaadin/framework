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
public class GridScrollWithoutRows extends AbstractTestUI {

    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        List<Integer> data = new ArrayList<>();

        Grid<Integer> grid = new Grid<>();
        grid.addColumn(Integer::valueOf).setCaption("ID").setId("id");
        grid.addColumn(Integer::valueOf).setCaption("FOO").setId("foo");
        grid.setItems(data);

        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("250px");
        grid.setHeightByRows(3);
        addComponent(grid);

        addComponent(new Button("Add row", e -> {
            data.add(counter);
            ++counter;
            grid.getDataProvider().refreshAll();
        }));
        Button beginningButton = new Button("Scroll to beginning", e -> {
            grid.scrollToStart();
        });
        beginningButton.setId("beginning");
        addComponent(beginningButton);
        Button endButton = new Button("Scroll to end", e -> {
            grid.scrollToEnd();
        });
        endButton.setId("end");
        addComponent(endButton);
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to scroll to beginning or end without assertion errors "
                + "even when there are no rows (requires SuperDevMode).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11558;
    }
}
