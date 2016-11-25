package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

public class ComboBoxEnablesComboBox extends TestBase {

    private ComboBox cb2;

    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>("Always enabled");
        cb.setDataProvider(new ItemDataProvider(10));
        cb.addValueChangeListener(event -> cb2.setEnabled(true));
        cb2 = new ComboBox<String>("Initially disabled");
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
