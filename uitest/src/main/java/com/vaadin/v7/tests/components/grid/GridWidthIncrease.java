package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.Grid;

public class GridWidthIncrease extends AbstractReindeerTestUI {

    public static int COLUMN_COUNT = 5;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        Object[] rowData = new String[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; ++i) {
            grid.addColumn("Column " + i, String.class);
            rowData[i] = "Foo (0, " + i + ")";
        }
        grid.addRow(rowData);
        grid.setWidth(400 + "px");
        addComponent(grid);
        addComponent(
                new Button("Increase Grid Width", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.setWidth((grid.getWidth() + 50) + "px");
                    }
                }));
    }
}
