package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPage
        extends AbstractReindeerTestUI {

    protected static final int ITEM_COUNT = 21;
    protected static final String ITEM_NAME_TEMPLATE = "Item %d";

    @Override
    protected void setup(VaadinRequest request) {
        Label value = getLabel();
        ComboBox<String> combobox = getComboBox(value);

        addComponent(combobox);
        addComponent(value);
    }

    private Label getLabel() {
        final Label value = new Label();
        value.setId("value");

        return value;
    }

    private ComboBox<String> getComboBox(final Label value) {
        final ComboBox<String> combobox = new ComboBox<>("MyCaption");
        combobox.setDescription(
                "ComboBox with more than 10 elements in it's dropdown list.");

        List<String> items = new ArrayList<>();
        for (int i = 1; i <= ITEM_COUNT; i++) {
            items.add(String.format(ITEM_NAME_TEMPLATE, i));
        }
        combobox.setItems(items);

        combobox.addValueChangeListener(
                event -> value.setValue(String.valueOf(event.getValue())));

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
