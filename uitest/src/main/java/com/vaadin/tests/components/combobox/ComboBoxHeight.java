package com.vaadin.tests.components.combobox;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxHeight extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPageLength(0);
        comboBox.setItems(
                IntStream.range(0, 100)
                        .mapToObj(i -> "Item number " + String.valueOf(i))
                        .collect(Collectors.toList()));

        addComponent(comboBox);
    }
}
