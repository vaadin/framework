package com.vaadin.tests.components;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class UnknownComponentConnector extends AbstractTestUI {

    public static class ComponentWithoutConnector extends AbstractComponent {

    }

    @Override
    protected void setup(VaadinRequest request) {
        ComponentWithoutConnector component = new ComponentWithoutConnector();
        component.setId("no-connector-component");
        addComponent(component);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
