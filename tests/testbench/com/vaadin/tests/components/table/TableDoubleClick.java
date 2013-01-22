package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableDoubleClick extends TestBase {

    private final String COLUMN1_PROPERTY_ID = "col1";
    private final String COLUMN2_PROPERTY_ID = "col2";
    private final String COLUMN3_PROPERTY_ID = "col3";

    private Log log = new Log(5);

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setDebugId("table");
        table.setContainerDataSource(createContainer());
        table.setWidth("400px");
        // table.setHeight("400px");
        table.setPageLength(10);
        table.setImmediate(true);
        table.setFooterVisible(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setNullSelectionAllowed(false);

        table.setColumnFooter(COLUMN1_PROPERTY_ID, "fuu");
        // table.setColumnFooter(COLUMN2_PROPERTY_ID, "bar");
        table.setColumnFooter(COLUMN3_PROPERTY_ID, "fuubar");

        table.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    if (event.getButton() == ItemClickEvent.BUTTON_LEFT) {
                        log.log("Left double clicked.");
                    }
                }
            }
        });

        addComponent(log);

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "Doubleclick events should have getButton() == BUTTON_LEFT";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10763;
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
