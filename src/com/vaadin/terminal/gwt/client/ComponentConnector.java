/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import com.google.gwt.dom.client.Element;
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
    @Override
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
     * Checks if the connector is read only.
     * 
     * @deprecated This belongs in AbstractFieldConnector, see #8514
     * @return true
     */
    @Deprecated
    public boolean isReadOnly();

    public boolean hasEventListener(String eventIdentifier);

    /**
     * Return true if parent handles caption, false if the paintable handles the
     * caption itself.
     * 
     * <p>
     * This should always return true and all components should let the parent
     * handle the caption and use other attributes for internal texts in the
     * component
     * </p>
     * 
     * @return true if caption handling is delegated to the parent, false if
     *         parent should not be allowed to render caption
     */
    public boolean delegateCaptionHandling();

    /**
     * Sets the enabled state of the widget associated to this connector.
     * 
     * @param widgetEnabled
     *            true if the widget should be enabled, false otherwise
     */
    public void setWidgetEnabled(boolean widgetEnabled);

    /**
     * Gets the tooltip info for the given element.
     * 
     * @param element
     *            The element to lookup a tooltip for
     * @return The tooltip for the element or null if no tooltip is defined for
     *         this element.
     */
    public TooltipInfo getTooltipInfo(Element element);

}
