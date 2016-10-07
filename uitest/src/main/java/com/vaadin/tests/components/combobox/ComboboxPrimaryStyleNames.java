package com.vaadin.tests.components.combobox;

import com.vaadin.server.data.DataSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboboxPrimaryStyleNames extends TestBase {

    @Override
    protected void setup() {
        final ComboBox<String> box = new ComboBox(null,
                DataSource.create("Value 1", "Value 2", "Value 3", "Value 4"));
        box.setPrimaryStyleName("my-combobox");

        addComponent(box);
        addComponent(new Button("Set primary style",
                event -> box.setPrimaryStyleName("my-second-combobox")));

    }

    @Override
    protected String getDescription() {
        return "Combobox should work with primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9901;
    }

}
