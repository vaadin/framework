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
}
