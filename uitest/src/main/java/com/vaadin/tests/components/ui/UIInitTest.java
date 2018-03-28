package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class UIInitTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("Hello UI"));
    }

    @Override
    public String getTestDescription() {
        return "Testing basic UI creation";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(3067);
    }
}
