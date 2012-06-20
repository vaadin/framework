package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.WidgetContainer;

@Connect(WidgetContainer.class)
public class WidgetContainerConnector extends
        AbstractComponentContainerConnector {

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        List<ComponentConnector> children = getChildren();
        VWidgetContainer widget = (VWidgetContainer) getWidget();
        widget.clear();
        for (ComponentConnector connector : children) {
            widget.add(connector.getWidget());
        }
        super.onConnectorHierarchyChange(event);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VWidgetContainer.class);
    }

    public void updateCaption(ComponentConnector connector) {
    }
}