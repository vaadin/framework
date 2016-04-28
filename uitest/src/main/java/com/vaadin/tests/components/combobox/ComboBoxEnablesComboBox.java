package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxEnablesComboBox extends TestBase {

    private ComboBox cb2;

    @Override
    protected void setup() {
        ComboBox cb = new ComboBox("Always enabled");
        cb.setImmediate(true);
        populate(cb);
        cb.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                cb2.setEnabled(true);
            }

        });
        cb2 = new ComboBox("Initially disabled");
        cb2.setImmediate(true);
        cb2.setEnabled(false);
        populate(cb2);

        addComponent(cb);
        addComponent(cb2);
    }

    private void populate(ComboBox cb) {
        for (int i = 1; i < 10; i++) {
            cb.addItem("Item " + i);
        }
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
