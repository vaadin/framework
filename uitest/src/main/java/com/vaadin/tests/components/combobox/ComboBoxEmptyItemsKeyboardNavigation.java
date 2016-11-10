package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.DataProvider;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxEmptyItemsKeyboardNavigation extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>(null,
                DataProvider.create("foo", "bar"));

        addComponent(comboBox);
    }
}
