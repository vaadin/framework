package com.vaadin.tests.components.combobox;

import com.vaadin.data.util.MethodProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;

public class WidthToggleReadOnly extends TestBase {

    @Override
    protected void setup() {
        ComboBox combo = createNewComboBoxA("Untouched combobox");
        addComponent(combo);

        combo = createNewComboBoxA("Toggled combobox");
        addComponent(combo);
        addComponent(createReadOnlyForComboBox(combo));
    }

    private ComboBox createNewComboBoxA(String caption) {
        ComboBox combo = new ComboBox(caption);
        combo.addItem("first");
        combo.setValue("first");

        addComponent(combo);

        return combo;
    }

    private CheckBox createReadOnlyForComboBox(ComboBox combo) {
        CheckBox readonly = new CheckBox("Second combobox is read only",
                new MethodProperty(combo, "readOnly"));
        readonly.setImmediate(true);
        addComponent(readonly);
        return readonly;
    }

    @Override
    protected String getDescription() {
        return "Check that toggling read only mode of second combobox does not change it's width.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5833;
    }

}
