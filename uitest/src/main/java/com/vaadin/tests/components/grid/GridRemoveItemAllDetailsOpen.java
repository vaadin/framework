package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GridRemoveItemAllDetailsOpen extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<String> data = new ArrayList<>(
                Arrays.asList("row1", "row2", "row3", "row4"));
        Grid<String> grid = createGrid();

        ListDataProvider<String> dataProvider = new ListDataProvider<>(data);
        grid.setDataProvider(dataProvider);
        data.forEach(item -> grid.setDetailsVisible(item, true));

        Button removeBtn = new Button("Remove selected item");
        removeBtn.addClickListener(event -> {
            data.remove(grid.getSelectedItems().iterator().next());
            dataProvider.refreshAll();
            grid.deselectAll();
        });
        addComponent(removeBtn);
        addComponent(grid);
    }

    private Grid<String> createGrid() {
        Grid<String> grid = new Grid<>();
        grid.setHeight("400px");
        grid.addColumn(item -> item).setCaption("column").setId("column");
        grid.setDetailsGenerator(item -> {
            Button closeBtn = new Button("Close");
            closeBtn.addClickListener(
                    clickEvent -> grid.setDetailsVisible(item, false));
            return new HorizontalLayout(new Label("Item details: " + item),
                    closeBtn);
        });
        grid.addItemClickListener(
                itemClick -> grid.setDetailsVisible(itemClick.getItem(), true));
        return grid;
    }

    @Override
    protected Integer getTicketNumber() {
        return 12328;
    }

    @Override
    protected String getTestDescription() {
        return "Removing selected item (first or second)"
                + "should not cause a client side exception.";
    }
}
