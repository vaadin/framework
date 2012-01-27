/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

/**
 * Interface implemented by all client side classes that can be communicate with
 * the server. Classes implementing this interface are initialized by the
 * framework when needed and have the ability to communicate with the server.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public interface VPaintable {
    /**
     * TODO
     * 
     * @param uidl
     * @param client
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client);

    // /**
    // * Returns the id for this VPaintable. This must always be what has been
    // set
    // * using {@link #setId(String)}.
    // *
    // * @return The id for the VPaintable.
    // */
    // public String getId();
    //
    // /**
    // * Sets the id for the VPaintable. This method is called once by the
    // * framework when the VPaintable is initialized and should never be called
    // * otherwise.
    // * <p>
    // * The VPaintable id is used to map the server and the client paintables
    // * together. It is unique in this root and assigned by the framework.
    // * </p>
    // *
    // * @param id
    // * The id of the paintable.
    // */
    // public void setId(String id);

    /**
     * Gets ApplicationConnection instance that created this VPaintable.
     * 
     * @return The ApplicationConnection as set by
     *         {@link #setConnection(ApplicationConnection)}
     */
    // public ApplicationConnection getConnection();

    /**
     * Sets the reference to ApplicationConnection. This method is called by the
     * framework when the VPaintable is created and should never be called
     * otherwise.
     * 
     * @param connection
     *            The ApplicationConnection that created this VPaintable
     */
    // public void setConnection(ApplicationConnection connection);

    /**
     * Tests whether the component is enabled or not. A user can not interact
     * with disabled components. Disabled components are rendered in a style
     * that indicates the status, usually in gray color. Children of a disabled
     * component are also disabled.
     * 
     * @return true if the component is enabled, false otherwise
     */
    // public boolean isEnabled();
}
