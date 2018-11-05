package com.vaadin.tests.application;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class DetachOldUIOnReload extends AbstractTestUIWithLog {
    private static final String PERSISTENT_MESSAGES_ATTRIBUTE = DetachOldUIOnReload.class
            .getName() + ".sessionMessages";

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("This is UI " + getUIId()));
        addComponent(new Button("Reload page", event -> getPage().reload()));
        addComponent(new Button("Read log messages from session", event -> {
            for (String message : getSessionMessages(false)) {
                log(message);
            }
        }));
    }

    private List<String> getSessionMessages(boolean storeIfNeeded) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) getSession()
                .getAttribute(PERSISTENT_MESSAGES_ATTRIBUTE);
        if (messages == null) {
            messages = new ArrayList<>();
            if (storeIfNeeded) {
                getSession().setAttribute(PERSISTENT_MESSAGES_ATTRIBUTE,
                        messages);
            }
        }
        return messages;
    }

    private void logToSession(String message) {
        getSessionMessages(true).add(message);
    }

    @Override
    public void detach() {
        super.detach();
        logToSession("UI " + getUIId() + " has been detached");
    }

    @Override
    protected String getTestDescription() {
        return "Tests that the previous UI gets cleaned immediately when refreshing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10338);
    }

}
