/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal;

import java.io.Serializable;

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
public interface Scrollable extends Serializable {

    /**
     * Gets scroll left offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled right.
     * </p>
     * 
     * @return Horizontal scrolling position in pixels.
     */
    public int getScrollLeft();

    /**
     * Sets scroll left offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled right.
     * </p>
     * 
     * @param pixelsScrolled
     *            the xOffset.
     */
    public void setScrollLeft(int pixelsScrolled);

    /**
     * Gets scroll top offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled down.
     * </p>
     * 
     * @return Vertical scrolling position in pixels.
     */
    public int getScrollTop();

    /**
     * Sets scroll top offset.
     * 
     * <p>
     * Scrolling offset is the number of pixels this scrollable has been
     * scrolled down.
     * </p>
     * 
     * @param pixelsScrolled
     *            the yOffset.
     */
    public void setScrollTop(int pixelsScrolled);

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
     *            true if the scrolling is allowed.
     */
    public void setScrollable(boolean isScrollingEnabled);

}
