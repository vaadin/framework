package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;

public class GridSelectAllStatus extends AbstractTestUI {

    public Grid<String> grid;

    @Override
    protected void setup(VaadinRequest request) {
        grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.setItems("Item 1", "Item 2");
        grid.addColumn(item -> item);

        Label label = new Label("Select-all checkbox is checked?");
        Label selectAllStatus = new Label(
                String.valueOf(grid.asMultiSelect().isAllSelected()));
        selectAllStatus.setId("status");

        grid.asMultiSelect()
                .addMultiSelectionListener(e -> selectAllStatus.setValue(
                        String.valueOf(grid.asMultiSelect().isAllSelected())));

        addComponents(grid, label, selectAllStatus);
    }

    @Override
    protected Integer getTicketNumber() {
        return 12081;
    }

    @Override
    protected String getTestDescription() {
        return "The status of the Grid's select-all checkbox should be "
                + "accessible through the Java API.";
    }
}
