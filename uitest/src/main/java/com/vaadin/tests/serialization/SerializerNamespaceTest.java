package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.DummyLabel;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class SerializerNamespaceTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("The real label"));
        addComponent(new DummyLabel("The dummy label"));
    }

    @Override
    protected String getTestDescription() {
        return "Using connectors with different state classes having the same simple name should not cause any client-side exceptions";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8683);
    }

}
