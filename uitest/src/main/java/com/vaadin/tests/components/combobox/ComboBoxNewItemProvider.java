package com.vaadin.tests.components.combobox;

import java.util.Collections;
import java.util.Optional;

public class ComboBoxNewItemProvider
        extends ComboBoxSelectingNewItemValueChange {

    @Override
    protected void configureNewItemHandling() {
        comboBox.setNewItemProvider(text -> {
            if (delay.getValue()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            if (Boolean.TRUE.equals(reject.getValue())) {
                valueStateLabel.setValue("item " + text + " discarded");
                return Optional.empty();
            } else {
                items.add(text);
                Collections.sort(items);
                valueStateLabel
                        .setValue("adding new item... count: " + items.size());
                comboBox.getDataProvider().refreshAll();
                if (noSelection.getValue()) {
                    return Optional.empty();
                }
            }
            return Optional.of(text);
        });
    }
}
