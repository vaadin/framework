/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;

public abstract class AbstractComponentContainerConnector extends
        AbstractComponentConnector implements ComponentContainerConnector,
        ConnectorHierarchyChangeHandler {

    List<ComponentConnector> childComponents;

    private final boolean debugLogging = false;

    /**
     * Default constructor
     */
    public AbstractComponentContainerConnector() {
        addConnectorHierarchyChangeHandler(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ComponentContainerConnector#getChildren()
     */
    public List<ComponentConnector> getChildComponents() {
        if (childComponents == null) {
            return Collections.emptyList();
        }

        return childComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ComponentContainerConnector#setChildren
     * (java.util.Collection)
     */
    public void setChildComponents(List<ComponentConnector> childComponents) {
        this.childComponents = childComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentContainerConnector#
     * connectorHierarchyChanged
     * (com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent)
     */
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        if (debugLogging) {
            VConsole.log("Hierarchy changed for "
                    + Util.getConnectorString(this));
            String oldChildren = "* Old children: ";
            for (ComponentConnector child : event.getOldChildren()) {
                oldChildren += Util.getConnectorString(child) + " ";
            }
            VConsole.log(oldChildren);

            String newChildren = "* New children: ";
            for (ComponentConnector child : getChildComponents()) {
                newChildren += Util.getConnectorString(child) + " ";
            }
            VConsole.log(newChildren);
        }
    }

    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager().addHandler(
                ConnectorHierarchyChangeEvent.TYPE, handler);
    }
}
