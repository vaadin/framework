package com.vaadin.tests.components.combobox;

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

    private ComboBox<String> createNewComboBoxA(String caption) {
        ComboBox<String> combo = new ComboBox<>(caption);
        combo.setItems("first");
        combo.setValue("first");

        addComponent(combo);

        return combo;
    }

    private CheckBox createReadOnlyForComboBox(ComboBox combo) {
        CheckBox readonly = new CheckBox("Second combobox is read only");
        readonly.setValue(combo.isReadOnly());
        readonly.addValueChangeListener(
                event -> combo.setReadOnly(event.getValue()));
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
