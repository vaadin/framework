package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Table.FooterClickEvent;

@SuppressWarnings("serial")
public class FooterClick extends TestBase {

    private final String COLUMN1_PROPERTY_ID = "col1";
    private final String COLUMN2_PROPERTY_ID = "col2";
    private final String COLUMN3_PROPERTY_ID = "col3";

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);
        table.setFooterVisible(true);

        table.setColumnFooter(COLUMN1_PROPERTY_ID, "fuu");
        table.setColumnFooter(COLUMN2_PROPERTY_ID, "bar");
        table.setColumnFooter(COLUMN3_PROPERTY_ID, "fuubar");

        final TextField columnField = new TextField(
                "ProperyId of clicked column");

        // Set the footer click handler
        table.setFooterClickHandler(new Table.FooterClickHandler() {
            public void handleFooterClick(FooterClickEvent event) {
                columnField.setValue(event.getPropertyId());
            }
        });

        addComponent(table);
        addComponent(columnField);
    }

    @Override
    protected String getDescription() {
        return "Tests the footer click handler";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4516;
    }

    private Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(COLUMN1_PROPERTY_ID, String.class, "");
        container.addContainerProperty(COLUMN2_PROPERTY_ID, String.class, "");
        container.addContainerProperty(COLUMN3_PROPERTY_ID, String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty(COLUMN1_PROPERTY_ID).setValue("first" + i);
            item.getItemProperty(COLUMN2_PROPERTY_ID).setValue("middle" + i);
            item.getItemProperty(COLUMN3_PROPERTY_ID).setValue("last" + i);
        }

        return container;
    }

}
