package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.GenericWidgetComponent;

@Connect(GenericWidgetComponent.class)
public class GenericWidgetConnector extends AbstractComponentConnector {
    @Override
    public GenericWidget<String> getWidget() {
        return (GenericWidget<String>) super.getWidget();
    }

    @Override
    public GenericWidgetState getState() {
        return (GenericWidgetState) super.getState();
    }
}
