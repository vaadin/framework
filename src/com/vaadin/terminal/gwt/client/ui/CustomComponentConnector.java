/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent;

public class CustomComponentConnector extends
        AbstractComponentContainerConnector {

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomComponent.class);
    }

    @Override
    public VCustomComponent getWidget() {
        return (VCustomComponent) super.getWidget();
    }

    public void updateCaption(ComponentConnector component) {
        // NOP, custom component dont render composition roots caption
    }

    @Override
    public void connectorHierarchyChanged(ConnectorHierarchyChangedEvent event) {
        super.connectorHierarchyChanged(event);

        ComponentConnector newChild = null;
        if (getChildren().size() == 1) {
            newChild = getChildren().get(0);
        }

        VCustomComponent customComponent = getWidget();
        customComponent.setWidget(newChild.getWidget());

    }
}
