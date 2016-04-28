package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxEmptyItemsKeyboardNavigation extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox();
        comboBox.addItems("foo", "bar");

        addComponent(comboBox);
    }
}
