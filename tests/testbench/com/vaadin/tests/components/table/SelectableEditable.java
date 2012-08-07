package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class SelectableEditable extends TestBase {

    @Override
    protected void setup() {
        // TODO Auto-generated method stub

        final Table table = new Table();
        table.setWidth("500px");
        table.setSelectable(true);
        table.setEditable(true);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("alive", Boolean.class, false);
        for (int i = 0; i < 10; ++i) {
            table.addItem(new Object[] { "Person " + i, false }, i);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return "It is difficult to select rows of an editable Table, especially columns with checkboxes.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return 9064;
    }
}
