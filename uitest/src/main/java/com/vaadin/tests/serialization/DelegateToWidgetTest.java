package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.DelegateToWidgetComponent;

@Widgetset(TestingWidgetSet.NAME)
public class DelegateToWidgetTest extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new DelegateToWidgetComponent());
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that @DelegateToWidget has the desired effect";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9297);
    }

}
