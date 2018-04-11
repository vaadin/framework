package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

public class ComboBoxInputPrompt extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> cb1 = new ComboBox<String>("Normal");
        cb1.setPlaceholder("Normal input prompt");

        final ComboBox<String> cb2 = new ComboBox<String>("Disabled");
        cb2.setEnabled(false);
        cb2.setPlaceholder("Disabled input prompt");

        final ComboBox<String> cb3 = new ComboBox<String>("Read-only");
        cb3.setReadOnly(true);
        cb3.setPlaceholder("Read-only input prompt");

        Button enableButton = new Button("Toggle enabled", event -> {
            cb2.setEnabled(!cb2.isEnabled());
            cb3.setReadOnly(!cb3.isReadOnly());
        });

        addComponents(cb1, cb2, cb3, enableButton);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should not display the input prompt if disabled or read-only.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10573;
    }

}
