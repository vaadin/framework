package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Grid;

public class GridDisabledMultiselect extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.addColumn("foo", String.class);
        grid.addRow("bar");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        addComponent(grid);

        addButton("Multi", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                grid.setSelectionMode(Grid.SelectionMode.MULTI);
            }
        });

        addButton("Disable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                grid.setEnabled(!grid.isEnabled());
            }
        });
    }
}