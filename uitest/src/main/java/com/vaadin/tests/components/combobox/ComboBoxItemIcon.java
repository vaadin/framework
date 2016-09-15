package com.vaadin.tests.components.combobox;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxItemIcon extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2455;
    }

    @Override
    protected String getDescription() {
        return "All items in the ComboBoxes should have icons.";
    }

    @Override
    protected void setup() {
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("FI", "SE");
            cb.setItemIconGenerator(item -> new ThemeResource(
                    "../tests-tickets/icons/" + item.toLowerCase() + ".gif"));

            addComponent(cb);
        }
        {
            ComboBox<String> cb = new ComboBox<>();
            cb.setItems("Finland", "Australia", "Hungary");
            cb.setItemIconGenerator(
                    item -> new ThemeResource("../tests-tickets/icons/"
                            + item.substring(0, 2).toLowerCase() + ".gif"));

            cb.setValue("Hungary");
            addComponent(cb);
        }
    }

}
