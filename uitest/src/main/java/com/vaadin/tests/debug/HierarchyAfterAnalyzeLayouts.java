package com.vaadin.tests.debug;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class HierarchyAfterAnalyzeLayouts extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("This is a label"));
    }

    @Override
    protected String getTestDescription() {
        return "The connector hierarchy should be in order after clicking AL in the debug console";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11067);
    }

}
