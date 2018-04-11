package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxSetNullWhenNewItemsAllowed extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox comboBox = new ComboBox("My ComboBox");
        comboBox.setImmediate(true);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setNewItemsAllowed(true);
        for (int i = 0; i < 10; i++) {
            comboBox.addItem("Item " + i);
        }

        final Label value = new Label("Selected: ");

        comboBox.addValueChangeListener(event -> {
            if (comboBox.getValue() != null) {
                comboBox.setValue(null);
                value.setValue("Selected: " + (String) comboBox.getValue());
            }
        });
        addComponent(comboBox);
        addComponent(value);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should clear its value when setting to null with new items.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13413;
    }
}
