package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxUndefinedWidthAndIcon extends TestBase {
    @Override
    protected void setup() {
        List<String> data = new ArrayList<>();
        for (int i = 1; i < 200 + 1; i++) {
            data.add("Item " + i);
        }
        ComboBox<String> cb = new ComboBox<>(null, data);
        cb.setItemIconProvider(
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
