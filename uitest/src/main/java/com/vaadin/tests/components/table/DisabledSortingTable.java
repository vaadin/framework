package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;

public class DisabledSortingTable extends AbstractReindeerTestUI {

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();

        table.addContainerProperty("header1", String.class, "column1");
        table.addContainerProperty("header2", String.class, "column2");
        table.addContainerProperty("header3", String.class, "column3");

        for (int row = 0; row < 5; row++) {
            Object key = table.addItem();
            table.getItem(key).getItemProperty("header1")
                    .setValue(String.valueOf(row));
            table.getItem(key).getItemProperty("header2")
                    .setValue(String.valueOf(5 - row));
        }

        addComponent(table);

        addButton("Enable sorting", event -> table.setSortEnabled(true));

        addButton("Disable sorting", event -> table.setSortEnabled(false));

        addButton("Sort by empty array",
                event -> table.sort(new Object[] {}, new boolean[] {}));
    }

    @Override
    public String getTestDescription() {
        return "Sorting with empty arrays should hide sorting indicator but not reset sorting in Table with default container.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16563;
    }
}
