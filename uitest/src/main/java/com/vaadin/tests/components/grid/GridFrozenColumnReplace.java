package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridFrozenColumnReplace extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(String::toString).setId("id1").setCaption("Frozen 1");
        grid.addColumn(String::toString).setId("id2").setCaption("Col 2");
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setFrozenColumnCount(1);

        Button button = new Button("Replace columns");
        button.addClickListener(e -> {
            grid.removeAllColumns(); // will change frozenColumnCount to 0

            grid.addColumn(String::toString).setId("id1")
                    .setCaption("New Frozen 1");
            grid.addColumn(String::toString).setId("id2")
                    .setCaption("New Frozen 2");
            grid.addColumn(String::toString).setId("id3")
                    .setCaption("New Col 3");
            grid.setFrozenColumnCount(2);
        });

        addComponents(grid, button);
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to remove and replace columns on same round trip "
                + "even if some of them are frozen.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11824;
    }
}
