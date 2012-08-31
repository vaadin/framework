package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxValueInput extends TestBase {

    @Override
    protected void setup() {
        (getLayout()).setSpacing(true);

        ComboBox cb = getComboBox("A combobox", false);
        addComponent(cb);

        cb = getComboBox("A combobox with input prompt", false);
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A combobox with null item", true);
        addComponent(cb);

        cb = getComboBox("A combobox with null item and input prompt", true);
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A disabled combobox", true);
        cb.setEnabled(false);
        addComponent(cb);

        cb = getComboBox("A read-only combobox", true);
        cb.setReadOnly(true);
        addComponent(cb);

        cb = getComboBox("A combobox with filteringMode off", false);
        cb.setFilteringMode(ComboBox.FILTERINGMODE_OFF);

    }

    @Override
    protected String getDescription() {
        return "A combobox should always show the selected value when it is not focused. Entering a text when nothing is selected and blurring the combobox should reset the value. The same should happen when a value is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3268;
    }

    private ComboBox getComboBox(String caption, boolean addNullItem) {
        ComboBox cb = new ComboBox(caption);
        cb.setImmediate(true);
        if (addNullItem) {
            cb.addItem("Null item");
            cb.setNullSelectionItemId("Null item");
        }
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");

        return cb;
    }

}
