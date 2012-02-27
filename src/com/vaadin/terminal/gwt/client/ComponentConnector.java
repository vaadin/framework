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
public interface ComponentConnector extends Connector {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintable#getState()
     */
    public ComponentState getState();

    /**
     * TODO: Rename to getWidget
     */
    public Widget getWidget();

    /**
     * Returns the parent {@link ComponentContainerConnector}
     * 
     * @return
     */
    public ComponentContainerConnector getParent();

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
     * Gets the width of this paintable as defined on the server.
     * 
     * @return the server side width definition
     */
    public String getDeclaredWidth();

    /**
     * Gets the height of this paintable as defined on the server.
     * 
     * @return the server side height definition
     */
    public String getDeclaredHeight();
}
