package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

/**
 * Test UI for disbaled label.
 *
 * @author Vaadin Ltd
 */
public class DisabledLabel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label enabled = new Label("enabled");
        enabled.addStyleName("my-enabled");
        addComponent(enabled);

        Label disabled = new Label("disabled");
        disabled.setEnabled(false);
        disabled.addStyleName("my-disabled");

        addComponent(disabled);
    }

    @Override
    protected Integer getTicketNumber() {
        return 15489;
    }

    @Override
    protected String getTestDescription() {
        return "Disabled label should be visibly disabled (has an opacity).";
    }

}
