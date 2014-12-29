package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPage extends
        AbstractTestUI {

    protected static final int ITEM_COUNT = 21;
    protected static final String ITEM_NAME_TEMPLATE = "Item %d";

    @Override
    protected void setup(VaadinRequest request) {
        Label value = getLabel();
        ComboBox combobox = getComboBox(value);

        addComponent(combobox);
        addComponent(value);
    }

    private Label getLabel() {
        final Label value = new Label();
        value.setId("value");

        return value;
    }

    private ComboBox getComboBox(final Label value) {
        final ComboBox combobox = new ComboBox("MyCaption");
        combobox.setDescription("ComboBox with more than 10 elements in it's dropdown list.");

        combobox.setImmediate(true);

        for (int i = 1; i <= ITEM_COUNT; i++) {
            combobox.addItem(String.format(ITEM_NAME_TEMPLATE, i));
        }

        combobox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                value.setValue(String.valueOf(event.getProperty().getValue()));
            }
        });

        return combobox;
    }

    @Override
    protected String getTestDescription() {
        return "Test for ensuring that ComboBox shows selected value beyound the first dropdown page";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10600;
    }
}
