package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridMultiSelectEmpty extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(t -> t).setCaption("String");
        grid.setSelectionMode(SelectionMode.MULTI);
        MultiSelectionModel<String> selectionModel = (MultiSelectionModel<String>) grid
                .getSelectionModel();
        selectionModel.setSelectAllCheckBoxVisibility(
                SelectAllCheckBoxVisibility.HIDDEN);

        List<String> items = new ArrayList<>();
        ListDataProvider<String> dataProvider = DataProvider
                .ofCollection(items);
        grid.setDataProvider(dataProvider);

        addComponent(grid);
        addComponent(new Button("Add Row", e -> {
            items.add("Foo!");
            dataProvider.refreshAll();
        }));
        addComponent(new Button("Recalculate", e -> {
            grid.recalculateColumnWidths();
        }));
    }
}
