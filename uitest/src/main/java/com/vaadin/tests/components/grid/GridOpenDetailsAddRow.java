package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridOpenDetailsAddRow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> testGrid = new Grid<>();

        testGrid.addColumn(item -> item).setCaption("column").setId("column");

        List<String> list = new ArrayList<>();
        list.add("row1");
        list.add("row2");
        list.add("row3");
        ListDataProvider<String> dataProvider = new ListDataProvider<>(list);

        testGrid.setDataProvider(dataProvider);
        testGrid.setDetailsGenerator(item -> new Label("details - " + item));
        list.forEach(item -> testGrid.setDetailsVisible(item, true));

        Button addButton = new Button("add");
        addButton.addClickListener(event -> {
            String newItem = "row" + (list.size() + 1);
            list.add(newItem);
            testGrid.setDetailsVisible(newItem, true);
            dataProvider.refreshAll();
        });

        VerticalLayout testLayout = new VerticalLayout(addButton, testGrid);
        addComponent(testLayout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 12106;
    }

    @Override
    protected String getTestDescription() {
        return "Already open details rows shouldn't disappear when a new row is added";
    }
}
