package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class DisabledTableKeyboardNavigation extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table("Multiselectable table");

        table.setContainerDataSource(createContainer());
        table.setImmediate(true);
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setWidth("400px");
        table.setHeight("200px");

        table.setEnabled(false);

        addComponent(table);

    }

    @Override
    protected String getDescription() {
        return "Once should not be able to focus or use a disabled table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5797;
    }

    private Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("col1", String.class, "");
        container.addContainerProperty("col2", String.class, "");
        container.addContainerProperty("col3", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty("col1").setValue("first" + i);
            item.getItemProperty("col2").setValue("middle" + i);
            item.getItemProperty("col3").setValue("last" + i);
        }

        return container;
    }

}
