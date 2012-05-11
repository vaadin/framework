/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.LinkedList;
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

    List<ComponentConnector> children;

    private final boolean debugLogging = false;

    /**
     * Temporary storage for last enabled state to be able to see if it has
     * changed. Can be removed once we are able to listen specifically for
     * enabled changes in the state. Widget.isEnabled() cannot be used as all
     * Widgets do not implement HasEnabled
     */
    private boolean lastWidgetEnabledState = true;

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
    public List<ComponentConnector> getChildren() {
        if (children == null) {
            return new LinkedList<ComponentConnector>();
        }

        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ComponentContainerConnector#setChildren
     * (java.util.Collection)
     */
    public void setChildren(List<ComponentConnector> children) {
        this.children = children;
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
            for (ComponentConnector child : getChildren()) {
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

    @Override
    public void setWidgetEnabled(boolean widgetEnabled) {
        if (lastWidgetEnabledState == widgetEnabled) {
            return;
        }
        lastWidgetEnabledState = widgetEnabled;

        super.setWidgetEnabled(widgetEnabled);
        for (ComponentConnector c : getChildren()) {
            // Update children as they might be affected by the enabled state of
            // their parent
            c.setWidgetEnabled(c.isEnabled());
        }
    }
}
