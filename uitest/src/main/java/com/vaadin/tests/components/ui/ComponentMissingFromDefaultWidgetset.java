package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComponentMissingFromDefaultWidgetset
        extends AbstractReindeerTestUI {

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
