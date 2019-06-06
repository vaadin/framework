package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class CheckboxAlignmentWithNoHeaderGrid extends AbstractTestUI {

    List<String> items = new ArrayList<>();
    int count = 1;

    @Override
    protected void setup(VaadinRequest request) {

        VerticalLayout lay = new VerticalLayout();

        Grid<String> grid = new Grid<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setHeaderVisible(false);
        grid.addColumn(Object::toString);

        grid.setItems(items);

        lay.addComponent(grid);
        lay.addComponent(new Button("add", e -> {
            items.add("ABCDEFG" + count);
            grid.getDataProvider().refreshAll();
            count++;
        }));
        addComponent(lay);

    }

    @Override
    protected String getTestDescription() {
        return "Rows added to empty grid with multiselect and no header should not break ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11607;
    }

}
