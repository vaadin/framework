package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

public class GridOpenDetailsSort extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> testGrid = new Grid<>();

        testGrid.addColumn(item -> item).setCaption("column").setId("column");

        List<String> list = new ArrayList<>();
        list.add("row3");
        list.add("row2");
        list.add("row1");
        ListDataProvider<String> dataProvider = new ListDataProvider<>(list);

        testGrid.setDataProvider(dataProvider);
        testGrid.setDetailsGenerator(item -> new Label("details - " + item));
        list.forEach(item -> testGrid.setDetailsVisible(item, true));

        addComponent(testGrid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 12341;
    }

    @Override
    protected String getTestDescription() {
        return "Already open details rows shouldn't break when the Grid is sorted.";
    }
}
