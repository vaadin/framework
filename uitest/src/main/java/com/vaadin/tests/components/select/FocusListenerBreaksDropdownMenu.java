package com.vaadin.tests.components.select;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.ComboBox;

public class FocusListenerBreaksDropdownMenu extends TestBase {

    @Override
    protected void setup() {
        final ComboBox comboBox = new ComboBox();
        for (int i = 0; i < 5; ++i) {
            comboBox.addItem("Item " + i);
        }

        comboBox.addFocusListener(event -> comboBox.addItem());

        comboBox.setImmediate(true);
        addComponent(comboBox);
    }

    @Override
    protected String getDescription() {
        return "Clicking the dropdown arrow on a not-already-focused ComboBox "
                + "breaks the dropdown list if a FocusListener adds or removes items";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8321;
    }

}
