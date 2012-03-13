/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.List;

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
public class ConnectorHierarchyChangedEvent {
    List<ComponentConnector> oldChildren;
    private ComponentContainerConnector parent;

    public ConnectorHierarchyChangedEvent() {
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

}
