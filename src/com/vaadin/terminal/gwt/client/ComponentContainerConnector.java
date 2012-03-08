/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * An interface used by client-side connectors whose widget is a component
 * container (implements {@link HasWidgets}).
 */
public interface ComponentContainerConnector extends ComponentConnector {

    /**
     * Update child components caption, description and error message.
     * 
     * <p>
     * Each component is responsible for maintaining its caption, description
     * and error message. In most cases components doesn't want to do that and
     * those elements reside outside of the component. Because of this layouts
     * must provide service for it's childen to show those elements for them.
     * </p>
     * 
     * @param connector
     *            Child component for which service is requested.
     * @param uidl
     *            UIDL of the child component.
     */
    void updateCaption(ComponentConnector connector, UIDL uidl);

    /**
     * Returns the children for this connector.
     * <p>
     * The children for this connector are defined as all
     * {@link ComponentConnector}s whose parent is this
     * {@link ComponentContainerConnector}.
     * </p>
     * 
     * @return A collection of children for this connector. An empty collection
     *         if there are no children. Never returns null.
     */
    public Collection<ComponentConnector> getChildren();

    /**
     * Sets the children for this connector. This method should only be called
     * by the framework to ensure that the connector hierarchy on the client
     * side and the server side are in sync.
     * <p>
     * Note that calling this method does not call
     * {@link #connectorHierarchyChanged(ConnectorHierarchyChangedEvent)}. The
     * event method is called only when the hierarchy has been updated for all
     * connectors.
     * 
     * @param children
     *            The new child connectors
     */
    public void setChildren(Collection<ComponentConnector> children);

    /**
     * Called when the child connector hierarchy of this connector has changed.
     * When this method is called the full hierarchy has been updated so
     * {@link #getChildren()} returns the new child connectors of this
     * connector.
     * 
     * @param event
     *            An event containing additional information about how the
     *            hierarchy has changed.
     */
    public void connectorHierarchyChanged(ConnectorHierarchyChangedEvent event);

}
