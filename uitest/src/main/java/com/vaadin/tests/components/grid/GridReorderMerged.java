package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderRow;

public class GridReorderMerged extends AbstractTestUI {

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        HeaderRow headerRow = grid.prependHeaderRow();
        for (int i = 1; i < 10; ++i) {
            String propertyId = "" + i;
            Column column = grid.addColumn(propertyId);
            column.setHidable(true);
            if (i == 5) {
                column.setHidden(true);
            }
            // add one value per row for easier visualization
            grid.getContainerDataSource().addItem(i).getItemProperty(propertyId)
                    .setValue(propertyId);
        }
        headerRow.join("1", "2", "3").setText("1");
        headerRow.join("4", "5", "6").setText("2"); // middle column hidden
        headerRow.join("7", "8", "9").setText("3");
        grid.setColumnReorderingAllowed(true);
        addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 12377;
    }

    @Override
    protected String getTestDescription() {
        return "Reordering columns should respect joined cells "
                + "even when some columns are hidden.";
    }
}
