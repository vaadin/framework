package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.TableDragMode;

@SuppressWarnings("serial")
public class CtrlShiftMultiselect extends TestBase {

    protected final Table table = new Table("Multiselectable table");

    @Override
    protected void setup() {

        table.setContainerDataSource(createContainer());
        table.setImmediate(true);

        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setDragMode(TableDragMode.MULTIROW);

        table.setWidth("400px");
        table.setHeight("400px");

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Improve Table multiselect to use Ctrl and Shift for selection";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3520;
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
