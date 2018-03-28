package com.vaadin.tests.widgetset.client;

import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.DelegateToWidgetComponent;

@Connect(DelegateToWidgetComponent.class)
public class DelegateConnector extends AbstractComponentConnector {
    @Override
    public DelegateWidget getWidget() {
        return (DelegateWidget) super.getWidget();
    }

    @Override
    public DelegateState getState() {
        return (DelegateState) super.getState();
    }
}
