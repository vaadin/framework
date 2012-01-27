package com.vaadin.tests.components.table;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class NotselectablePaintSelections extends TestBase {

    @Override
    protected String getDescription() {
        return "Table should paint selections even if it's not selectable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3500;
    }

    @Override
    protected void setup() {
        // Multiselect
        Table t = new Table("Multiselect");
        addComponent(t);
        t.setSelectable(false);
        t.setMultiSelect(true);
        t.setPageLength(5);
        t.addContainerProperty("Name", String.class, null);
        Set<Object> selected = new HashSet<Object>();
        for (int i = 0; i < 30; i++) {
            Item item = t.addItem(i);
            item.getItemProperty("Name").setValue("Name " + i);
            if (i % 2 == 0) {
                selected.add(i);
            }
        }
        t.setValue(selected);

        // Singleselect
        t = new Table("Singleselect");
        addComponent(t);
        t.setSelectable(false);
        t.setMultiSelect(false);
        t.setPageLength(5);
        t.addContainerProperty("Name", String.class, null);
        for (int i = 0; i < 30; i++) {
            Item item = t.addItem(i);
            item.getItemProperty("Name").setValue("Name " + i);
        }
        t.setValue(3);

    }
}
