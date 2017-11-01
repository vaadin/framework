package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class LastColumnNegative extends TestBase {
    Table table = setupTable();
    VerticalLayout wrapper = new VerticalLayout();

    @Override
    public void setup() {
        Button addButton = new Button("Add a table",
                event -> wrapper.addComponent(table));
        Button removeButton = new Button("Remove a table",
                event -> wrapper.removeComponent(table));
        Button shrinkWrapper = new Button("Shrink wrapper",
                event -> wrapper.setWidth("400px"));

        addComponent(addButton);
        addComponent(removeButton);
        addComponent(shrinkWrapper);
        addComponent(wrapper);
    }

    private Table setupTable() {
        IndexedContainer container = new IndexedContainer();

        container.addContainerProperty("fileName", String.class,
                "Long enough string to cause a scrollbar when the window is set to a dencently small size.");
        container.addContainerProperty("size", Long.class, 23958l);
        container.addItem();
        container.addItem();
        container.addItem();

        Table table = new Table();
        table.setContainerDataSource(container);
        table.setWidth("100%");
        table.setColumnCollapsingAllowed(true);
        table.setColumnExpandRatio("size", 1);
        return table;
    }

    @Override
    protected String getDescription() {

        return "Table rendering should not fail when view becomes smaller than the table width.";
    }

    @Override
    protected Integer getTicketNumber() {

        return 8411;
    }
}
