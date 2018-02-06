package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.combobox.ComboBoxClientRpc;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

public class ComboBoxSelectingNewItemValueChange extends ComboBoxSelecting {

    private final class CustomComboBox extends ComboBox<String> {
        private CustomComboBox(String caption, Collection<String> options) {
            super(caption, options);
        }

        public ComboBoxClientRpc getComboBoxClientRpc() {
            return getRpcProxy(ComboBoxClientRpc.class);
        }
    }

    CustomComboBox comboBox;
    List<String> items = new ArrayList<>();
    int valueChangeEventCount = 0;
    int selectionChangeEventCount = 0;
    Label valueLabel = new Label();
    Label valueChangeLabel = new Label(null, ContentMode.HTML);
    CheckBox delay = new CheckBox("Slow adding process", false);
    CheckBox reject = new CheckBox("Reject new values", false);
    CheckBox noSelection = new CheckBox("Don't select new values", false);

    @Override
    protected void setup(VaadinRequest request) {
        initItems();
        comboBox = new CustomComboBox(null, items);
        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);

        comboBox.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value != null) {
                valueLabel.setValue(value);
            } else {
                valueLabel.setValue("null");
            }
        });

        comboBox.setNewItemHandler(text -> {
            if (Boolean.TRUE.equals(delay.getValue())) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if (Boolean.TRUE.equals(reject.getValue())) {
                valueChangeLabel.setValue("item " + text + " discarded");
                comboBox.getComboBoxClientRpc().newItemNotAdded(text);
            } else {
                items.add(text);
                Collections.sort(items);
                valueChangeLabel
                        .setValue("adding new item... count: " + items.size());
                if (Boolean.TRUE.equals(noSelection.getValue())) {
                    comboBox.getComboBoxClientRpc().newItemNotAdded(text);
                }
                comboBox.getDataProvider().refreshAll();
            }
        });

        comboBox.addValueChangeListener(e -> {
            ++valueChangeEventCount;
            updateLabel(e.isUserOriginated());
        });

        comboBox.addSelectionListener(e -> {
            ++selectionChangeEventCount;
            updateLabel(e.isUserOriginated());
        });

        Button checkButton = new Button("Check ComboBox value", e -> {
            Notification.show("selected: " + comboBox.getValue());
        });

        Button resetButton = new Button("Reset options", e -> {
            comboBox.setValue(null);
            initItems();
            comboBox.getDataProvider().refreshAll();
            valueLabel.setValue("");
            valueChangeLabel.setValue("Reset");
            valueChangeEventCount = 0;
            selectionChangeEventCount = 0;
        });

        valueLabel.setId("value");
        valueChangeLabel.setId("change");
        delay.setId("delay");
        reject.setId("reject");
        noSelection.setId("noSelection");
        resetButton.setId("reset");

        addComponents(comboBox, valueLabel, valueChangeLabel, checkButton,
                resetButton, delay, reject, noSelection);
    }

    private void initItems() {
        items.clear();
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                items.add("" + c + i);
            }
        }
    }

    private void updateLabel(boolean userOriginated) {
        valueChangeLabel.setValue("Value change count: " + valueChangeEventCount
                + "\nSelection change count: " + selectionChangeEventCount
                + "\nuser originated: " + userOriginated);
    }

    @Override
    protected String getTestDescription() {
        return "New item should trigger value change when accepted "
                + "and restore the field to previous value when rejected.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10284;
    }
}
