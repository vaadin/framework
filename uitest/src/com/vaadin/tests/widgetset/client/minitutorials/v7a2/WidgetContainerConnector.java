package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.minitutorials.v7a2.WidgetContainer;

@Connect(WidgetContainer.class)
public class WidgetContainerConnector extends
        AbstractComponentContainerConnector {

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        List<ComponentConnector> children = getChildComponents();
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

    @Override
    public void updateCaption(ComponentConnector connector) {
    }
}