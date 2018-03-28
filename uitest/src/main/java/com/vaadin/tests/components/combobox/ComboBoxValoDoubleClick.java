package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxValoDoubleClick extends AbstractTestUI {

    // Quite impossible to autotest reliably as there must be a click to open
    // the popup and another click during the opening animation to reproduce the
    // bug. Manually a double click is just about the right timing.
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> cb = new ComboBox<>("Double-click Me");
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add("Item-" + i);
        }
        cb.setItems(items);
        addComponent(cb);
    }

    @Override
    public String getTestDescription() {
        return "ComboBox should remain usable even after double-clicking (affects only Valo theme with $v-overlay-animate-in).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17903;
    }

}
