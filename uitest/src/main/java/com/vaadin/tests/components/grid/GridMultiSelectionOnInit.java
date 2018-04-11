package com.vaadin.tests.components.grid;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;

public class GridMultiSelectionOnInit extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<String> grid = new Grid<>();
        grid.setItems("Foo 1", "Foo 2");
        grid.addColumn(item -> item);
        MultiSelectionModelImpl<String> selectionModel = (MultiSelectionModelImpl<String>) grid
                .setSelectionMode(SelectionMode.MULTI);

        addComponent(grid);

        addComponent(new Button("Select rows",
                event -> grid.getSelectionModel().select("Foo 1")));
        if (request.getParameter("initialSelection") != null) {
            grid.getSelectionModel().select("Foo 2");
        }

        RadioButtonGroup<SelectAllCheckBoxVisibility> rbg = new RadioButtonGroup<>(
                "Select All Visible",
                Arrays.asList(SelectAllCheckBoxVisibility.VISIBLE,
                        SelectAllCheckBoxVisibility.HIDDEN,
                        SelectAllCheckBoxVisibility.DEFAULT));
        rbg.setValue(selectionModel.getSelectAllCheckBoxVisibility());
        rbg.addValueChangeListener(event -> selectionModel
                .setSelectAllCheckBoxVisibility(event.getValue()));
        addComponent(rbg);
    }
}
