package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Widgetset;
import com.vaadin.tests.widgetset.TestingWidgetSet;

import java.util.Collections;
import java.util.Optional;

@Widgetset(TestingWidgetSet.NAME)
public class ComboBoxNewItemProvider
        extends ComboBoxSelectingNewItemValueChange {

    @Override
    protected void configureNewItemHandling() {
        comboBox.addValueChangeListener(e -> {
            System.out.println("e.getValue() = " + e.getValue());
        });
        comboBox.setNewItemProvider(text -> {
            if (delay.getValue()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if (Boolean.TRUE.equals(reject.getValue())) {
                valueChangeLabel.setValue("item " + text + " discarded");
                return Optional.empty();
            } else {
                items.add(text);
                Collections.sort(items);
                valueChangeLabel
                        .setValue("adding new item... count: " + items.size());
                comboBox.getDataProvider().refreshAll();
                if (Boolean.TRUE.equals(noSelection.getValue())) {
                    return Optional.empty();
                }
            }
            return Optional.of(text);
        });
    }
}
