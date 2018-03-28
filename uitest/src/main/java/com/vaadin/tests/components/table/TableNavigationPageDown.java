package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.Table;

public class TableNavigationPageDown extends AbstractReindeerTestUI {

    private static final int ROW_NUMBER = 50;

    @Override
    protected void setup(VaadinRequest req) {
        Table table = new Table();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setHeight("150px");

        table.addContainerProperty("num", Integer.class, "num");
        table.addContainerProperty("Foo", String.class, "Foov");
        table.addContainerProperty("Bar", String.class, "Barv");

        for (int i = 0; i < ROW_NUMBER; i++) {
            Object key = table.addItem();
            table.getItem(key).getItemProperty("num").setValue(i);
        }

        addComponent(table);

    }

    @Override
    protected String getTestDescription() {
        return "Navigation in Table with PageDown/PageUp/Home/End keys should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15332;
    }

}
