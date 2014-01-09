package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

public class ComponentMissingFromDefaultWidgetset extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MissingFromDefaultWidgetsetComponent component = new MissingFromDefaultWidgetsetComponent();
        component.setId("missing-component");
        addComponent(component);
    }

    @Override
    public String getTestDescription() {
        return "This contents if this UI should not work as the component is not present in DefaultWidgetSet";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
