package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxValueInput extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        (getLayout()).setSpacing(true);

        ComboBox cb = getComboBox("A combobox", false, "default");
        addComponent(cb);

        cb = getComboBox("A combobox with input prompt", false,
                "default-prompt");
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A combobox with null item", true, "null");
        addComponent(cb);

        cb = getComboBox("A combobox with null item and input prompt", true,
                "null-prompt");
        cb.setInputPrompt("Please select");
        addComponent(cb);

        cb = getComboBox("A combobox with filteringMode off", false,
                "filtering-off");
        cb.setFilteringMode(FilteringMode.OFF);
        addComponent(cb);

    }

    @Override
    protected String getTestDescription() {
        return "A combobox should always show the selected value when it is not focused. Entering a text when nothing is selected and blurring the combobox should reset the value. The same should happen when a value is selected";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3268;
    }

    private ComboBox getComboBox(String caption, boolean addNullItem, String id) {
        ComboBox cb = new ComboBox(caption);
        cb.setImmediate(true);
        if (addNullItem) {
            cb.addItem("Null item");
            cb.setNullSelectionItemId("Null item");
        }
        cb.addItem("Value 1");
        cb.addItem("Value 2");
        cb.addItem("Value 3");

        cb.setId(id);

        return cb;
    }

}
