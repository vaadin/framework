/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentContainerConnector;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public abstract class AbstractComponentContainerConnector extends
        AbstractComponentConnector implements ComponentContainerConnector,
        ConnectorHierarchyChangeHandler {

    public interface LayoutClickRPC extends ServerRpc {
        /**
         * Called when a layout click event has occurred and there are server
         * side listeners for the event.
         * 
         * @param mouseDetails
         *            Details about the mouse when the event took place
         * @param clickedConnector
         *            The child component that was the target of the event
         */
        public void layoutClick(MouseEventDetails mouseDetails,
                Connector clickedConnector);
    }

    List<ComponentConnector> children;

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
        // TODO Remove debug info
        VConsole.log("Hierarchy changed for " + Util.getConnectorString(this));
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

    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager().addHandler(
                ConnectorHierarchyChangeEvent.TYPE, handler);
    }

}
