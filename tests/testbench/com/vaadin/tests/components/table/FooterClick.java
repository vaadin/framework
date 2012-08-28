package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class FooterClick extends TestBase {

    private final String COLUMN1_PROPERTY_ID = "col1";
    private final String COLUMN2_PROPERTY_ID = "col2";
    private final String COLUMN3_PROPERTY_ID = "col3";

    private Log log = new Log(5);

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setId("table");
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        table.setHeight("400px");
        table.setImmediate(true);
        table.setFooterVisible(true);
        table.setColumnReorderingAllowed(true);

        table.setColumnFooter(COLUMN1_PROPERTY_ID, "fuu");
        // table.setColumnFooter(COLUMN2_PROPERTY_ID, "bar");
        table.setColumnFooter(COLUMN3_PROPERTY_ID, "fuubar");

        final TextField columnField = new TextField(
                "ProperyId of clicked column");
        columnField.setId("ClickedColumn");

        // Add a footer click listener
        table.addListener(new Table.FooterClickListener() {
            @Override
            public void footerClick(FooterClickEvent event) {
                columnField.setValue(String.valueOf(event.getPropertyId()));
                log.log("Clicked on footer: " + event.getPropertyId());
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
        addComponent(columnReorderingCheckbox);

        addComponent(log);

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
