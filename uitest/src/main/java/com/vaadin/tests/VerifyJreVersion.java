package com.vaadin.tests;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class VerifyJreVersion extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String jreVersion = "Using Java " + System.getProperty("java.version")
                + " by " + System.getProperty("java.vendor");
        Label jreVersionLabel = new Label(jreVersion);
        jreVersionLabel.setId("jreVersionLabel");

        addComponent(jreVersionLabel);
    }

    @Override
    protected String getTestDescription() {
        return "Test used to detect when the JRE used to run these tests have changed.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11835);
    }

}
