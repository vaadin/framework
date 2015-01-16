package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class TimerTriggeredTextChangeEvent extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final TextField textfield = new TextField();
        final Label serverValue = new Label();
        serverValue.setCaption("Server:");

        textfield.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                serverValue.setValue(textfield.getValue());
            }
        });

        textfield.setTextChangeEventMode(
                AbstractTextField.TextChangeEventMode.EAGER);

        addComponent(textfield);
        addComponent(serverValue);
    }

    @Override
    protected Integer getTicketNumber() {
        return 16270;
    }

    @Override
    protected String getTestDescription() {
        return "Text value in server should always be updated.";
    }
}
