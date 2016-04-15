package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class NewItemsESCPress extends TestBase {

    @Override
    protected void setup() {
        final TextArea addedItems = new TextArea("Last added items:");
        addedItems.setRows(10);
        addComponent(addedItems);

        final ComboBox box = new ComboBox("New items are allowed");
        box.setNewItemsAllowed(true);
        box.setNewItemHandler(new NewItemHandler() {
            @Override
            public void addNewItem(String newItemCaption) {
                String value = addedItems.getValue();
                addedItems.setValue(value + newItemCaption + "\n");
                box.addItem(newItemCaption);
            }
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
