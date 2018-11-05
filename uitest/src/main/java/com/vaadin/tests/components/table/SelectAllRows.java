package com.vaadin.tests.components.table;

import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Table;

public class SelectAllRows extends AbstractReindeerTestUI {

    static final String TABLE = "table";
    static final String COUNT_SELECTED_BUTTON = "button";
    static final int TOTAL_NUMBER_OF_ROWS = 300;
    static final String COUNT_OF_SELECTED_ROWS_LABEL = "label";

    @Override
    protected void setup(VaadinRequest request) {

        final Table table = new Table();
        table.setId(TABLE);
        table.setImmediate(true);
        table.setMultiSelect(true);
        table.setSelectable(true);
        table.addContainerProperty("row", String.class, null);
        addComponent(table);

        Button button = new Button("Count");
        button.setId(COUNT_SELECTED_BUTTON);
        addComponent(button);

        final Label label = new Label();
        label.setId(COUNT_OF_SELECTED_ROWS_LABEL);
        label.setCaption("Selected count:");
        addComponent(label);

        button.addClickListener(event -> {
            Set selected = (Set) table.getValue();
            label.setValue(String.valueOf(selected.size()));
        });

        for (int i = 0; i < TOTAL_NUMBER_OF_ROWS; i++) {
            Object itemId = table.addItem();
            table.getContainerProperty(itemId, "row").setValue("row " + i);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Selecting all rows does not work by selecting first row, press shift then select last row";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13008;
    }

}
