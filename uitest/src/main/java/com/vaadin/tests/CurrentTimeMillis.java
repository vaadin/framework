package com.vaadin.tests;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;

/**
 * Test UI (empty) to check high resolution time availability in browser.
 *
 * @author Vaadin Ltd
 */
public class CurrentTimeMillis extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // no need to add anything
    }

    @Override
    protected Integer getTicketNumber() {
        return 14716;
    }

    @Override
    protected String getTestDescription() {
        return "Use high precision time is available instead of Date.getTime().";
    }
}
