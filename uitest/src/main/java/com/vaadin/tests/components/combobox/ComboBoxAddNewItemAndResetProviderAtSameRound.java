package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class ComboBoxAddNewItemAndResetProviderAtSameRound
        extends AbstractTestUI {

    ComboBox<String> comboBox;
    List<String> items = new ArrayList<>();
    int valueChangeEventCount = 0;
    int selectionChangeEventCount = 0;
    Label valueChangeLabel = new Label(null, ContentMode.HTML);
    CheckBox delay = new CheckBox("Slow adding process", false);

    @Override
    protected void setup(VaadinRequest request) {

        initItems();
        comboBox = new ComboBox(null, items);
        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);

        configureNewItemHandling();

        Button checkButton = new Button("Check ComboBox value", e -> {
            Notification.show("selected: " + comboBox.getValue());
        });

        Button resetButton = new Button("Reset options", e -> {
            comboBox.setValue(null);
            initItems();
            comboBox.getDataProvider().refreshAll();
            valueChangeLabel.setValue("Reset");
            valueChangeEventCount = 0;
            selectionChangeEventCount = 0;
        });

        valueChangeLabel.setId("change");
        delay.setId("delay");
        resetButton.setId("reset");

        Button button = new Button("Button for clicking only");
        button.setId("button-for-click");

        HorizontalLayout hl = new HorizontalLayout(checkButton, button);
        addComponents(comboBox, valueChangeLabel, hl, resetButton, delay);
    }

    private void configureNewItemHandling() {
        comboBox.setNewItemProvider(text -> {
            if (delay.getValue()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            items.add(text);
            Collections.sort(items);
            valueChangeLabel
                    .setValue("adding new item... count: " + items.size());
            comboBox.getDataProvider().refreshAll();
            return Optional.of(text);
        });
    }

    private void initItems() {
        items.clear();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                items.add("" + c + i);
            }
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 11343;
    }
}
