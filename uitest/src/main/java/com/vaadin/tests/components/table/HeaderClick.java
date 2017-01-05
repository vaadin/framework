package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.HeaderClickEvent;

public class HeaderClick extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);

        final TextField columnField = new TextField(
                "ProperyId of clicked column");

        // Add a header click listener
        table.addHeaderClickListener(new Table.HeaderClickListener() {
            @Override
            public void headerClick(HeaderClickEvent event) {
                columnField.setValue(String.valueOf(event.getPropertyId()));
            }
        });

        CheckBox immediateCheckbox = new CheckBox("Immediate");
        immediateCheckbox.setValue(table.isImmediate());
        immediateCheckbox.addValueChangeListener(
                event -> table.setImmediate(event.getValue()));

        CheckBox sortEnabledCheckbox = new CheckBox("Sortable");
        sortEnabledCheckbox.setValue(table.isSortEnabled());
        sortEnabledCheckbox.addValueChangeListener(
                event -> table.setSortEnabled(event.getValue()));

        CheckBox columnReorderingCheckbox = new CheckBox(
                "Column reordering allowed");
        columnReorderingCheckbox.setValue(table.isColumnReorderingAllowed());
        columnReorderingCheckbox.addValueChangeListener(
                event -> table.setColumnReorderingAllowed(event.getValue()));

        addComponent(immediateCheckbox);
        addComponent(sortEnabledCheckbox);
        addComponent(columnReorderingCheckbox);
        addComponent(table);
        addComponent(columnField);

    }

    @Override
    protected String getTestDescription() {
        return "Tests the header click listener";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4515;
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
