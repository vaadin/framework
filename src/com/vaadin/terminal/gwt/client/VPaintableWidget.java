/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.MeasureManager.MeasuredSize;

/**
 * An interface used by client-side widgets or paintable parts to receive
 * updates from the corresponding server-side components in the form of
 * {@link UIDL}.
 * 
 * Updates can be sent back to the server using the
 * {@link ApplicationConnection#updateVariable()} methods.
 */
public interface VPaintableWidget extends VPaintable {

    /**
     * TODO: Rename to getWidget
     */
    public Widget getWidgetForPaintable();

    /**
     * Returns the parent {@link VPaintableWidgetContainer}
     * 
     * @return
     */
    // FIXME: Rename to getParent()
    public VPaintableWidgetContainer getParent();

    public MeasuredSize getMeasuredSize();

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
    public String getDefinedWidth();

    /**
     * Gets the height of this paintable as defined on the server.
     * 
     * @return the server side height definition
     */
    public String getDefinedHeight();
}
