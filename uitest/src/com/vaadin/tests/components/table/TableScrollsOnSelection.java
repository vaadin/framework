package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

public class TableScrollsOnSelection extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setSizeUndefined();

        IndexedContainer cont = new IndexedContainer();
        cont.addContainerProperty("number", String.class, null);
        for (int i = 0; i < 80; i++) {
            Item item = cont.addItem(i);
            item.getItemProperty("number").setValue(i + "");
        }
        Table table = new Table();
        table.setPageLength(0);
        table.setContainerDataSource(cont);
        table.setSelectable(true);
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "The scroll position should not change when an item is selected in a Table that is higher than the view.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6197;
    }
}
