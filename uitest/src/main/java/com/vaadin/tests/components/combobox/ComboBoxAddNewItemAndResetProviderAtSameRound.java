package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class ComboBoxAddNewItemAndResetProviderAtSameRound
        extends AbstractTestUIWithLog {

    ComboBox<String> comboBox;
    List<String> items = new ArrayList<>();
    Label resetLabel = new Label("Reset Label");
    Label valueLabel = new Label("Value Label");
    CheckBox delay = new CheckBox("Slow adding process", false);

    @Override
    protected void setup(VaadinRequest request) {

        initItems();
        comboBox = new ComboBox(null, items);
        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);
        comboBox.addValueChangeListener(event -> {
            valueLabel.setValue(comboBox.getValue());
            log("ComboBox value : " + comboBox.getValue());
        });

        configureNewItemHandling();

        Button checkButton = new Button("Check ComboBox value", e -> {
            Notification.show("selected: " + comboBox.getValue());
        });

        Button resetButton = new Button("Reset options", e -> {
            comboBox.setValue(null);
            initItems();
            comboBox.getDataProvider().refreshAll();
            resetLabel.setValue("Reset");
            valueLabel.setValue("Value is reset");
            log("DataProvider has been reset");
        });

        resetLabel.setId("reset-label");
        valueLabel.setId("value-label");
        delay.setId("delay");
        resetButton.setId("reset");

        Button button = new Button("Button for clicking only");
        button.setId("button-for-click");

        HorizontalLayout hl = new HorizontalLayout(checkButton, button);
        addComponents(comboBox, resetLabel, valueLabel, hl, resetButton, delay);
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

            comboBox.getDataProvider().refreshAll();
            log("New item has been added");
            return Optional.of(text);
        });
    }

    private void initItems() {
        items.clear();
        StringBuilder builder = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                builder.setLength(0);
                items.add(builder.append(c).append(i).toString());
            }
        }
    }

    @Override
    protected Integer getTicketNumber() {
        return 11343;
    }
}
