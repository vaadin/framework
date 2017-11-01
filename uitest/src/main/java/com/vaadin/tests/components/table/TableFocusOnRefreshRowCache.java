package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.Table;

public class TableFocusOnRefreshRowCache extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setSizeFull();
        table.addContainerProperty("Name", String.class, null);
        for (int i = 0; i < 200; i++) {
            table.addItem(new Object[] { "Item " + i }, i);
        }

        table.setSelectable(true);
        table.addItemClickListener(event -> table.refreshRowCache());
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Calling Table#refreshRowCache() loses cell focus";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11797;
    }
}
