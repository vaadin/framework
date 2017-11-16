package com.vaadin.tests.components.table;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;

public class AddItemToEmptyTable extends TestBase {

    private Table rightTable;

    @Override
    protected void setup() {
        CheckBox cb = new CheckBox("Set first column width");
        cb.setValue(false);
        cb.addValueChangeListener(event -> {
            if (event.getValue()) {
                rightTable.setColumnWidth("name", 150);
            } else {
                rightTable.setColumnWidth("name", -1);
            }
        });
        addComponent(cb);

        cb = new CheckBox("Set second column width");
        cb.setValue(true);
        cb.addValueChangeListener(event -> {
            if (event.getValue()) {
                rightTable.setColumnWidth("info", 20);
            } else {
                rightTable.setColumnWidth("info", -1);
            }
        });
        addComponent(cb);

        rightTable = new Table();
        rightTable.setSizeFull();
        rightTable.setPageLength(7);

        rightTable.setColumnReorderingAllowed(false);
        rightTable.setColumnCollapsingAllowed(true);

        rightTable.setSelectable(true);
        rightTable.setMultiSelect(true);
        // react at once when something is selected
        rightTable.setImmediate(true);

        rightTable.addContainerProperty("name", String.class, null);
        // rightTable.setColumnWidth("name", 150);
        rightTable.setColumnAlignment("name", Table.ALIGN_LEFT);

        rightTable.addContainerProperty("info", Button.class, null);
        rightTable.setColumnWidth("info", 20);
        rightTable.setColumnAlignment("info", Table.ALIGN_LEFT);

        addComponent(rightTable);

        Button b = new Button("Add item", event -> {
            Object id = rightTable.addItem();
            Item item = rightTable.getItem(id);
            item.getItemProperty("name").setValue("Role");
            item.getItemProperty("info").setValue(new Button("Button"));
        });
        addComponent(b);

        b = new Button("Clear", event -> rightTable.removeAllItems());
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Adding an item to a Table should work independent of column width settings...";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7731;
    }
}
