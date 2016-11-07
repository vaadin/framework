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

package com.vaadin.client.widget.escalator;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationSupportDetector;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;

/**
 * An element-like bundle representing a configurable and visual scrollbar in
 * one axis.
 *
 * @since 7.4
 * @author Vaadin Ltd
 * @see VerticalScrollbarBundle
 * @see HorizontalScrollbarBundle
 */
public abstract class ScrollbarBundle implements DeferredWorker {
    private static final boolean supportsRequestAnimationFrame = new AnimationSupportDetector()
            .isNativelySupported();

    private class ScrollEventFirer {

        private final ScheduledCommand fireEventCommand = new ScheduledCommand() {
            @Override
            public void execute() {
                /*
                 * Some kind of native-scroll-event related asynchronous problem
                 * occurs here (at least on desktops) where the internal
                 * bookkeeping isn't up to date with the real scroll position.
                 * The weird thing is, that happens only once, and if you drag
                 * scrollbar fast enough. After it has failed once, it never
                 * fails again.
                 *
                 * Theory: the user drags the scrollbar, and this command is
                 * executed before the browser has a chance to fire a scroll
                 * event (which normally would correct this situation). This
                 * would explain why slow scrolling doesn't trigger the problem,
                 * while fast scrolling does.
                 *
                 * To make absolutely sure that we have the latest scroll
                 * position, let's update the internal value.
                 *
                 * This might lead to a slight performance hit (on my computer
                 * it was never more than 3ms on either of Chrome 38 or Firefox
                 * 31). It also _slightly_ counteracts the purpose of the
                 * internal bookkeeping. But since getScrollPos is called 3
                 * times (on one direction) per scroll loop, it's still better
                 * to have take this small penalty than removing it altogether.
                 */
                updateScrollPosFromDom();

                getHandlerManager().fireEvent(new ScrollEvent());
                isBeingFired = false;
            }
        };

        private boolean isBeingFired;

        public void scheduleEvent() {
            if (!isBeingFired) {
                /*
                 * We'll gather all the scroll events, and only fire once, once
                 * everything has calmed down.
                 */
                if (supportsRequestAnimationFrame) {
                    // Chrome MUST use this as deferred commands will sometimes
                    // be run with a 300+ ms delay when scrolling.
                    AnimationScheduler.get()
                            .requestAnimationFrame(new AnimationCallback() {
                                @Override
                                public void execute(double timestamp) {
                                    fireEventCommand.execute();

                                }
                            });
                } else {
                    // Does not support requestAnimationFrame and the fallback
                    // uses a delay of 16ms, we stick to the old deferred
                    // command which uses a delay of 0ms
                    Scheduler.get().scheduleDeferred(fireEventCommand);
                }
                isBeingFired = true;
            }
        }
    }

    /**
     * The orientation of the scrollbar.
     */
    public enum Direction {
        VERTICAL, HORIZONTAL;
    }

    private class TemporaryResizer {
        private static final int TEMPORARY_RESIZE_DELAY = 1000;

        private final Timer timer = new Timer() {
            @Override
            public void run() {
                internalSetScrollbarThickness(1);
                root.getStyle().setVisibility(Visibility.HIDDEN);
            }
        };

        public void show() {
            internalSetScrollbarThickness(OSX_INVISIBLE_SCROLLBAR_FAKE_SIZE_PX);
            root.getStyle().setVisibility(Visibility.VISIBLE);
            timer.schedule(TEMPORARY_RESIZE_DELAY);
        }
    }

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

    public static class VisibilityChangeEvent
            extends GwtEvent<VisibilityHandler> {
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
     * A representation of a single vertical scrollbar.
     *
     * @see VerticalScrollbarBundle#getElement()
     */
    public final static class VerticalScrollbarBundle extends ScrollbarBundle {

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
        protected void internalSetScrollSize(double px) {
            scrollSizeElement.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        protected String internalGetScrollSize() {
            return scrollSizeElement.getStyle().getHeight();
        }

        @Override
        protected void internalSetOffsetSize(double px) {
            root.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        public String internalGetOffsetSize() {
            return root.getStyle().getHeight();
        }

        @Override
        protected void internalSetScrollbarThickness(double px) {
            root.getStyle().setPaddingRight(px, Unit.PX);
            root.getStyle().setWidth(0, Unit.PX);
            scrollSizeElement.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        protected String internalGetScrollbarThickness() {
            return scrollSizeElement.getStyle().getWidth();
        }

        @Override
        protected void internalForceScrollbar(boolean enable) {
            if (enable) {
                root.getStyle().setOverflowY(Overflow.SCROLL);
            } else {
                root.getStyle().clearOverflowY();
            }
        }

        @Override
        public Direction getDirection() {
            return Direction.VERTICAL;
        }
    }

    /**
     * A representation of a single horizontal scrollbar.
     *
     * @see HorizontalScrollbarBundle#getElement()
     */
    public final static class HorizontalScrollbarBundle
            extends ScrollbarBundle {

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
        protected void internalSetScrollSize(double px) {
            scrollSizeElement.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        protected String internalGetScrollSize() {
            return scrollSizeElement.getStyle().getWidth();
        }

        @Override
        protected void internalSetOffsetSize(double px) {
            root.getStyle().setWidth(px, Unit.PX);
        }

        @Override
        public String internalGetOffsetSize() {
            return root.getStyle().getWidth();
        }

        @Override
        protected void internalSetScrollbarThickness(double px) {
            root.getStyle().setPaddingBottom(px, Unit.PX);
            root.getStyle().setHeight(0, Unit.PX);
            scrollSizeElement.getStyle().setHeight(px, Unit.PX);
        }

        @Override
        protected String internalGetScrollbarThickness() {
            return scrollSizeElement.getStyle().getHeight();
        }

        @Override
        protected void internalForceScrollbar(boolean enable) {
            if (enable) {
                root.getStyle().setOverflowX(Overflow.SCROLL);
            } else {
                root.getStyle().clearOverflowX();
            }
        }

        @Override
        public Direction getDirection() {
            return Direction.HORIZONTAL;
        }
    }

    protected final Element root = DOM.createDiv();
    protected final Element scrollSizeElement = DOM.createDiv();
    protected boolean isInvisibleScrollbar = false;

    private double scrollPos = 0;
    private double maxScrollPos = 0;

    private boolean scrollHandleIsVisible = false;

    private boolean isLocked = false;

    /** @deprecated access via {@link #getHandlerManager()} instead. */
    @Deprecated
    private HandlerManager handlerManager;

    private TemporaryResizer invisibleScrollbarTemporaryResizer = new TemporaryResizer();

    private final ScrollEventFirer scrollEventFirer = new ScrollEventFirer();

    private HandlerRegistration scrollSizeTemporaryScrollHandler;
    private HandlerRegistration offsetSizeTemporaryScrollHandler;
    private HandlerRegistration scrollInProgress;

    private ScrollbarBundle() {
        root.appendChild(scrollSizeElement);
        root.getStyle().setDisplay(Display.NONE);
        root.setTabIndex(-1);
    }

    protected abstract String internalGetScrollSize();

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
     *
     * @param px
     *            the length of the scrollbar in pixels
     */
    public final void setOffsetSize(final double px) {

        /*
         * This needs to be made step-by-step because IE8 flat-out refuses to
         * fire a scroll event when the scroll size becomes smaller than the
         * offset size. All other browser need to suffer alongside.
         */

        boolean newOffsetSizeIsGreaterThanScrollSize = px > getScrollSize();
        boolean offsetSizeBecomesGreaterThanScrollSize = showsScrollHandle()
                && newOffsetSizeIsGreaterThanScrollSize;
        if (offsetSizeBecomesGreaterThanScrollSize && getScrollPos() != 0) {
            if (offsetSizeTemporaryScrollHandler != null) {
                offsetSizeTemporaryScrollHandler.removeHandler();
            }
            // must be a field because Java insists.
            offsetSizeTemporaryScrollHandler = addScrollHandler(
                    new ScrollHandler() {
                        @Override
                        public void onScroll(ScrollEvent event) {
                            setOffsetSizeNow(px);
                        }
                    });
            setScrollPos(0);
        } else {
            setOffsetSizeNow(px);
        }
    }

    private void setOffsetSizeNow(double px) {
        internalSetOffsetSize(Math.max(0, px));
        recalculateMaxScrollPos();
        forceScrollbar(showsScrollHandle());
        fireVisibilityChangeIfNeeded();
        if (offsetSizeTemporaryScrollHandler != null) {
            offsetSizeTemporaryScrollHandler.removeHandler();
            offsetSizeTemporaryScrollHandler = null;
        }
    }

    /**
     * Force the scrollbar to be visible with CSS. In practice, this means to
     * set either <code>overflow-x</code> or <code>overflow-y</code> to "
     * <code>scroll</code>" in the scrollbar's direction.
     * <p>
     * This method is an IE8 workaround, since it doesn't always show scrollbars
     * with <code>overflow: auto</code> enabled.
     * <p>
     * Firefox on the other hand loses pending scroll events when the scrollbar
     * is hidden, so the event must be fired manually.
     * <p>
     * When IE8 support is dropped, this should really be simplified.
     */
    protected void forceScrollbar(boolean enable) {
        if (enable) {
            root.getStyle().clearDisplay();
        } else {
            if (BrowserInfo.get().isFirefox()) {
                /*
                 * This is related to the Firefox workaround in setScrollSize
                 * for setScrollPos(0)
                 */
                scrollEventFirer.scheduleEvent();
            }

            root.getStyle().setDisplay(Display.NONE);
        }
        internalForceScrollbar(enable);
    }

    protected abstract void internalForceScrollbar(boolean enable);

    /**
     * Gets the length of the scrollbar
     *
     * @return the length of the scrollbar in pixels
     */
    public double getOffsetSize() {
        return parseCssDimensionToPixels(internalGetOffsetSize());
    }

    public abstract String internalGetOffsetSize();

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
        if (isLocked()) {
            return;
        }

        double oldScrollPos = scrollPos;
        scrollPos = Math.max(0, Math.min(maxScrollPos, truncate(px)));

        if (!WidgetUtil.pixelValuesEqual(oldScrollPos, scrollPos)) {
            if (scrollInProgress == null) {
                // Only used for tracking that there is "workPending"
                scrollInProgress = addScrollHandler(new ScrollHandler() {
                    @Override
                    public void onScroll(ScrollEvent event) {
                        scrollInProgress.removeHandler();
                        scrollInProgress = null;
                    }
                });
            }
            if (isInvisibleScrollbar) {
                invisibleScrollbarTemporaryResizer.show();
            }

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
     * Should be called whenever this bundle is attached to the DOM (typically,
     * from the onLoad of the containing widget). Used to ensure the DOM scroll
     * position is maintained when detaching and reattaching the bundle.
     *
     * @since 7.4.1
     */
    public void onLoad() {
        internalSetScrollPos(toInt32(scrollPos));
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
        assert internalGetScrollPos() == toInt32(
                scrollPos) : "calculated scroll position (" + scrollPos
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
    protected abstract void internalSetScrollSize(double px);

    /**
     * Sets the amount of pixels the scrollbar needs to be able to scroll
     * through.
     *
     * @param px
     *            the number of pixels the scrollbar should be able to scroll
     *            through
     */
    public final void setScrollSize(final double px) {

        /*
         * This needs to be made step-by-step because IE8 flat-out refuses to
         * fire a scroll event when the scroll size becomes smaller than the
         * offset size. All other browser need to suffer alongside.
         *
         * This really should be changed to not use any temporary scroll
         * handlers at all once IE8 support is dropped, like now done only for
         * Firefox.
         */

        boolean newScrollSizeIsSmallerThanOffsetSize = px <= getOffsetSize();
        boolean scrollSizeBecomesSmallerThanOffsetSize = showsScrollHandle()
                && newScrollSizeIsSmallerThanOffsetSize;
        if (scrollSizeBecomesSmallerThanOffsetSize && getScrollPos() != 0) {
            /*
             * For whatever reason, Firefox loses the scroll event in this case
             * and the onscroll handler is never called (happens when reducing
             * size from 1000 items to 1 while being scrolled a bit down, see
             * #19802). Based on the comment above, only IE8 should really use
             * 'delayedSizeSet'
             */
            boolean delayedSizeSet = !BrowserInfo.get().isFirefox();
            // must be a field because Java insists.
            if (delayedSizeSet) {
                if (scrollSizeTemporaryScrollHandler != null) {
                    scrollSizeTemporaryScrollHandler.removeHandler();
                }
                scrollSizeTemporaryScrollHandler = addScrollHandler(
                        new ScrollHandler() {
                            @Override
                            public void onScroll(ScrollEvent event) {
                                setScrollSizeNow(px);
                            }
                        });
            }
            setScrollPos(0);
            if (!delayedSizeSet) {
                setScrollSizeNow(px);
            }
        } else {
            setScrollSizeNow(px);
        }
    }

    private void setScrollSizeNow(double px) {
        internalSetScrollSize(Math.max(0, px));
        recalculateMaxScrollPos();
        forceScrollbar(showsScrollHandle());
        fireVisibilityChangeIfNeeded();
        if (scrollSizeTemporaryScrollHandler != null) {
            scrollSizeTemporaryScrollHandler.removeHandler();
            scrollSizeTemporaryScrollHandler = null;
        }
    }

    /**
     * Gets the amount of pixels the scrollbar needs to be able to scroll
     * through.
     *
     * @return the number of pixels the scrollbar should be able to scroll
     *         through
     */
    public double getScrollSize() {
        return parseCssDimensionToPixels(internalGetScrollSize());
    }

    /**
     * Modifies {@link #scrollSizeElement scrollSizeElement's} dimensions in the
     * opposite axis to what the scrollbar is representing.
     *
     * @param px
     *            the dimension that {@link #scrollSizeElement} should take in
     *            the opposite axis to what the scrollbar is representing
     */
    protected abstract void internalSetScrollbarThickness(double px);

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
    public final void setScrollbarThickness(double px) {
        isInvisibleScrollbar = (px == 0);

        if (isInvisibleScrollbar) {
            Event.sinkEvents(root, Event.ONSCROLL);
            Event.setEventListener(root, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    invisibleScrollbarTemporaryResizer.show();
                }
            });
            root.getStyle().setVisibility(Visibility.HIDDEN);
        } else {
            Event.sinkEvents(root, 0);
            Event.setEventListener(root, null);
            root.getStyle().clearVisibility();
        }

        internalSetScrollbarThickness(Math.max(1d, px));
    }

    /**
     * Gets the scrollbar's thickness as defined in the DOM.
     *
     * @return the scrollbar's thickness as defined in the DOM, in pixels
     */
    protected abstract String internalGetScrollbarThickness();

    /**
     * Gets the scrollbar's thickness.
     * <p>
     * This value will differ from the value in the DOM, if the thickness was
     * set to 0 with {@link #setScrollbarThickness(double)}, as the scrollbar is
     * then treated as "invisible."
     *
     * @return the scrollbar's thickness in pixels
     */
    public final double getScrollbarThickness() {
        if (!isInvisibleScrollbar) {
            return parseCssDimensionToPixels(internalGetScrollbarThickness());
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
        return getScrollSize() - getOffsetSize() > WidgetUtil.PIXEL_EPSILON;
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

        /*
         * TODO: this method probably shouldn't be called from Escalator's JSNI,
         * but probably could be handled internally by this listening to its own
         * element. Would clean up the code quite a bit. Needs further
         * investigation.
         */

        int newScrollPos = internalGetScrollPos();
        if (!isLocked()) {
            scrollPos = newScrollPos;
            scrollEventFirer.scheduleEvent();
        } else if (scrollPos != newScrollPos) {
            // we need to actually undo the setting of the scroll.
            internalSetScrollPos(toInt32(scrollPos));
        }
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
        return Math.round(val) | 0;
    }-*/;

    /**
     * Locks or unlocks the scrollbar bundle.
     * <p>
     * A locked scrollbar bundle will refuse to scroll, both programmatically
     * and via user-triggered events.
     *
     * @param isLocked
     *            <code>true</code> to lock, <code>false</code> to unlock
     */
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * Checks whether the scrollbar bundle is locked or not.
     *
     * @return <code>true</code> iff the scrollbar bundle is locked
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Returns the scroll direction of this scrollbar bundle.
     *
     * @return the scroll direction of this scrollbar bundle
     */
    public abstract Direction getDirection();

    /**
     * Adds a scroll handler to the scrollbar bundle.
     *
     * @param handler
     *            the handler to add
     * @return the registration object for the handler registration
     */
    public HandlerRegistration addScrollHandler(final ScrollHandler handler) {
        return getHandlerManager().addHandler(ScrollEvent.TYPE, handler);
    }

    private static double parseCssDimensionToPixels(String size) {

        /*
         * Sizes of elements are calculated from CSS rather than
         * element.getOffset*() because those values are 0 whenever display:
         * none. Because we know that all elements have populated
         * CSS-dimensions, it's better to do it that way.
         *
         * Another solution would be to make the elements visible while
         * measuring and then re-hide them, but that would cause unnecessary
         * reflows that would probably kill the performance dead.
         */

        if (size.isEmpty()) {
            return 0;
        } else {
            assert size.endsWith("px") : "Can't parse CSS dimension \"" + size
                    + "\"";
            return Double.parseDouble(size.substring(0, size.length() - 2));
        }
    }

    @Override
    public boolean isWorkPending() {
        // Need to include scrollEventFirer.isBeingFired as it might use
        // requestAnimationFrame - which is not automatically checked
        return scrollSizeTemporaryScrollHandler != null
                || offsetSizeTemporaryScrollHandler != null
                || scrollInProgress != null || scrollEventFirer.isBeingFired;
    }
}
