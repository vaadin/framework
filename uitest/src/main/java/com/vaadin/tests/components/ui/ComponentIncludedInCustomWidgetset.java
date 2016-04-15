package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class ComponentIncludedInCustomWidgetset extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MissingFromDefaultWidgetsetComponent component = new MissingFromDefaultWidgetsetComponent();
        component.setId("missing-component");
        addComponent(component);
    }

    @Override
    public String getTestDescription() {
        return "This contents if this UI should work as the component is present in TestingWidgetSet";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
