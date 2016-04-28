package com.vaadin.tests.components.table;

import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class KeyControl extends TestBase {

    private final Label selected1 = new Label("No selected items.");
    private final Table table1 = new Table("Read only table");

    private final Label selected2 = new Label("No selected items");
    private final Table table2 = new Table("Selectable table");

    private final Label selected3 = new Label("Not selected items");
    private final Table table3 = new Table("Multi-selectable table");

    @Override
    protected void setup() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        addComponent(layout);

        // Create read only table
        VerticalLayout layout1 = new VerticalLayout();
        layout1.setSpacing(true);

        table1.setContainerDataSource(createContainer());
        table1.setWidth("300px");
        table1.setHeight("300px");
        table1.setImmediate(true);
        layout1.addComponent(table1);

        table1.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                @SuppressWarnings("unchecked")
                Set<String> value = (Set<String>) table1.getValue();
                selected1.setValue(value.toString() + " TOTAL: " + value.size());
            }
        });

        layout1.addComponent(selected1);
        layout.addComponent(layout1);

        // Create single select table
        VerticalLayout layout2 = new VerticalLayout();
        layout2.setSpacing(true);

        table2.setContainerDataSource(createContainer());
        table2.setSelectable(true);
        table2.setWidth("300px");
        table2.setHeight("300px");
        table2.setImmediate(true);
        layout2.addComponent(table2);

        table2.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String value = table2.getValue() == null ? "No selected items"
                        : table2.getValue().toString();
                selected2.setValue(value);
            }
        });

        layout2.addComponent(selected2);
        layout.addComponent(layout2);

        // Create multi select table
        VerticalLayout layout3 = new VerticalLayout();
        layout3.setSpacing(true);

        table3.setContainerDataSource(createContainer());
        table3.setSelectable(true);
        table3.setMultiSelect(true);
        table3.setWidth("300px");
        table3.setHeight("300px");
        table3.setImmediate(true);
        layout3.addComponent(table3);

        table3.addListener(new Table.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                @SuppressWarnings("unchecked")
                Set<String> value = (Set<String>) table3.getValue();
                selected3.setValue(value.size() == 0 ? "No selected items"
                        : value + ": Total " + value.size() + " items");
            }
        });

        selected3.setWidth("300px");
        selected3.setHeight("500px");
        layout3.addComponent(selected3);
        layout.addComponent(layout3);
    }

    @Override
    protected String getDescription() {
        return "Add keyboard control to the Table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2390;
    }

    private Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("col1", String.class, "");
        container.addContainerProperty("col2", String.class, "");
        container.addContainerProperty("col3", String.class, "");
        container.addContainerProperty("col4", String.class, "");
        container.addContainerProperty("col5", String.class, "");
        container.addContainerProperty("col6", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty("col1").setValue("First column " + i);
            item.getItemProperty("col2").setValue("Second column " + i);
            item.getItemProperty("col3").setValue("Third column" + i);
            item.getItemProperty("col4").setValue("Fourth column" + i);
            item.getItemProperty("col5").setValue("Fifth column" + i);
            item.getItemProperty("col6").setValue("Sixth column" + i);
        }

        return container;
    }

}
