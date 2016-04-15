package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ComboBoxSelecting extends AbstractTestUI {
    protected ComboBox comboBox;

    @Override
    protected void setup(VaadinRequest request) {
        comboBox = new ComboBox();
        final Label label = new Label();
        label.setId("value");

        comboBox.setTextInputAllowed(true);
        comboBox.setNullSelectionAllowed(true);
        comboBox.setNullSelectionItemId(null);

        for (char c = 'a'; c <= 'z'; c++) {
            for (int i = 0; i < 100; i++) {
                comboBox.addItem("" + c + i);
            }
        }

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                if (value != null) {
                    label.setValue(value.toString());
                } else {
                    label.setValue("null");
                }

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
