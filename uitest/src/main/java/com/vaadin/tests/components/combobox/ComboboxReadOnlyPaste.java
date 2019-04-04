package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboboxReadOnlyPaste extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> nameComboBox = new ComboBox<>("Name");
        nameComboBox.setId("readOnlyCB");
        nameComboBox.setEmptySelectionAllowed(true);
        nameComboBox.setItems(
                Stream.of("A", "B", "C", "D").collect(Collectors.toList()));
        nameComboBox.setValue("B");
        nameComboBox.setReadOnly(true);

        addComponent(nameComboBox);
    }
}
