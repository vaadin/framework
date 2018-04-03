package com.vaadin.tests.components.combobox;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

/**
 * Test UI to check click on icon in the combobox.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxClickIcon extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> combo = new ComboBox<>();
        combo.setItems("A", "B", "C");
        combo.setItemIconGenerator(item -> VaadinIcons.ALIGN_CENTER);
        combo.setTextInputAllowed(false);
        addComponent(combo);
    }

    @Override
    protected String getTestDescription() {
        return "Combobox icon should handle click events";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14624;
    }

}
