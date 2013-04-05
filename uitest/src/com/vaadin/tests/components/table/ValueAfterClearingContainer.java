package com.vaadin.tests.components.table;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

public class ValueAfterClearingContainer extends TestBase {

    private static final String PROPERTY_ID = "property";

    private Log log = new Log(5);
    private final Table table = new Table();

    @Override
    protected void setup() {
        log.setId("log");
        table.setId("table");
        table.setSelectable(true);
        table.addContainerProperty(PROPERTY_ID, Integer.class, null);
        table.setImmediate(true);
        table.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changed to " + event.getProperty().getValue());
            }
        });
        addComponent(log);

        addComponent(table);
        final CheckBox multiselect = new CheckBox("Multiselect");
        multiselect.setImmediate(true);
        multiselect.setId("multiselect");
        multiselect.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Boolean value = multiselect.getValue();
                table.setMultiSelect(value == null ? false : value);
            }
        });
        addComponent(multiselect);
        Button addItemsButton = new Button("Add table items",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        if (!table.getItemIds().isEmpty()) {
                            Notification
                                    .show("Only possible when the table is empty");
                            return;
                        } else {
                            for (int i = 0; i < 5; i++) {
                                table.addItem(
                                        new Object[] { Integer.valueOf(i) },
                                        Integer.valueOf(i));
                            }
                        }
                    }
                });
        addItemsButton.setId("addItemsButton");
        addComponent(addItemsButton);

        Button showValueButton = new Button("Show value",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log("Table selection: " + table.getValue());
                    }
                });
        showValueButton.setId("showValueButton");
        addComponent(showValueButton);

        Button removeItemsFromTableButton = new Button(
                "Remove items from table", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.removeAllItems();
                    }
                });
        removeItemsFromTableButton.setId("removeItemsFromTableButton");
        addComponent(removeItemsFromTableButton);

        Button removeItemsFromContainerButton = new Button(
                "Remove items from container", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.getContainerDataSource().removeAllItems();
                    }
                });
        removeItemsFromContainerButton.setId("removeItemsFromContainerButton");
        addComponent(removeItemsFromContainerButton);
        Button removeItemsFromContainerAndSanitizeButton = new Button(
                "Remove items from container and sanitize",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.getContainerDataSource().removeAllItems();
                        table.sanitizeSelection();
                    }
                });
        removeItemsFromContainerAndSanitizeButton
                .setId("removeItemsFromContainerAndSanitizeButton");
        addComponent(removeItemsFromContainerAndSanitizeButton);
        Button removeSelectedFromTableButton = new Button(
                "Remove selected item from table", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Object selection = table.getValue();
                        if (selection == null) {
                            Notification.show("There is no selection");
                            return;
                        } else {
                            table.removeItem(selection);
                        }
                    }
                });
        removeSelectedFromTableButton.setId("removeSelectedFromTableButton");
        addComponent(removeSelectedFromTableButton);
        Button removeSelectedFromContainer = new Button(
                "Remove selected item from container",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Object selection = table.getValue();
                        if (selection == null) {
                            Notification.show("There is no selection");
                            return;
                        } else {
                            table.getContainerDataSource()
                                    .removeItem(selection);
                        }
                    }
                });
        removeSelectedFromContainer.setId("removeSelectedFromContainer");
        addComponent(removeSelectedFromContainer);
    }

    @Override
    protected String getDescription() {
        return "Table value should be cleared when the selected item is removed from the container.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9986);
    }

}
