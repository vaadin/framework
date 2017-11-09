package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

public class UITabIndex extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Set tabIndex to -1", event -> setTabIndex(-1));
        addButton("Set tabIndex to 0", event -> setTabIndex(0));
        addButton("Set tabIndex to 1", event -> setTabIndex(1));
    }

    @Override
    protected String getTestDescription() {
        return "Tests tab index handling for UI";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11129;
    }

}
