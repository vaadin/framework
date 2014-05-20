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

package com.vaadin.client.ui.grid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;

/**
 * An element-like bundle representing a configurable and visual scrollbar in
 * one axis.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see VerticalScrollbarBundle
 * @see HorizontalScrollbarBundle
 */
abstract class ScrollbarBundle {

    /**
     * A means to listen to when the scrollbar handle in a
     * {@link ScrollbarBundle} either appears or is removed.
     */
    public interface VisibilityHandler extends EventHandler {
        /**
         * This method is called whenever the scrollbar handle's visibility is
         * changed in a {@link ScrollbarBundle}.
         * 
         * @param event
         *            the {@link VisibilityChangeEvent}
         */
        void visibilityChanged(VisibilityChangeEvent event);
    }

    public static class VisibilityChangeEvent extends
            GwtEvent<VisibilityHandler> {
        public static final Type<VisibilityHandler> TYPE = new Type<ScrollbarBundle.VisibilityHandler>() {
            @Override
            public String toString() {
                return "VisibilityChangeEvent";
            }
        };

        private final boolean isScrollerVisible;

        private VisibilityChangeEvent(boolean isScrollerVisible) {
            this.isScrollerVisible = isScrollerVisible;
        }

        /**
         * Checks whether the scroll handle is currently visible or not
         * 
         * @return <code>true</code> if the scroll handle is currently visible.
         *         <code>false</code> if not.
         */
        public boolean isScrollerVisible() {
            return isScrollerVisible;
        }

        @Override
        public Type<VisibilityHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(VisibilityHandler handler) {
            handler.visibilityChanged(this);
        }
    }

    /**
     * The pixel size for OSX's invisible scrollbars.
     * <p>
     * Touch devices don't show a scrollbar at all, so the scrollbar size is
     * irrelevant in their case. There doesn't seem to be any other popular
     * platforms that has scrollbars similar to OSX. Thus, this behavior is
     * tailored for OSX only, until additional platforms start behaving this
     * way.
     */
    private static final int OSX_INVISIBLE_SCROLLBAR_FAKE_SIZE_PX = 13;

    /**
     * The allowed value inaccuracy when comparing two double-typed pixel
     * values.
     * <p>
     * Since we're comparing pixels on a screen, epsilon must be less than 1.
     * 0.49 was deemed a perfectly fine and beautifully round number.
     */
    private static final double PIXEL_EPSILON = 0.49d;

    /**
     * A representation of a single vertical scrollbar.
     * 
     * @see VerticalScrollbarBundle#getElement()
     */
    final static class VerticalScrollbarBundle extends ScrollbarBundle {

        @Override
        public void setStylePrimaryName(String primaryStyleName) {
            super.setStylePrimaryName(primaryStyleName);
            root.addClassName(primaryStyleName + "-scroller-vertical");
        }

        @Override
        protected void internalSetScrollPos(int px) {
            root.setScrollTop(px);
        }

        @Override
        protected int internalGetScrollPos() {
            return root.getScrollTop();
        }

        @Override
        protected void internalSetScrollSize(int px) {
            scrollSizeElement.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        protected int internalGetScrollSize() {
            return scrollSizeElement.getOffsetHeight();
        }

        @Override
        protected void internalSetOffsetSize(double px) {
            root.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        public double getOffsetSize() {
            return root.getOffsetHeight();
        }

        @Override
        protected void internalSetScrollbarThickness(int px) {
            root.getStyle().setWidth(px, Unit.PX);
            scrollSizeElement.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        protected int internalGetScrollbarThickness() {
            return root.getOffsetWidth();
        }

        @Override
        protected void forceScrollbar(boolean enable) {
            if (enable) {
                root.getStyle().setOverflowY(Overflow.SCROLL);
            } else {
                root.getStyle().clearOverflowY();
            }
        }
    }

    /**
     * A representation of a single horizontal scrollbar.
     * 
     * @see HorizontalScrollbarBundle#getElement()
     */
    final static class HorizontalScrollbarBundle extends ScrollbarBundle {

        @Override
        public void setStylePrimaryName(String primaryStyleName) {
            super.setStylePrimaryName(primaryStyleName);
            root.addClassName(primaryStyleName + "-scroller-horizontal");
        }

        @Override
        protected void internalSetScrollPos(int px) {
            root.setScrollLeft(px);
        }

        @Override
        protected int internalGetScrollPos() {
            return root.getScrollLeft();
        }

        @Override
        protected void internalSetScrollSize(int px) {
            scrollSizeElement.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        protected int internalGetScrollSize() {
            return scrollSizeElement.getOffsetWidth();
        }

        @Override
        protected void internalSetOffsetSize(double px) {
            root.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        public double getOffsetSize() {
            return root.getOffsetWidth();
        }

        @Override
        protected void internalSetScrollbarThickness(int px) {
            root.getStyle().setHeight(px, Unit.PX);
            scrollSizeElement.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        protected int internalGetScrollbarThickness() {
            return root.getOffsetHeight();
        }

        @Override
        protected void forceScrollbar(boolean enable) {
            if (enable) {
                root.getStyle().setOverflowX(Overflow.SCROLL);
            } else {
                root.getStyle().clearOverflowX();
            }
        }
    }

    protected final Element root = DOM.createDiv();
    protected final Element scrollSizeElement = DOM.createDiv();
    protected boolean isInvisibleScrollbar = false;

    private double scrollPos = 0;
    private double maxScrollPos = 0;

    private boolean scrollHandleIsVisible = false;

    /** @deprecarted access via {@link #getHandlerManager()} instead. */
    @Deprecated
    private HandlerManager handlerManager;

    private ScrollbarBundle() {
        root.appendChild(scrollSizeElement);
    }

    protected abstract int internalGetScrollSize();

    /**
     * Sets the primary style name
     * 
     * @param primaryStyleName
     *            The primary style name to use
     */
    public void setStylePrimaryName(String primaryStyleName) {
        root.setClassName(primaryStyleName + "-scroller");
    }

    /**
     * Gets the root element of this scrollbar-composition.
     * 
     * @return the root element
     */
    public final Element getElement() {
        return root;
    }

    /**
     * Modifies the scroll position of this scrollbar by a number of pixels.
     * <p>
     * <em>Note:</em> Even though {@code double} values are used, they are
     * currently only used as integers as large {@code int} (or small but fast
     * {@code long}). This means, all values are truncated to zero decimal
     * places.
     * 
     * @param delta
     *            the delta in pixels to change the scroll position by
     */
    public final void setScrollPosByDelta(double delta) {
        if (delta != 0) {
            setScrollPos(getScrollPos() + delta);
        }
    }

    /**
     * Modifies {@link #root root's} dimensions in the axis the scrollbar is
     * representing.
     * 
     * @param px
     *            the new size of {@link #root} in the dimension this scrollbar
     *            is representing
     */
    protected abstract void internalSetOffsetSize(double px);

    /**
     * Sets the length of the scrollbar.
     * <p>
     * <em>Note:</em> Even though {@code double} values are used, they are
     * currently only used as integers as large {@code int} (or small but fast
     * {@code long}). This means, all values are truncated to zero decimal
     * places.
     * 
     * @param px
     *            the length of the scrollbar in pixels
     */
    public final void setOffsetSize(double px) {
        internalSetOffsetSize(Math.max(0, truncate(px)));
        forceScrollbar(showsScrollHandle());
        recalculateMaxScrollPos();
        fireVisibilityChangeIfNeeded();
    }

    /**
     * Force the scrollbar to be visible with CSS. In practice, this means to
     * set either <code>overflow-x</code> or <code>overflow-y</code> to "
     * <code>scroll</code>" in the scrollbar's direction.
     * <p>
     * This is an IE8 workaround, since it doesn't always show scrollbars with
     * <code>overflow: auto</code> enabled.
     */
    protected abstract void forceScrollbar(boolean enable);

    /**
     * Gets the length of the scrollbar
     * 
     * @return the length of the scrollbar in pixels
     */
    public abstract double getOffsetSize();

    /**
     * Sets the scroll position of the scrollbar in the axis the scrollbar is
     * representing.
     * <p>
     * <em>Note:</em> Even though {@code double} values are used, they are
     * currently only used as integers as large {@code int} (or small but fast
     * {@code long}). This means, all values are truncated to zero decimal
     * places.
     * 
     * @param px
     *            the new scroll position in pixels
     */
    public final void setScrollPos(double px) {
        double oldScrollPos = scrollPos;
        scrollPos = Math.max(0, Math.min(maxScrollPos, truncate(px)));

        if (!pixelValuesEqual(oldScrollPos, scrollPos)) {
            /*
             * This is where the value needs to be converted into an integer no
             * matter how we flip it, since GWT expects an integer value.
             * There's no point making a JSNI method that accepts doubles as the
             * scroll position, since the browsers themselves don't support such
             * large numbers (as of today, 25.3.2014). This double-ranged is
             * only facilitating future virtual scrollbars.
             */
            internalSetScrollPos(toInt32(scrollPos));
        }
    }

    /**
     * Truncates a double such that no decimal places are retained.
     * <p>
     * E.g. {@code trunc(2.3d) == 2.0d} and {@code trunc(-2.3d) == -2.0d}.
     * 
     * @param num
     *            the double value to be truncated
     * @return the {@code num} value without any decimal digits
     */
    private static double truncate(double num) {
        if (num > 0) {
            return Math.floor(num);
        } else {
            return Math.ceil(num);
        }
    }

    /**
     * Modifies the element's scroll position (scrollTop or scrollLeft).
     * <p>
     * <em>Note:</em> The parameter here is a type of integer (instead of a
     * double) by design. The browsers internally convert all double values into
     * an integer value. To make this fact explicit, this API has chosen to
     * force integers already at this level.
     * 
     * @param px
     *            integer pixel value to scroll to
     */
    protected abstract void internalSetScrollPos(int px);

    /**
     * Gets the scroll position of the scrollbar in the axis the scrollbar is
     * representing.
     * 
     * @return the new scroll position in pixels
     */
    public final double getScrollPos() {
        assert internalGetScrollPos() == toInt32(scrollPos) : "calculated scroll position ("
                + toInt32(scrollPos)
                + ") did not match the DOM element scroll position ("
                + internalGetScrollPos() + ")";
        return scrollPos;
    }

    /**
     * Retrieves the element's scroll position (scrollTop or scrollLeft).
     * <p>
     * <em>Note:</em> The parameter here is a type of integer (instead of a
     * double) by design. The browsers internally convert all double values into
     * an integer value. To make this fact explicit, this API has chosen to
     * force integers already at this level.
     * 
     * @return integer pixel value of the scroll position
     */
    protected abstract int internalGetScrollPos();

    /**
     * Modifies {@link #scrollSizeElement scrollSizeElement's} dimensions in
     * such a way that the scrollbar is able to scroll a certain number of
     * pixels in the axis it is representing.
     * 
     * @param px
     *            the new size of {@link #scrollSizeElement} in the dimension
     *            this scrollbar is representing
     */
    protected abstract void internalSetScrollSize(int px);

    /**
     * Sets the amount of pixels the scrollbar needs to be able to scroll
     * through.
     * <p>
     * <em>Note:</em> Even though {@code double} values are used, they are
     * currently only used as integers as large {@code int} (or small but fast
     * {@code long}). This means, all values are truncated to zero decimal
     * places.
     * 
     * @param px
     *            the number of pixels the scrollbar should be able to scroll
     *            through
     */
    public final void setScrollSize(double px) {
        internalSetScrollSize(toInt32(Math.max(0, truncate(px))));
        forceScrollbar(showsScrollHandle());
        recalculateMaxScrollPos();
        fireVisibilityChangeIfNeeded();
    }

    /**
     * Gets the amount of pixels the scrollbar needs to be able to scroll
     * through.
     * 
     * @return the number of pixels the scrollbar should be able to scroll
     *         through
     */
    public double getScrollSize() {
        return internalGetScrollSize();
    }

    /**
     * Modifies {@link #scrollSizeElement scrollSizeElement's} dimensions in the
     * opposite axis to what the scrollbar is representing.
     * 
     * @param px
     *            the dimension that {@link #scrollSizeElement} should take in
     *            the opposite axis to what the scrollbar is representing
     */
    protected abstract void internalSetScrollbarThickness(int px);

    /**
     * Sets the scrollbar's thickness.
     * <p>
     * If the thickness is set to 0, the scrollbar will be treated as an
     * "invisible" scrollbar. This means, the DOM structure will be given a
     * non-zero size, but {@link #getScrollbarThickness()} will still return the
     * value 0.
     * 
     * @param px
     *            the scrollbar's thickness in pixels
     */
    public final void setScrollbarThickness(int px) {
        isInvisibleScrollbar = (px == 0);
        internalSetScrollbarThickness(px != 0 ? Math.max(0, px)
                : OSX_INVISIBLE_SCROLLBAR_FAKE_SIZE_PX);
    }

    /**
     * Gets the scrollbar's thickness as defined in the DOM.
     * 
     * @return the scrollbar's thickness as defined in the DOM, in pixels
     */
    protected abstract int internalGetScrollbarThickness();

    /**
     * Gets the scrollbar's thickness.
     * <p>
     * This value will differ from the value in the DOM, if the thickness was
     * set to 0 with {@link #setScrollbarThickness(int)}, as the scrollbar is
     * then treated as "invisible."
     * 
     * @return the scrollbar's thickness in pixels
     */
    public final int getScrollbarThickness() {
        if (!isInvisibleScrollbar) {
            return internalGetScrollbarThickness();
        } else {
            return 0;
        }
    }

    /**
     * Checks whether the scrollbar's handle is visible.
     * <p>
     * In other words, this method checks whether the contents is larger than
     * can visually fit in the element.
     * 
     * @return <code>true</code> iff the scrollbar's handle is visible
     */
    public boolean showsScrollHandle() {
        return getOffsetSize() < getScrollSize();
    }

    public void recalculateMaxScrollPos() {
        double scrollSize = getScrollSize();
        double offsetSize = getOffsetSize();
        maxScrollPos = Math.max(0, scrollSize - offsetSize);

        // make sure that the correct max scroll position is maintained.
        setScrollPos(scrollPos);
    }

    /**
     * This is a method that JSNI can call to synchronize the object state from
     * the DOM.
     */
    private final void updateScrollPosFromDom() {
        scrollPos = internalGetScrollPos();
    }

    protected HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }

    /**
     * Adds handler for the scrollbar handle visibility.
     * 
     * @param handler
     *            the {@link VisibilityHandler} to add
     * @return {@link HandlerRegistration} used to remove the handler
     */
    public HandlerRegistration addVisibilityHandler(
            final VisibilityHandler handler) {
        return getHandlerManager().addHandler(VisibilityChangeEvent.TYPE,
                handler);
    }

    private void fireVisibilityChangeIfNeeded() {
        final boolean oldHandleIsVisible = scrollHandleIsVisible;
        scrollHandleIsVisible = showsScrollHandle();
        if (oldHandleIsVisible != scrollHandleIsVisible) {
            final VisibilityChangeEvent event = new VisibilityChangeEvent(
                    scrollHandleIsVisible);
            getHandlerManager().fireEvent(event);
        }
    }

    /**
     * Converts a double into an integer by JavaScript's terms.
     * <p>
     * Implementation copied from {@link Element#toInt32(double)}.
     * 
     * @param val
     *            the double value to convert into an integer
     * @return the double value converted to an integer
     */
    private static native int toInt32(double val)
    /*-{
        return val | 0;
    }-*/;

    /**
     * Compares two double values with the error margin of
     * {@link #PIXEL_EPSILON} (i.e. {@value #PIXEL_EPSILON})
     * 
     * @param num1
     *            the first value for which to compare equality
     * @param num2
     *            the second value for which to compare equality
     */
    private static boolean pixelValuesEqual(final double num1, final double num2) {
        return Math.abs(num1 - num2) <= PIXEL_EPSILON;
    }
}
