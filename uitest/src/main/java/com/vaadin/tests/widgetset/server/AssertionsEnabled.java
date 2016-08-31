package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.AssertionFailureWidget;

@Widgetset(TestingWidgetSet.NAME)
public class AssertionsEnabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new TestWidgetComponent(AssertionFailureWidget.class));
    }
}
