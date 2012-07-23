package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class HeaderClick extends TestBase {

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setColumnReorderingAllowed(true);
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);

        final TextField columnField = new TextField(
                "ProperyId of clicked column");

        // Add a header click listener
        table.addListener(new Table.HeaderClickListener() {
            @Override
            public void headerClick(HeaderClickEvent event) {
                columnField.setValue(String.valueOf(event.getPropertyId()));
            }
        });

        CheckBox immediateCheckbox = new CheckBox("Immediate");
        immediateCheckbox.setImmediate(true);
        immediateCheckbox.setValue(table.isImmediate());
        immediateCheckbox.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setImmediate((Boolean) event.getProperty().getValue());
            }
        });

        CheckBox sortEnabledCheckbox = new CheckBox("Sortable");
        sortEnabledCheckbox.setImmediate(true);
        sortEnabledCheckbox.setValue(table.isSortEnabled());
        sortEnabledCheckbox.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                table.setSortDisabled(!(Boolean) event.getProperty().getValue());
            }
        });

        CheckBox columnReorderingCheckbox = new CheckBox(
                "Column reordering allowed");
        columnReorderingCheckbox.setImmediate(true);
        columnReorderingCheckbox.setValue(table.isColumnReorderingAllowed());
        columnReorderingCheckbox
                .addListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        table.setColumnReorderingAllowed((Boolean) event
                                .getProperty().getValue());
                    }
                });

        addComponent(immediateCheckbox);
        addComponent(sortEnabledCheckbox);
        addComponent(columnReorderingCheckbox);
        addComponent(table);
        addComponent(columnField);

    }

    @Override
    protected String getDescription() {
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
