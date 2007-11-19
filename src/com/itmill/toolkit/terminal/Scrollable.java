/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.terminal;

/**
 * <p>
 * This interface is implemented by all visual objects that can be scrolled. The
 * unit of scrolling is pixel.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public interface Scrollable {

    /**
     * Gets scroll X offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled to left.
     * </p>
     * 
     * @return Horizontal scrolling position in pixels.
     */
    public int getScrollOffsetX();

    /**
     * Sets scroll X offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled to left.
     * </p>
     * 
     * @param pixelsScrolledLeft
     *                the xOffset.
     */
    public void setScrollOffsetX(int pixelsScrolledLeft);

    /**
     * Gets scroll Y offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled to down.
     * </p>
     * 
     * @return Vertical scrolling position in pixels.
     */
    public int getScrollOffsetY();

    /**
     * Sets scroll Y offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled to down.
     * </p>
     * 
     * @param pixelsScrolledDown
     *                the yOffset.
     */
    public void setScrollOffsetY(int pixelsScrolledDown);

    /**
     * Is the scrolling enabled.
     * 
     * <p>
     * Enabling scrolling allows the user to scroll the scrollable view
     * interactively
     * </p>
     * 
     * @return <code>true</code> if the scrolling is allowed, otherwise
     *         <code>false</code>.
     */
    public boolean isScrollable();

    /**
     * Enables or disables scrolling..
     * 
     * <p>
     * Enabling scrolling allows the user to scroll the scrollable view
     * interactively
     * </p>
     * 
     * @param isScrollingEnabled
     *                true if the scrolling is allowed.
     */
    public void setScrollable(boolean isScrollingEnabled);

}
