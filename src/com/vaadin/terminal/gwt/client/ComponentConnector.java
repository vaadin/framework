/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * An interface used by client-side widgets or paintable parts to receive
 * updates from the corresponding server-side components in the form of
 * {@link UIDL}.
 * 
 * Updates can be sent back to the server using the
 * {@link ApplicationConnection#updateVariable()} methods.
 */
public interface ComponentConnector extends ServerConnector {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintable#getState()
     */
    public ComponentState getState();

    /**
     * Returns the widget for this {@link ComponentConnector}
     */
    public Widget getWidget();

    public LayoutManager getLayoutManager();

    /**
     * Returns <code>true</code> if the width of this paintable is currently
     * undefined. If the width is undefined, the actual width of the paintable
     * is defined by its contents.
     * 
     * @return <code>true</code> if the width is undefined, else
     *         <code>false</code>
     */
    public boolean isUndefinedWidth();

    /**
     * Returns <code>true</code> if the height of this paintable is currently
     * undefined. If the height is undefined, the actual height of the paintable
     * is defined by its contents.
     * 
     * @return <code>true</code> if the height is undefined, else
     *         <code>false</code>
     */
    public boolean isUndefinedHeight();

    /**
     * Returns <code>true</code> if the width of this paintable is currently
     * relative. If the width is relative, the actual width of the paintable is
     * a percentage of the size allocated to it by its parent.
     * 
     * @return <code>true</code> if the width is undefined, else
     *         <code>false</code>
     */
    public boolean isRelativeWidth();

    /**
     * Returns <code>true</code> if the height of this paintable is currently
     * relative. If the height is relative, the actual height of the paintable
     * is a percentage of the size allocated to it by its parent.
     * 
     * @return <code>true</code> if the width is undefined, else
     *         <code>false</code>
     */
    public boolean isRelativeHeight();

    /**
     * Returns the parent of this connector. Can be null for only the root
     * connector.
     * 
     * @return The parent of this connector, as set by
     *         {@link #setParent(ComponentContainerConnector)}.
     */
    public ComponentContainerConnector getParent();

    /**
     * Sets the parent for this connector. This method should only be called by
     * the framework to ensure that the connector hierarchy on the client side
     * and the server side are in sync.
     * <p>
     * Note that calling this method does not fire a
     * {@link ConnectorHierarchyChangedEvent}. The event is fired only when the
     * whole hierarchy has been updated.
     * 
     * @param parent
     *            The new parent of the connector
     */
    public void setParent(ComponentContainerConnector parent);

    /**
     * Checks if the connector is read only.
     * 
     * @deprecated This belongs in AbstractFieldConnector, see #8514
     * @return true
     */
    @Deprecated
    public boolean isReadOnly();
}
