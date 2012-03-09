/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * <p>
 * This interface is implemented by all visual objects that can be scrolled
 * programmatically from the server-side. The unit of scrolling is pixel.
 * </p>
 * 
 * @author Vaadin Ltd.
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
     * @param scrollLeft
     *            the xOffset.
     */
    public void setScrollLeft(int scrollLeft);

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
     * <p>
     * The scrolling position is limited by the current height of the content
     * area. If the position is below the height, it is scrolled to the bottom.
     * However, if the same response also adds height to the content area,
     * scrolling to bottom only scrolls to the bottom of the previous content
     * area.
     * </p>
     * 
     * @param scrollTop
     *            the yOffset.
     */
    public void setScrollTop(int scrollTop);

}
