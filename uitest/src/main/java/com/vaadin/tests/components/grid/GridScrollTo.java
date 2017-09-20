package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class GridScrollTo extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            data.add("Name " + i);
        }

        Grid<String> grid = new Grid<>();
        grid.setItems(data);

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(ValueProvider.identity()).setId("Name")
                .setCaption("Name");

        grid.setDetailsGenerator(item -> {
            final HorizontalLayout detailsLayout = new HorizontalLayout();
            detailsLayout.setWidth(100, Unit.PERCENTAGE);
            detailsLayout.setHeightUndefined();

            final Label lbl1 = new Label(item + " details");
            detailsLayout.addComponent(lbl1);
            return detailsLayout;
        });

        grid.addItemClickListener(event -> {
            final String itemId = event.getItem();
            grid.setDetailsVisible(itemId, !grid.isDetailsVisible(itemId));
        });

        Button scrollToTop = new Button("Scroll to top",
                clickEvent -> grid.scrollToStart());
        scrollToTop.setId("top");

        Button scrollToEnd = new Button("Scroll to end",
                clickEvent -> grid.scrollToEnd());
        scrollToEnd.setId("end");

        TextField rowField = new TextField("Target row");
        rowField.setId("row-field");

        Button scrollToRow = new Button("Scroll to row", clickEvent -> grid
                .scrollTo(Integer.parseInt(rowField.getValue())));
        scrollToRow.setId("row");

        addComponent(grid);

        addComponent(new HorizontalLayout(scrollToTop, scrollToEnd, rowField,
                scrollToRow));
    }
}
