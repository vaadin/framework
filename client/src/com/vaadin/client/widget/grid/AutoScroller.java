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
package com.vaadin.client.widget.grid;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widgets.Grid;

/**
 * A class for handling automatic scrolling vertically / horizontally in the
 * Grid when the cursor is close enough the edge of the body of the grid,
 * depending on the scroll direction chosen.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public class AutoScroller {

    /**
     * Callback that notifies when the cursor is on top of a new row or column
     * because of the automatic scrolling.
     */
    public interface AutoScrollerCallback {

        /**
         * Triggered when doing automatic scrolling.
         * <p>
         * Because the auto scroller currently only supports scrolling in one
         * axis, this method is used for both vertical and horizontal scrolling.
         * 
         * @param scrollDiff
         *            the amount of pixels that have been auto scrolled since
         *            last call
         */
        void onAutoScroll(int scrollDiff);

        /**
         * Triggered when the grid scroll has reached the minimum scroll
         * position. Depending on the scroll axis, either scrollLeft or
         * scrollTop is 0.
         */
        void onAutoScrollReachedMin();

        /**
         * Triggered when the grid scroll has reached the max scroll position.
         * Depending on the scroll axis, either scrollLeft or scrollTop is at
         * its maximum value.
         */
        void onAutoScrollReachedMax();
    }

    public enum ScrollAxis {
        VERTICAL, HORIZONTAL
    }

    /** The maximum number of pixels per second to autoscroll. */
    private static final int SCROLL_TOP_SPEED_PX_SEC = 500;

    /**
     * The minimum area where the grid doesn't scroll while the pointer is
     * pressed.
     */
    private static final int MIN_NO_AUTOSCROLL_AREA_PX = 50;

    /** The size of the autoscroll area, both top/left and bottom/right. */
    private int scrollAreaPX = 100;

    /**
     * This class's main objective is to listen when to stop autoscrolling, and
     * make sure everything stops accordingly.
     */
    private class TouchEventHandler implements NativePreviewHandler {
        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            /*
             * Remember: targetElement is always where touchstart started, not
             * where the finger is pointing currently.
             */
            switch (event.getTypeInt()) {
            case Event.ONTOUCHSTART: {
                if (event.getNativeEvent().getTouches().length() == 1) {
                    /*
                     * Something has dropped a touchend/touchcancel and the
                     * scroller is most probably running amok. Let's cancel it
                     * and pretend that everything's going as expected
                     * 
                     * Because this is a preview, this code is run before start
                     * event can be passed to the start(...) method.
                     */
                    stop();

                    /*
                     * Related TODO: investigate why iOS seems to ignore a
                     * touchend/touchcancel when frames are dropped, and/or if
                     * something can be done about that.
                     */
                }
                break;
            }

            case Event.ONTOUCHMOVE:
                event.cancel();
                break;

            case Event.ONTOUCHEND:
            case Event.ONTOUCHCANCEL:
                // TODO investigate if this works as desired
                stop();
                break;
            }
        }

    }

    /**
     * This class's responsibility is to scroll the table while a pointer is
     * kept in a scrolling zone.
     * <p>
     * <em>Techical note:</em> This class is an AnimationCallback because we
     * need a timer: when the finger is kept in place while the grid scrolls, we
     * still need to be able to make new selections. So, instead of relying on
     * events (which won't be fired, since the pointer isn't necessarily
     * moving), we do this check on each frame while the pointer is "active"
     * (mouse is pressed, finger is on screen).
     */
    private class AutoScrollingFrame implements AnimationCallback {

        /**
         * If the acceleration gradient area is smaller than this, autoscrolling
         * will be disabled (it becomes too quick to accelerate to be usable).
         */
        private static final int GRADIENT_MIN_THRESHOLD_PX = 10;

        /**
         * The speed at which the gradient area recovers, once scrolling in that
         * direction has started.
         */
        private static final int SCROLL_AREA_REBOUND_PX_PER_SEC = 1;
        private static final double SCROLL_AREA_REBOUND_PX_PER_MS = SCROLL_AREA_REBOUND_PX_PER_SEC / 1000.0d;

        /**
         * The lowest y/x-coordinate on the {@link Event#getClientY() client-y}
         * or {@link Event#getClientX() client-x} from where we need to start
         * scrolling towards the top/left.
         */
        private int startBound = -1;

        /**
         * The highest y/x-coordinate on the {@link Event#getClientY() client-y}
         * or {@link Event#getClientX() client-x} from where we need to
         * scrolling towards the bottom.
         */
        private int endBound = -1;

        /**
         * The area where the selection acceleration takes place. If &lt;
         * {@link #GRADIENT_MIN_THRESHOLD_PX}, autoscrolling is disabled
         */
        private final int gradientArea;

        /**
         * The number of pixels per seconds we currently are scrolling (negative
         * is towards the top/left, positive is towards the bottom/right).
         */
        private double scrollSpeed = 0;

        private double prevTimestamp = 0;

        /**
         * This field stores fractions of pixels to scroll, to make sure that
         * we're able to scroll less than one px per frame.
         */
        private double pixelsToScroll = 0.0d;

        /** Should this animator be running. */
        private boolean running = false;

        /** The handle in which this instance is running. */
        private AnimationHandle handle;

        /**
         * The pointer's pageY (VERTICAL) / pageX (HORIZONTAL) coordinate
         * depending on scrolling axis.
         */
        private int scrollingAxisPageCoordinate;

        /** @see #doScrollAreaChecks(int) */
        private int finalStartBound;

        /** @see #doScrollAreaChecks(int) */
        private int finalEndBound;

        private boolean scrollAreaShouldRebound = false;

        public AutoScrollingFrame(final int startBound, final int endBound,
                final int gradientArea) {
            finalStartBound = startBound;
            finalEndBound = endBound;
            this.gradientArea = gradientArea;
        }

        @Override
        public void execute(final double timestamp) {
            final double timeDiff = timestamp - prevTimestamp;
            prevTimestamp = timestamp;

            reboundScrollArea(timeDiff);

            pixelsToScroll += scrollSpeed * (timeDiff / 1000.0d);
            final int intPixelsToScroll = (int) pixelsToScroll;
            pixelsToScroll -= intPixelsToScroll;

            if (intPixelsToScroll != 0) {
                double scrollPos;
                double maxScrollPos;
                double newScrollPos;
                if (scrollDirection == ScrollAxis.VERTICAL) {
                    scrollPos = grid.getScrollTop();
                    maxScrollPos = getMaxScrollTop();
                } else {
                    scrollPos = grid.getScrollLeft();
                    maxScrollPos = getMaxScrollLeft();
                }
                if (intPixelsToScroll > 0 && scrollPos < maxScrollPos
                        || intPixelsToScroll < 0 && scrollPos > 0) {
                    newScrollPos = scrollPos + intPixelsToScroll;
                    if (scrollDirection == ScrollAxis.VERTICAL) {
                        grid.setScrollTop(newScrollPos);
                    } else {
                        grid.setScrollLeft(newScrollPos);
                    }
                    callback.onAutoScroll(intPixelsToScroll);
                    if (newScrollPos <= 0) {
                        callback.onAutoScrollReachedMin();
                    } else if (newScrollPos >= maxScrollPos) {
                        callback.onAutoScrollReachedMax();
                    }
                }
            }

            reschedule();
        }

        /**
         * If the scroll are has been offset by the pointer starting out there,
         * move it back a bit
         */
        private void reboundScrollArea(double timeDiff) {
            if (!scrollAreaShouldRebound) {
                return;
            }

            int reboundPx = (int) Math.ceil(SCROLL_AREA_REBOUND_PX_PER_MS
                    * timeDiff);
            if (startBound < finalStartBound) {
                startBound += reboundPx;
                startBound = Math.min(startBound, finalStartBound);
                updateScrollSpeed(scrollingAxisPageCoordinate);
            } else if (endBound > finalEndBound) {
                endBound -= reboundPx;
                endBound = Math.max(endBound, finalEndBound);
                updateScrollSpeed(scrollingAxisPageCoordinate);
            }
        }

        private void updateScrollSpeed(final int pointerPageCordinate) {

            final double ratio;
            if (pointerPageCordinate < startBound) {
                final double distance = pointerPageCordinate - startBound;
                ratio = Math.max(-1, distance / gradientArea);
            }

            else if (pointerPageCordinate > endBound) {
                final double distance = pointerPageCordinate - endBound;
                ratio = Math.min(1, distance / gradientArea);
            }

            else {
                ratio = 0;
            }

            scrollSpeed = ratio * SCROLL_TOP_SPEED_PX_SEC;
        }

        public void start() {
            running = true;
            reschedule();
        }

        public void stop() {
            running = false;

            if (handle != null) {
                handle.cancel();
                handle = null;
            }
        }

        private void reschedule() {
            if (running && gradientArea >= GRADIENT_MIN_THRESHOLD_PX) {
                handle = AnimationScheduler.get().requestAnimationFrame(this,
                        grid.getElement());
            }
        }

        public void updatePointerCoords(int pageX, int pageY) {
            final int pageCordinate;
            if (scrollDirection == ScrollAxis.VERTICAL) {
                pageCordinate = pageY;
            } else {
                pageCordinate = pageX;
            }
            doScrollAreaChecks(pageCordinate);
            updateScrollSpeed(pageCordinate);
            scrollingAxisPageCoordinate = pageCordinate;
        }

        /**
         * This method checks whether the first pointer event started in an area
         * that would start scrolling immediately, and does some actions
         * accordingly.
         * <p>
         * If it is, that scroll area will be offset "beyond" the pointer (above
         * if pointer is towards the top/left, otherwise below/right).
         */
        private void doScrollAreaChecks(int pageCordinate) {
            /*
             * The first run makes sure that neither scroll position is
             * underneath the finger, but offset to either direction from
             * underneath the pointer.
             */
            if (startBound == -1) {
                startBound = Math.min(finalStartBound, pageCordinate);
                endBound = Math.max(finalEndBound, pageCordinate);
            }

            /*
             * Subsequent runs make sure that the scroll area grows (but doesn't
             * shrink) with the finger, but no further than the final bound.
             */
            else {
                int oldTopBound = startBound;
                if (startBound < finalStartBound) {
                    startBound = Math.max(startBound,
                            Math.min(finalStartBound, pageCordinate));
                }

                int oldBottomBound = endBound;
                if (endBound > finalEndBound) {
                    endBound = Math.min(endBound,
                            Math.max(finalEndBound, pageCordinate));
                }

                final boolean startDidNotMove = oldTopBound == startBound;
                final boolean endDidNotMove = oldBottomBound == endBound;
                final boolean wasMovement = pageCordinate != scrollingAxisPageCoordinate;
                scrollAreaShouldRebound = (startDidNotMove && endDidNotMove && wasMovement);
            }
        }
    }

    /**
     * This handler makes sure that pointer movements are handled.
     * <p>
     * Essentially, a native preview handler is registered (so that selection
     * gestures can happen outside of the selection column). The handler itself
     * makes sure that it's detached when the pointer is "lifted".
     */
    private final NativePreviewHandler scrollPreviewHandler = new NativePreviewHandler() {
        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            if (autoScroller == null) {
                stop();
                return;
            }

            final NativeEvent nativeEvent = event.getNativeEvent();
            int pageY = 0;
            int pageX = 0;
            switch (event.getTypeInt()) {
            case Event.ONMOUSEMOVE:
            case Event.ONTOUCHMOVE:
                pageY = WidgetUtil.getTouchOrMouseClientY(nativeEvent);
                pageX = WidgetUtil.getTouchOrMouseClientX(nativeEvent);
                autoScroller.updatePointerCoords(pageX, pageY);
                break;
            case Event.ONMOUSEUP:
            case Event.ONTOUCHEND:
            case Event.ONTOUCHCANCEL:
                stop();
                break;
            }
        }
    };
    /** The registration info for {@link #scrollPreviewHandler} */
    private HandlerRegistration handlerRegistration;

    /**
     * The top/left bound, as calculated from the {@link Event#getClientY()
     * client-y} or {@link Event#getClientX() client-x} coordinates.
     */
    private double startingBound = -1;

    /**
     * The bottom/right bound, as calculated from the {@link Event#getClientY()
     * client-y} or or {@link Event#getClientX() client-x} coordinates.
     */
    private int endingBound = -1;

    /** The size of the autoscroll acceleration area. */
    private int gradientArea;

    private Grid<?> grid;

    private HandlerRegistration nativePreviewHandlerRegistration;

    private ScrollAxis scrollDirection;

    private AutoScrollingFrame autoScroller;

    private AutoScrollerCallback callback;

    /**
     * Creates a new instance for scrolling the given grid.
     * 
     * @param grid
     *            the grid to auto scroll
     */
    public AutoScroller(Grid<?> grid) {
        this.grid = grid;
    }

    /**
     * Starts the automatic scrolling detection.
     * 
     * @param startEvent
     *            the event that starts the automatic scroll
     * @param scrollAxis
     *            the axis along which the scrolling should happen
     * @param callback
     *            the callback for getting info about the automatic scrolling
     */
    public void start(final NativeEvent startEvent, ScrollAxis scrollAxis,
            AutoScrollerCallback callback) {
        scrollDirection = scrollAxis;
        this.callback = callback;
        injectNativeHandler();
        start();
        startEvent.preventDefault();
        startEvent.stopPropagation();
    }

    /**
     * Stops the automatic scrolling.
     */
    public void stop() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }

        if (autoScroller != null) {
            autoScroller.stop();
            autoScroller = null;
        }

        removeNativeHandler();
    }

    /**
     * Set the auto scroll area height or width depending on the scrolling axis.
     * This is the amount of pixels from the edge of the grid that the scroll is
     * triggered.
     * <p>
     * Defaults to 100px.
     * 
     * @param px
     *            the pixel height/width for the auto scroll area depending on
     *            direction
     */
    public void setScrollArea(int px) {
        scrollAreaPX = px;
    }

    /**
     * Returns the size of the auto scroll area in pixels.
     * <p>
     * Defaults to 100px.
     * 
     * @return size in pixels
     */
    public int getScrollArea() {
        return scrollAreaPX;
    }

    private void start() {
        /*
         * bounds are updated whenever the autoscroll cycle starts, to make sure
         * that the widget hasn't changed in size, moved around, or whatnot.
         */
        updateScrollBounds();

        assert handlerRegistration == null : "handlerRegistration was not null";
        assert autoScroller == null : "autoScroller was not null";
        handlerRegistration = Event
                .addNativePreviewHandler(scrollPreviewHandler);
        autoScroller = new AutoScrollingFrame((int) Math.ceil(startingBound),
                endingBound, gradientArea);
        autoScroller.start();
    }

    private void updateScrollBounds() {
        double startBorder = getBodyClientStart();
        final int endBorder = getBodyClientEnd();
        startBorder += getFrozenColumnsWidth();

        startingBound = startBorder + scrollAreaPX;
        endingBound = endBorder - scrollAreaPX;
        gradientArea = scrollAreaPX;

        // modify bounds if they're too tightly packed
        if (endingBound - startingBound < MIN_NO_AUTOSCROLL_AREA_PX) {
            double adjustment = MIN_NO_AUTOSCROLL_AREA_PX
                    - (endingBound - startingBound);
            startingBound -= adjustment / 2;
            endingBound += adjustment / 2;
            gradientArea -= adjustment / 2;
        }
    }

    private void injectNativeHandler() {
        removeNativeHandler();
        nativePreviewHandlerRegistration = Event
                .addNativePreviewHandler(new TouchEventHandler());
    }

    private void removeNativeHandler() {
        if (nativePreviewHandlerRegistration != null) {
            nativePreviewHandlerRegistration.removeHandler();
            nativePreviewHandlerRegistration = null;
        }
    }

    private TableElement getTableElement() {
        final Element root = grid.getElement();
        final Element tablewrapper = Element.as(root.getChild(2));
        if (tablewrapper != null) {
            return TableElement.as(tablewrapper.getFirstChildElement());
        } else {
            return null;
        }
    }

    private TableSectionElement getTheadElement() {
        TableElement table = getTableElement();
        if (table != null) {
            return table.getTHead();
        } else {
            return null;
        }
    }

    private TableSectionElement getTfootElement() {
        TableElement table = getTableElement();
        if (table != null) {
            return table.getTFoot();
        } else {
            return null;
        }
    }

    private int getBodyClientEnd() {
        if (scrollDirection == ScrollAxis.VERTICAL) {
            return getTfootElement().getAbsoluteTop() - 1;
        } else {
            return getTableElement().getAbsoluteRight();
        }

    }

    private int getBodyClientStart() {
        if (scrollDirection == ScrollAxis.VERTICAL) {
            return getTheadElement().getAbsoluteBottom() + 1;
        } else {
            return getTableElement().getAbsoluteLeft();
        }
    }

    private double getFrozenColumnsWidth() {
        double value = getMultiSelectColumnWidth();
        for (int i = 0; i < grid.getFrozenColumnCount(); i++) {
            value += grid.getColumn(i).getWidthActual();
        }
        return value;
    }

    private double getMultiSelectColumnWidth() {
        if (grid.getFrozenColumnCount() >= 0
                && grid.getSelectionModel().getSelectionColumnRenderer() != null) {
            // frozen checkbox column is present
            return getTheadElement().getFirstChildElement()
                    .getFirstChildElement().getOffsetWidth();
        }
        return 0.0;
    }

    private double getMaxScrollLeft() {
        return grid.getScrollWidth()
                - (getTableElement().getParentElement().getOffsetWidth() - getFrozenColumnsWidth());
    }

    private double getMaxScrollTop() {
        return grid.getScrollHeight() - getTfootElement().getOffsetHeight()
                - getTheadElement().getOffsetHeight();
    }
}
