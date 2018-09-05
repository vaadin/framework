package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Table;

public class TableRequiredIndicator extends AbstractReindeerTestUI {

    static final String TABLE = "table";
    static final String COUNT_SELECTED_BUTTON = "button";
    static final int TOTAL_NUMBER_OF_ROWS = 300;
    static final String COUNT_OF_SELECTED_ROWS_LABEL = "label";

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.setId(TABLE);
        table.setSelectable(true);
        table.addContainerProperty("row", String.class, null);
        for (int i = 0; i < TOTAL_NUMBER_OF_ROWS; i++) {
            Object itemId = table.addItem();
            table.getContainerProperty(itemId, "row").setValue("row " + i);
        }
        addComponent(table);

        // This should cause red asterisk to the vertical layout
        table.setRequired(true);

        table.addValueChangeListener(event -> {
            Object value = table.getValue();
            if (value != null) {
                Notification.show("Value is set.");
            } else {
                Notification.show("Value is NOT set.");
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Table is required and should have red asterisk to indicate that.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13008;
    }

}
