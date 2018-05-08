package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.UseStateFromHierachyComponent;

@Connect(UseStateFromHierachyComponent.class)
public class UseStateFromHierachyChangeConnector
        extends AbstractSingleComponentContainerConnector {

    @Override
    public SimplePanel getWidget() {
        return (SimplePanel) super.getWidget();
    }

    @Override
    public UseStateFromHierachyChangeConnectorState getState() {
        return (UseStateFromHierachyChangeConnectorState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
        // Caption not supported
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        Connector stateChild = getState().child;
        if (stateChild == null) {
            if (getChildComponents().size() != 0) {
                throw new IllegalStateException(
                        "Hierarchy has child but state has not");
            } else {
                getWidget().setWidget(null);
            }
        } else {
            if (getChildComponents().size() != 1
                    || getChildComponents().get(0) != stateChild) {
                throw new IllegalStateException(
                        "State has child but hierarchy has not");
            } else {
                getWidget().setWidget(getChildComponents().get(0).getWidget());
            }
        }
    }

}
