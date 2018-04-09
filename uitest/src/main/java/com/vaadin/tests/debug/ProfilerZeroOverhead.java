package com.vaadin.tests.debug;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.ProfilerCompilationCanary;
import com.vaadin.tests.widgetset.server.TestWidgetComponent;

@Widgetset(TestingWidgetSet.NAME)
public class ProfilerZeroOverhead extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new TestWidgetComponent(ProfilerCompilationCanary.class));
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for verifying that Profiler.isEnabled() causes no side effects in generated javascript";
    }
}
