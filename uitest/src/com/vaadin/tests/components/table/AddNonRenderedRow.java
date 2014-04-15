package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

public class AddNonRenderedRow extends TestBase {
    int index = 0;

    private final Table table = new Table();

    @Override
    public void setup() {

        table.setPageLength(5);
        table.addContainerProperty("rowID", Integer.class, null);
        for (int i = 0; i < 4; i++) {
            addRow();
        }

        Button addrowButton = new Button("Add row");
        addrowButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent pEvent) {
                addRow();
            }
        });

        addComponent(table);
        addComponent(addrowButton);
    }

    private void addRow() {
        table.addItem(new Object[] { Integer.valueOf(++index) }, null);
        // table.refreshRowCache();
    }

    @Override
    protected String getDescription() {
        return "Adding a row to the table should work even when the added rows are not visible.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8077);
    }
}
