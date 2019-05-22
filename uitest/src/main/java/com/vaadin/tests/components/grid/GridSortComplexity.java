package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridSortComplexity extends AbstractTestUIWithLog {

    @Override
    protected int getLogSize() {
        return 10;
    }

    @Override
    protected void setup(VaadinRequest request) {
        List<Integer> data = new ArrayList<>();
        data.add(0);
        data.add(1);
        data.add(2);

        Grid<Integer> grid = new Grid<>();
        grid.addSortListener(event -> {
            log("ON SORT: "
                    + event.getSortOrder().get(0).getDirection().name());
        });

        grid.addColumn(Integer::valueOf).setCaption("ID").setId("id")
                .setSortable(true).setSortProperty("id");

        grid.setDataProvider(DataProvider.fromCallbacks(query -> {
            log("FETCH");
            if (query.getSortOrders().isEmpty() || "ASCENDING".equals(
                    query.getSortOrders().get(0).getDirection().name())) {
                return data.stream();
            } else {
                return data.stream().sorted(Collections.reverseOrder());
            }
        }, query -> {
            log("SIZE");
            return data.size();
        }));

        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("250px");
        grid.setHeightByRows(3);
        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Sorting Grid should not fetch data and recalculate size multiple times.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11532;
    }
}
