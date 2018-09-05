package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;

@Widgetset(TestingWidgetSet.NAME)
public class ReplaceComponentUI extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new ReplaceComponent());
    }

    @Override
    protected String getTestDescription() {
        return "Tests that the right client-side connector is used when there are multiple connectors with @Connect mappings to the same server-side component.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9826);
    }

}
