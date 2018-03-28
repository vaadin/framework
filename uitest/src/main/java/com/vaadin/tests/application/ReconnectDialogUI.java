package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.ui.Button;

public class ReconnectDialogUI extends AbstractReindeerTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        if (request.getParameter("reconnectAttempts") != null) {
            getReconnectDialogConfiguration().setReconnectAttempts(Integer
                    .parseInt(request.getParameter("reconnectAttempts")));
        }
        Button b = new Button("Say hello");
        b.addClickListener(event -> log("Hello from the server"));

        addComponent(b);
    }

}
