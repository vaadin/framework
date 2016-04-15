package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class SelectingItemScrollsRight extends TestBase {

    @Override
    protected void setup() {
        Table table = new Table();
        table.setSelectable(true);
        table.setWidth("300px");
        table.setColumnWidth("Column", 500);
        table.addGeneratedColumn("Column", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                return new Label(
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
        });

        for (int i = 0; i < 50; i++) {
            table.addItem();
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Clicking on an item that is longer than the table width should not scroll the table right";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5385;
    }

}
