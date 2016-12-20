package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.DefaultFieldFactory;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

public class TableUnregisterComponent extends TestBase {

    private static final String COL_A = "textFieldA";
    private static final String COL_B = "textB";

    @Override
    protected void setup() {
        final Log log = new Log(5);

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(COL_A, TextField.class, null);
        container.addContainerProperty(COL_B, String.class, "");

        Item it = container.addItem("a");
        final ObjectProperty<String> valA = new ObjectProperty<>("orgVal");
        final TextField fieldA = new TextField(valA) {
            @Override
            public void setPropertyDataSource(Property newDataSource) {
                super.setPropertyDataSource(newDataSource);
                if (newDataSource == null) {
                    log.log("Embedded field property data source cleared");
                } else {
                    log.log("Embedded field property data source set");
                }
            }
        };
        it.getItemProperty(COL_A).setValue(fieldA);
        it.getItemProperty(COL_B).setValue("Some text here");

        final Table table = new Table("", container);
        table.setColumnCollapsingAllowed(true);
        table.setTableFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field<?> createField(Container container, Object itemId,
                    Object propertyId, Component uiContext) {
                if (COL_B.equals(propertyId)) {
                    Field<String> field = new TextField() {
                        @Override
                        public void setPropertyDataSource(
                                Property newDataSource) {
                            super.setPropertyDataSource(newDataSource);
                            if (newDataSource == null) {
                                log.log("Edit field property data source cleared");
                            } else {
                                log.log("Edit field property data source set");
                            }
                        }
                    };
                    field.setCaption(createCaptionByPropertyId(propertyId));
                    return field;
                } else {
                    return super.createField(container, itemId, propertyId,
                            uiContext);
                }
            }
        });

        addComponent(log);
        addComponent(table);

        addComponent(new Button("Switch column collapse", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setColumnCollapsed(COL_A,
                        !table.isColumnCollapsed(COL_A));
            }
        }));

        addComponent(new Button("Switch editable", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                table.setEditable(!table.isEditable());
            }
        }));

    }

    @Override
    protected String getDescription() {
        return "Table.uncollapseColumn (triggered by collapsing column or disabling editable mode) should only unregister property data sources that have been added by the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf("7541");
    }

}
