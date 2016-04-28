/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.shared.AbstractComponentState;

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
     * @see com.vaadin.client.VPaintable#getState()
     */
    @Override
    public AbstractComponentState getState();

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
     * <p>
     * When overriding this method, {@link #hasTooltip()} should also be
     * overridden to return <code>true</code> in all situations where this
     * method might return a non-empty result.
     * </p>
     * 
     * @param element
     *            The element to lookup a tooltip for
     * @return The tooltip for the element or null if no tooltip is defined for
     *         this element.
     */
    public TooltipInfo getTooltipInfo(Element element);

    /**
     * Check whether there might be a tooltip for this component. The framework
     * will only add event listeners for automatically handling tooltips (using
     * {@link #getTooltipInfo(Element)}) if this method returns true.
     * <p>
     * This is only done to optimize performance, so in cases where the status
     * is not known, it's safer to return <code>true</code> so that there will
     * be a tooltip handler even though it might not be needed in all cases.
     * 
     * @return <code>true</code> if some part of the component might have a
     *         tooltip, otherwise <code>false</code>
     */
    public boolean hasTooltip();

    /**
     * Called for the active (focused) connector when a situation occurs that
     * the focused connector might have buffered changes which need to be
     * processed before other activity takes place.
     * <p>
     * This is currently called when the user changes the fragment using the
     * back/forward button in the browser and allows the focused field to submit
     * its value to the server before the fragment change event takes place.
     * </p>
     */
    public void flush();

}
