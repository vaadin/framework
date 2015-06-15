package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;

public class RemoveTextChangeListener extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final TextField textfield = new TextField();

        textfield.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                textfield.removeTextChangeListener(this);
            }
        });

        addComponent(textfield);
    }

    @Override
    protected Integer getTicketNumber() {
        return 16270;
    }

    @Override
    protected String getTestDescription() {
        return "Removing text change listener on text change event should not reset the input.";
    }
}
