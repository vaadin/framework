package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;

public class TableAndBrowserContextMenu extends TestBase implements
        Action.Handler, ItemClickListener {

    private Table table;
    private boolean actionHandlerHasActions = false;

    @Override
    public void setup() {
        CheckBox cb = new CheckBox("Item click listener");
        cb.setImmediate(true);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (((Boolean) event.getProperty().getValue())) {
                    table.addListener(TableAndBrowserContextMenu.this);
                } else {
                    table.removeListener(TableAndBrowserContextMenu.this);
                }

            }
        });
        addComponent(cb);

        CheckBox cbActionHandler = new CheckBox("Action handler");
        cbActionHandler.setImmediate(true);
        cbActionHandler.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (((Boolean) event.getProperty().getValue())) {
                    table.addActionHandler(TableAndBrowserContextMenu.this);
                } else {
                    table.removeActionHandler(TableAndBrowserContextMenu.this);
                }

            }
        });
        addComponent(cbActionHandler);

        CheckBox cbActionHasActions = new CheckBox("Action handler has actions");
        cbActionHasActions.setImmediate(true);
        cbActionHasActions.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                actionHandlerHasActions = ((Boolean) event.getProperty()
                        .getValue());

                // Workaround to ensure actions are repainted
                removeComponent(table);
                addComponent(table);

            }
        });
        addComponent(cbActionHasActions);

        createTable();
        addComponent(table);

    }

    private void createTable() {
        // Have a table with a numeric column
        table = new Table("A table");
        table.addContainerProperty("Name", String.class, null);
        table.addContainerProperty("Died At Age", Integer.class, null);

        // Add a generated column with a link to Google
        table.addGeneratedColumn("Search", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                Item item = source.getItem(itemId);
                String name = (String) item.getItemProperty("Name").getValue();
                return new Link("Google for " + name, new ExternalResource(
                        "http://www.google.co.uk/search?q=" + name));
            }
        });

        // Insert some data
        Object people[][] = { { "Galileo", 77 }, { "Monnier", 83 },
                { "Vaisala", 79 }, { "Oterma", 86 } };

        for (int i = 0; i < people.length; i++) {
            table.addItem(people[i], i);
        }

        // Calculate the average of the numeric column
        double avgAge = 0;
        for (int i = 0; i < people.length; i++) {
            avgAge += (Integer) people[i][1];
        }
        avgAge /= people.length;

        // Set the footers
        table.setFooterVisible(true);
        table.setColumnFooter("Name", "Average");
        table.setColumnFooter("Died At Age", String.valueOf(avgAge));

        // Adjust the table height a bit
        table.setPageLength(table.size() + 2);

        for (int i = 0; i < people.length; i++) {
            Object[] person = people[i];
            String name = (String) person[0];
            addComponent(new Link("Google for " + name, new ExternalResource(
                    "http://www.google.co.uk/search?q=" + name)));
        }

    }

    @Override
    protected String getDescription() {
        return "Table should only prevent the browser context menu when the right click is used for some Table specific operation. In practice these are either action handlers/context menu or item click listeners (right click). Note that item click listeners affects rows only, not the body.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5924;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        getMainWindow()
                .showNotification("Click using " + event.getButtonName());
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        if (!actionHandlerHasActions) {
            return null;
        }

        return new Action[] { new Action("test") };
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        getMainWindow().showNotification("Action: " + action.getCaption());
    }
}
