package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxTestBenchPerformance extends AbstractTestUI {

    @SuppressWarnings("deprecation")
    @Override
    protected void setup(VaadinRequest request) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(i + "");
        }
        ComboBox<String> combo = new ComboBox<>("ComboBox with NewItemHandler");
        combo.setItems(items);
        combo.setEmptySelectionAllowed(false);
        combo.setNewItemHandler(inputString -> {
            items.add(inputString);
            combo.setItems(items);
            combo.setSelectedItem(inputString);
        });
        combo.setSelectedItem(items.iterator().next());
        addComponent(combo);
    }

    @Override
    protected String getTestDescription() {
        return "Selecting values through TestBench shouldn't take minutes.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10284;
    }
}
