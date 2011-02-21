package com.vaadin.tests.server.component.table;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;

public class TableGenerator {
    public static Table createTableWithDefaultContainer(int properties,
            int items) {
        Table t = new Table();

        for (int i = 0; i < properties; i++) {
            t.addContainerProperty("Property " + i, String.class, null);
        }

        for (int j = 0; j < items; j++) {
            Item item = t.addItem("Item " + j);
            for (int i = 0; i < properties; i++) {
                item.getItemProperty("Property " + i).setValue(
                        "Item " + j + "/Property " + i);
            }
        }

        return t;
    }

}
