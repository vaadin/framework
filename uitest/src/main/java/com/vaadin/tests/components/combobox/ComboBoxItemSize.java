package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class ComboBoxItemSize extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        List<String> items = new ArrayList<>(Arrays.asList("blue", "red",
                "green", "purple", "grey", "orange"));

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setId("combobox");
        comboBox.setPageLength(0);
        comboBox.setItems(items);
        DataProvider<String, ?> dataProvider = comboBox.getDataProvider();
        comboBox.setNewItemProvider(value -> {
            items.add(value);
            dataProvider.refreshAll();
            return Optional.ofNullable(value);
        });

        Button button = new Button("get value");
        Label label = new Label("value of combobox");
        button.addClickListener(event -> label
                .setValue("combobox value " + comboBox.getValue()));

        Button reset = new Button("reset");
        reset.addClickListener(event -> {
            items.clear();
            items.addAll(Arrays.asList("blue", "red", "green", "purple", "grey",
                    "orange"));
            dataProvider.refreshAll();
            comboBox.setValue(null);
        });

        addComponents(comboBox, button, label, reset);
    }
}
