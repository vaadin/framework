package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

public class ComboBoxEnablesComboBox extends TestBase {

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>("Always enabled");
        ComboBox<String> cb2 = new ComboBox<>("Initially disabled");
        cb.setDataProvider(new ItemDataProvider(10));
        cb.addValueChangeListener(event -> cb2.setEnabled(true));
        cb.setDataProvider(new ItemDataProvider(10));
        cb2.setEnabled(false);

        addComponent(cb);
        addComponent(cb2);
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
