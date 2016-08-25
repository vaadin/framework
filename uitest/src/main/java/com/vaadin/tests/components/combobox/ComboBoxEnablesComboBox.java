package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxEnablesComboBox extends TestBase {

    private ComboBox cb2;

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>("Always enabled");
        populate(cb);
        cb.addValueChangeListener(event -> cb2.setEnabled(true));
        cb2 = new ComboBox<String>("Initially disabled");
        cb2.setEnabled(false);
        populate(cb2);

        addComponent(cb);
        addComponent(cb2);
    }

    private void populate(ComboBox<String> cb) {
        List<String> items = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            items.add("Item " + i);
        }
        cb.setItems(items);
    }

    @Override
    protected String getDescription() {
        return "Selecting an item in the first combobox enables the second.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4632;
    }

}
