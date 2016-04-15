package com.vaadin.tests.debug;

import com.vaadin.tests.components.TestBase;

public class DebugWindowPresent extends TestBase {

    @Override
    protected void setup() {
        // Nothing to set up
    }

    @Override
    protected String getDescription() {
        return "The debug window should be present with &debug present in the url, but not othervise";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7555);
    }

}
