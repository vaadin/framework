/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.communication.SharedState;

/**
 * Interface implemented by all client side classes that can be communicate with
 * the server. Classes implementing this interface are initialized by the
 * framework when needed and have the ability to communicate with the server.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface Connector {
    /**
     * TODO
     * 
     * @param uidl
     * @param client
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client);

    /**
     * Gets the current shared state of the connector.
     * 
     * @return state
     */
    public SharedState getState();

    /**
     * Sets a new state for the connector.
     * 
     * @param state
     *            The new state
     * 
     */
    public void setState(SharedState state);

    /**
     * Returns the id for this connector. This must always be what has been set
     * using {@link #setId(String)}.
     * 
     * @return The id for the connector.
     */
    public String getId();

    /**
     * Sets the id for the connector. This method is called once by the
     * framework when the connector is initialized and should never be called
     * otherwise.
     * <p>
     * The connector id is used to map the server and the client side together.
     * It is unique in this Root and assigned by the framework.
     * </p>
     * 
     * @param id
     *            The id of the connector.
     */
    public void setId(String id);

    /**
     * Gets ApplicationConnection instance that created this connector.
     * 
     * @return The ApplicationConnection as set by
     *         {@link #setConnection(ApplicationConnection)}
     */
    public ApplicationConnection getConnection();

    /**
     * Sets the reference to ApplicationConnection. This method is called by the
     * framework when the connector is created and should never be called
     * otherwise.
     * 
     * @param connection
     *            The ApplicationConnection that created this connector
     */
    public void setConnection(ApplicationConnection connection);

    /**
     * Tests whether the component is enabled or not. A user can not interact
     * with disabled components. Disabled components are rendered in a style
     * that indicates the status, usually in gray color. Children of a disabled
     * component are also disabled.
     * 
     * @return true if the component is enabled, false otherwise
     */
    // public boolean isEnabled();

    /**
     * 
     * Called once when the connection and id has been set.
     * 
     * Note that the shared state is not yet available during init().
     */
    public void init();

}
