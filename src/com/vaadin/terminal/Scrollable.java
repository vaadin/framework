/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.io.Serializable;

/**
 * <p>
 * This interface is implemented by all visual objects that can be scrolled
 * programmatically from the server-side, or for which it is possible to know
 * the scroll position on the server-side. The unit of scrolling is pixel.
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
     * <p>
     * The method only has effect if programmatic scrolling is enabled for the
     * scrollable. Some implementations may require enabling programmatic before
     * this method can be used. See {@link #setScrollable(boolean)} for more
     * information.
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
     * <p>
     * The method only has effect if programmatic scrolling is enabled for the
     * scrollable. Some implementations may require enabling programmatic before
     * this method can be used. See {@link #setScrollable(boolean)} for more
     * information.
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
     * @param pixelsScrolled
     *            the yOffset.
     */
    public void setScrollTop(int pixelsScrolled);

    /**
     * Is programmatic scrolling enabled.
     * 
     * <p>
     * Whether programmatic scrolling with {@link #setScrollLeft(int)} and
     * {@link #setScrollTop(int)} is enabled.
     * </p>
     * 
     * @return <code>true</code> if the scrolling is enabled, otherwise
     *         <code>false</code>.
     */
    public boolean isScrollable();

    /**
     * Enables or disables programmatic scrolling.
     * 
     * <p>
     * Enables setting the scroll position with {@link #setScrollLeft(int)} and
     * {@link #setScrollTop(int)}. Implementations of the interface may have
     * programmatic scrolling disabled by default, in which case you need to
     * enable it to use the mentioned methods.
     * </p>
     * 
     * <p>
     * Notice that this does <i>not</i> control whether scroll bars are shown
     * for a scrollable component. That normally happens automatically when the
     * content grows too big for the component, relying on the "overflow: auto"
     * property in CSS.
     * </p>
     * 
     * @param isScrollingEnabled
     *            true if the scrolling is allowed.
     */
    public void setScrollable(boolean isScrollingEnabled);

}
