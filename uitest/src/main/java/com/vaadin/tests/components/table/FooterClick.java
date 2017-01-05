package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.FooterClickEvent;

public class FooterClick extends AbstractTestUIWithLog {

    private final String COLUMN1_PROPERTY_ID = "col1";
    private final String COLUMN2_PROPERTY_ID = "col2";
    private final String COLUMN3_PROPERTY_ID = "col3";

    @Override
    protected void setup(VaadinRequest request) {
        final Table table = new Table();
        table.setId("table");
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);
        table.setFooterVisible(true);
        table.setColumnReorderingAllowed(true);

        table.setColumnFooter(COLUMN1_PROPERTY_ID, "fuu");
        table.setColumnFooter(COLUMN3_PROPERTY_ID, "fuubar");

        final TextField columnField = new TextField(
                "ProperyId of clicked column");
        columnField.setId("ClickedColumn");

        // Add a footer click listener
        table.addFooterClickListener(new Table.FooterClickListener() {
            @Override
            public void footerClick(FooterClickEvent event) {
                columnField.setValue(String.valueOf(event.getPropertyId()));
                log("Clicked on footer: " + event.getPropertyId());
            }
        });

        CheckBox immediateCheckbox = new CheckBox("Immediate");
        immediateCheckbox.setValue(table.isImmediate());
        immediateCheckbox.addValueChangeListener(
                event -> table.setImmediate(event.getValue()));

        CheckBox columnReorderingCheckbox = new CheckBox(
                "Column reordering allowed");
        columnReorderingCheckbox.setValue(table.isColumnReorderingAllowed());
        columnReorderingCheckbox.addValueChangeListener(
                event -> table.setColumnReorderingAllowed(event.getValue()));
        addComponent(immediateCheckbox);
        addComponent(columnReorderingCheckbox);
        addComponent(table);
        addComponent(columnField);
    }

    @Override
    protected String getTestDescription() {
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
