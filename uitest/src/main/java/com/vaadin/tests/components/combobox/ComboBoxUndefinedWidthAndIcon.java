package com.vaadin.tests.components.combobox;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;

public class ComboBoxUndefinedWidthAndIcon extends TestBase {
    @Override
    protected void setup() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setDataProvider(new ItemDataProvider(200));
        cb.setItemIconGenerator(
                item -> new ThemeResource("../runo/icons/16/users.png"));

        addComponent(cb);
    }

    @Override
    protected String getDescription() {
        return "The width of the ComboBox should be fixed even though it is set to undefined width. The width should not change when changing pages in the dropdown.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7013;
    }
}
