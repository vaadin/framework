package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboboxSelectedItemText extends AbstractReindeerTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().addComponent(new Label(
                "Select first ANTIGUA AND BARBUDA from the first combobox. Then select ANTIGUA AND BARBUDA from the second combobox. Finally, click the popup button on the first combobox. Before fix you would see UA AND BAR in the field."));

        ComboBox<String> combobox = new ComboBox<>("Text input enabled:");
        combobox.setWidth("100px");

        combobox.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA");

        ComboBox<String> combobox2 = new ComboBox<>("Text input disabled:");
        combobox2.setWidth("100px");
        combobox2.setTextInputAllowed(false);

        combobox2.setItems("AMERICAN SAMOA", "ANTIGUA AND BARBUDA");

        getLayout().addComponent(combobox);
        getLayout().addComponent(combobox2);
    }

    @Override
    protected String getTestDescription() {
        return "Tests selected item is displayed from the beginning";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13477;
    }

}
