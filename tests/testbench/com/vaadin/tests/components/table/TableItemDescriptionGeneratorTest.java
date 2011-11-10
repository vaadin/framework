package com.vaadin.tests.components.table;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TableItemDescriptionGeneratorTest extends TestBase {

    private final String COLUMN1_PROPERTY_ID = "Text - Cell description";
    private final String COLUMN2_PROPERTY_ID = "Text - Row description";
    private final String COLUMN3_PROPERTY_ID = "Widget";

    @Override
    protected void setup() {
        final Table table = new Table();
        table.setDebugId("table");
        table.setContainerDataSource(createContainer());
        addComponent(table);

        table.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
            public String generateDescription(Component source, Object itemId,
                    Object propertyId) {
                if (propertyId == null) {
                    return "Row description " + itemId;
                } else if (propertyId == COLUMN1_PROPERTY_ID) {
                    return "Cell description " + itemId + "," + propertyId;
                }
                return null;
            }
        });

        table.addGeneratedColumn(COLUMN3_PROPERTY_ID,
                new Table.ColumnGenerator() {

                    public Component generateCell(Table source, Object itemId,
                            Object columnId) {
                        TextField lbl = new TextField();
                        lbl.setDescription("Textfields own description");
                        return lbl;
                    }
                });
    }

    @Override
    protected String getDescription() {
        return "Cells and rows should have tooltips";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5414;
    }

    private Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(COLUMN1_PROPERTY_ID, String.class, "");
        container.addContainerProperty(COLUMN2_PROPERTY_ID, String.class, "");
        // container.addContainerProperty(COLUMN3_PROPERTY_ID, String.class,
        // "");

        for (int i = 0; i < 5; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty(COLUMN1_PROPERTY_ID).setValue("first" + i);
            item.getItemProperty(COLUMN2_PROPERTY_ID).setValue("middle" + i);
            // item.getItemProperty(COLUMN3_PROPERTY_ID).setValue("last" + i);
        }

        return container;
    }

}
