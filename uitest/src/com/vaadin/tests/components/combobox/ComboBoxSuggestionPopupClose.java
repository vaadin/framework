package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPopupClose extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox select = new ComboBox("ComboBox");
        select.addItem("one");
        select.addItem("two");
        select.addItem("three");
        addComponent(select);
    }

    @Override
    protected String getTestDescription() {
        return "Closing the suggestion popup using Enter key is "
                + "broken in combobox when opening popup using Enter "
                + "key and not changin the selection using arrows";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14379;
    }
}
