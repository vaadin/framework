package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ComboBoxSelecting extends AbstractReindeerTestUI {
    protected ComboBox<String> comboBox;
    protected List<String> items = new ArrayList<>();

    @Override
    protected void setup(VaadinRequest request) {
        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                items.add("" + c + i);
            }
        }
        comboBox = new ComboBox<>(null, items);
        final Label label = new Label();
        label.setId("value");

        comboBox.setTextInputAllowed(true);
        comboBox.setEmptySelectionAllowed(true);

        comboBox.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value != null) {
                label.setValue(value);
            } else {
                label.setValue("null");
            }
        });

        // Had to add an extra text field for our old Firefox browsers, because
        // tab will otherwise send the focus to address bar and FF 24 won't fire
        // a key event properly. Nice!
        addComponents(comboBox, label, new TextField());
    }

    @Override
    protected String getTestDescription() {
        return "Clearing the filter and hitting enter should select the null item";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15502;
    }
}
