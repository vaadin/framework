package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * @author Vaadin Ltd
 *
 */
public class GridAriaMultiselectable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.setItems("a", "b");
        grid.setSelectionMode(SelectionMode.NONE);

        addComponent(grid);

        Button singleSelectBtn = new Button("SingleSelect",
                event -> grid.setSelectionMode(SelectionMode.SINGLE));
        addComponent(singleSelectBtn);

        Button multiSelectBtn = new Button("MultiSelect",
                event -> grid.setSelectionMode(SelectionMode.MULTI));
        addComponent(multiSelectBtn);
    }
}
