package com.vaadin.tests.serialization;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;

public class ChangeStateWhenReattaching extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Reattach and remove caption", event -> {
            Button b = event.getButton();
            removeComponent(b);
            addComponent(b);
            b.setCaption(null);
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking the button should remove its caption, even though it is also reattached.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10532);
    }

}
