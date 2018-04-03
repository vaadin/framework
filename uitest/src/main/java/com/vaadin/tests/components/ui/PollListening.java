package com.vaadin.tests.components.ui;

import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class PollListening extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label statusLabel = new Label("Default Label");
        addComponent(statusLabel);

        setPollInterval(2000);
        addPollListener(new PollListener() {
            @Override
            public void poll(PollEvent event) {
                setPollInterval(-1);
                statusLabel.setValue(
                        event.getClass().getSimpleName() + " received");
                removePollListener(this);
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Polling should fire a PollEvent on the server-side";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12466;
    }

}
