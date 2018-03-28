package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

/**
 * @author Vaadin Ltd
 *
 */
public class DisallowedDeselection extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.setItems("a", "b");

        GridSelectionModel<String> model = grid
                .setSelectionMode(SelectionMode.SINGLE);
        SingleSelectionModelImpl<?> singleSelectionModel = (SingleSelectionModelImpl<?>) model;
        singleSelectionModel.setDeselectAllowed(false);
        addComponent(grid);

        Button allowDeselection = new Button("Allow deselection",
                event -> singleSelectionModel.setDeselectAllowed(true));

        addComponent(allowDeselection);
    }

}
