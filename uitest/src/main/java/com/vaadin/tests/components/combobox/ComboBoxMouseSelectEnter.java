package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxMouseSelectEnter extends AbstractReindeerTestUI {
    protected ComboBox<String> comboBox;

    @Override
    protected void setup(VaadinRequest request) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add("a" + i);
        }
        comboBox = new ComboBox<>(null, items);
        final Label label = new Label();
        label.setId("value");

        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);

        comboBox.addValueChangeListener(
                event -> label.setValue(String.valueOf(event.getValue())));

        addComponents(comboBox);
        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "Pressing Enter should set value highlighted from mouse position after using arrow keys";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16981;
    }
}
