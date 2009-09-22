package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxValueInput extends TestBase {

    @Override
    protected void setup() {
        ComboBox cb = new ComboBox("A combobox without input prompt");
        cb.setImmediate(true);
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");

        addComponent(cb);

        cb = new ComboBox("A combobox with input prompt");
        cb.setInputPrompt("Please select");
        cb.setImmediate(true);
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");

        addComponent(cb);

        cb = new ComboBox("A combobox with null item");
        cb.setInputPrompt("Please select");
        cb.setImmediate(true);
        cb.addItem("Null item");
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");
        cb.setNullSelectionItemId("Null item");

        addComponent(cb);

        cb = new ComboBox("A combobox with null item and input prompt");
        cb.setImmediate(true);
        cb.addItem("Null item");
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");
        cb.setNullSelectionItemId("Null item");

        addComponent(cb);
    }

    @Override
    protected String getDescription() {
        return "A combobox should always show the selected value when it is not focused. Entering a text when nothing is selected and blurring the combobox should reset the value. The same should happen when a value is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3268;
    }

}
