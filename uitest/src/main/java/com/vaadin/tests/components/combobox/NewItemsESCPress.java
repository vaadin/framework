package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

@SuppressWarnings("serial")
public class NewItemsESCPress extends TestBase {

    @Override
    protected void setup() {
        final TextArea addedItems = new TextArea("Last added items:");
        addedItems.setRows(10);
        addComponent(addedItems);

        final ComboBox box = new ComboBox("New items are allowed");
        box.setNewItemsAllowed(true);
        box.setNewItemHandler(newItemCaption -> {
            String value = addedItems.getValue();
            addedItems.setValue(value + newItemCaption + "\n");
            box.addItem(newItemCaption);
        });
        box.setImmediate(true);
        addComponent(box);
    }

    @Override
    protected String getDescription() {
        return "Firefox flashes the previously entered value when holding the ESC-key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5694;
    }

}
