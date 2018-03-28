package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Table;

public class ColumnReorderingWithManyColumns extends TestBase {

    private static final int NUM_COLS = 16;

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSizeFull();
        table.setColumnReorderingAllowed(true);

        for (int i = 0; i < NUM_COLS; ++i) {
            table.addContainerProperty("col-" + i, String.class, null);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "When reordering columns via drag'n'drop, the drop marker is drawn too far to the right.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10890;
    }
}
