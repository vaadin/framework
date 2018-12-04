package com.vaadin.tests.applicationcontext;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

/**
 * Tests that UI is cleaned from session despite any errors that happen in
 * detach.
 *
 * @author Vaadin Ltd
 */
public class CleanupBrokenUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        logUIs();
        addComponent(new Label("Label with broken detach") {
            @Override
            public void detach() {
                throw new IllegalStateException(
                        "Detach does not work for this component");
            }
        });

        addComponent(new Button("Ping", event -> log("pong")));
    }

    private void logUIs() {
        log("UIs in session: " + getSession().getUIs().size());
    }

    @Override
    protected String getTestDescription() {
        return "Open the page as http://localhost:8888/run/CleanupBrokenUI, then refresh the page to get a new UI. On refresh there should be an IllegalStateException in the server log but pressing 'ping' after this should log no further messages and the old ui should no longer be in the VaadinSession";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16651;
    }
}
