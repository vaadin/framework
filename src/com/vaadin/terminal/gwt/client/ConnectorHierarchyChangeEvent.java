/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.terminal.gwt.client.communication.AbstractServerConnectorEvent;

/**
 * Event for containing data related to a change in the {@link ServerConnector}
 * hierarchy. A {@link ConnectorHierarchyChangedEvent} is fired when an update
 * from the server has been fully processed and all hierarchy updates have been
 * completed.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public class ConnectorHierarchyChangeEvent extends
        AbstractServerConnectorEvent<ConnectorHierarchyChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<ConnectorHierarchyChangeHandler> TYPE = new Type<ConnectorHierarchyChangeHandler>();

    List<ComponentConnector> oldChildren;
    private ComponentContainerConnector parent;

    public ConnectorHierarchyChangeEvent() {
    }

    /**
     * Returns a collection of the old children for the connector. This was the
     * state before the update was received from the server.
     * 
     * @return A collection of old child connectors. Never returns null.
     */
    public List<ComponentConnector> getOldChildren() {
        return oldChildren;
    }

    /**
     * Sets the collection of the old children for the connector.
     * 
     * @param oldChildren
     *            The old child connectors. Must not be null.
     */
    public void setOldChildren(List<ComponentConnector> oldChildren) {
        this.oldChildren = oldChildren;
    }

    /**
     * Returns the {@link ComponentContainerConnector} for which this event
     * occurred.
     * 
     * @return The {@link ComponentContainerConnector} whose child collection
     *         has changed. Never returns null.
     */
    public ComponentContainerConnector getParent() {
        return parent;
    }

    /**
     * Sets the {@link ComponentContainerConnector} for which this event
     * occurred.
     * 
     * @param The
     *            {@link ComponentContainerConnector} whose child collection has
     *            changed.
     */
    public void setParent(ComponentContainerConnector parent) {
        this.parent = parent;
    }

    public interface ConnectorHierarchyChangeHandler extends Serializable,
            EventHandler {
        public void onConnectorHierarchyChange(
                ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent);
    }

    @Override
    public void dispatch(ConnectorHierarchyChangeHandler handler) {
        handler.onConnectorHierarchyChange(this);
    }

    @Override
    public GwtEvent.Type<ConnectorHierarchyChangeHandler> getAssociatedType() {
        return TYPE;
    }

}