package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.FormLayout;

/**
 * Test UI for FormLayout: custom additional styles should be prefixed with
 * "v-formlayout-", not "v-layout-".
 *
 * @author Vaadin Ltd
 */
public class StylePrefix extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        FormLayout layout = new FormLayout();
        layout.addStyleName("mystyle");
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Form layout should set v-formlayout style name instead of v-layout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13509;
    }

}
