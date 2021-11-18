package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridReorderMerged extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        List<String> items = new ArrayList<>();
        HeaderRow headerRow = grid.prependHeaderRow();
        for (int i = 1; i < 10; ++i) {
            String propertyId = "" + i;
            Column<String, ?> column = grid
                    .addColumn(item -> propertyId.equals(item) ? item : "")
                    .setId(propertyId).setCaption(propertyId);
            column.setHidable(true);
            if (i == 5) {
                column.setHidden(true);
            }
            items.add(propertyId);
        }
        grid.setItems(items);
        headerRow.join("1", "2", "3").setText("1");
        headerRow.join("4", "5", "6").setText("2"); // middle column hidden
        headerRow.join("7", "8", "9").setText("3");
        grid.setColumnReorderingAllowed(true);
        addComponent(grid);
    }

    @Override
    protected String getTestDescription() {
        return "Reordering columns should respect joined cells "
                + "even when some columns are hidden.";
    }
}
