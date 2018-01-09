package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxPasteWithDisabled extends AbstractTestUI {

    @Override
    public String getDescription() {
        return "Paste from Clipboard should not open the popup of a disabled ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7898;
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setEnabled(false);
        addComponent(comboBox);
    }

}
