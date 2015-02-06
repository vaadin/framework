package com.vaadin.tests.themes;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;

public class ButtonTextOverflow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Button Button Button");

        button.setWidth("100px");

        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Overflowing button caption should be hidden with ellipsis.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11864;
    }
}
