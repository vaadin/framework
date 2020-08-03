/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.client.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.DeferredWorker;
import com.vaadin.client.Profiler;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.client.widget.escalator.Cell;
import com.vaadin.client.widget.escalator.ColumnConfiguration;
import com.vaadin.client.widget.escalator.EscalatorUpdater;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.escalator.FlyweightRow;
import com.vaadin.client.widget.escalator.PositionFunction;
import com.vaadin.client.widget.escalator.PositionFunction.Translate3DPosition;
import com.vaadin.client.widget.escalator.PositionFunction.TranslatePosition;
import com.vaadin.client.widget.escalator.PositionFunction.WebkitTranslate3DPosition;
import com.vaadin.client.widget.escalator.Row;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;
import com.vaadin.client.widget.escalator.RowVisibilityChangeEvent;
import com.vaadin.client.widget.escalator.RowVisibilityChangeHandler;
import com.vaadin.client.widget.escalator.ScrollbarBundle;
import com.vaadin.client.widget.escalator.ScrollbarBundle.Direction;
import com.vaadin.client.widget.escalator.ScrollbarBundle.HorizontalScrollbarBundle;
import com.vaadin.client.widget.escalator.ScrollbarBundle.VerticalScrollbarBundle;
import com.vaadin.client.widget.escalator.Spacer;
import com.vaadin.client.widget.escalator.SpacerUpdater;
import com.vaadin.client.widget.escalator.events.RowHeightChangedEvent;
import com.vaadin.client.widget.escalator.events.SpacerIndexChangedEvent;
import com.vaadin.client.widget.escalator.events.SpacerVisibilityChangedEvent;
import com.vaadin.client.widget.grid.events.EscalatorSizeChangeHandler;
import com.vaadin.client.widget.grid.events.EscalatorSizeChangeHandler.EscalatorSizeChangeEvent;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;
import com.vaadin.client.widget.grid.events.VerticalScrollbarVisibilityChangeHandler;
import com.vaadin.client.widget.grid.events.VerticalScrollbarVisibilityChangeHandler.VerticalScrollbarVisibilityChangeEvent;
import com.vaadin.client.widgets.Escalator.JsniUtil.TouchHandlerBundle;
import com.vaadin.shared.Range;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.shared.util.SharedUtil;

/*-

 Maintenance Notes! Reading these might save your day.
 (note for editors: line width is 80 chars, including the
 one-space indentation)


 == Row Container Structure

 AbstractRowContainer
 |-- AbstractStaticRowContainer
 | |-- HeaderRowContainer
 | `-- FooterContainer
 `---- BodyRowContainerImpl

 AbstractRowContainer is intended to contain all common logic
 between RowContainers. It manages the bookkeeping of row
 count, makes sure that all individual cells are rendered
 the same way, and so on.

 AbstractStaticRowContainer has some special logic that is
 required by all RowContainers that don't scroll (hence the
 word "static"). HeaderRowContainer and FooterRowContainer
 are pretty thin special cases of a StaticRowContainer
 (mostly relating to positioning of the root element).

 BodyRowContainerImpl could also be split into an additional
 "AbstractScrollingRowContainer", but I felt that no more
 inner classes were needed. So it contains both logic
 required for making things scroll about, and equivalent
 special cases for layouting, as are found in
 Header/FooterRowContainers.


 == The Three Indices

 Each RowContainer can be thought to have three levels of
 indices for any given displayed row (but the distinction
 matters primarily for the BodyRowContainerImpl, because of
 the way it scrolls through data):

 - Logical index
 - Physical (or DOM) index
 - Visual index

 LOGICAL INDEX is the index that is linked to the data
 source. If you want your data source to represent a SQL
 database with 10 000 rows, the 7 000:th row in the SQL has a
 logical index of 6 999, since the index is 0-based (unless
 that data source does some funky logic).

 PHYSICAL INDEX is the index for a row that you see in a
 browser's DOM inspector. If your row is the second <tr>
 element within a <tbody> tag, it has a physical index of 1
 (because of 0-based indices). In Header and
 FooterRowContainers, you are safe to assume that the logical
 index is the same as the physical index. But because the
 BodyRowContainerImpl never displays large data sources
 entirely in the DOM, a physical index usually has no
 apparent direct relationship with its logical index.
 This is the sectionRowIndex in TableRowElements.
 RowIndex in TableRowElements displays the physical index
 of all row elements, headers and footers included.

 VISUAL INDEX is the index relating to the order that you
 see a row in, in the browser, as it is rendered. The
 topmost row is 0, the second is 1, and so on. The visual
 index is similar to the physical index in the sense that
 Header and FooterRowContainers can assume a 1:1
 relationship between visual index and logical index. And
 again, BodyRowContainerImpl has no such relationship. The
 body's visual index has additionally no apparent
 relationship with its physical index. Because the <tr> tags
 are reused in the body and visually repositioned with CSS
 as the user scrolls, the relationship between physical
 index and visual index is quickly broken. You can get an
 element's visual index via the field
 BodyRowContainerImpl.visualRowOrder.

 Currently, the physical and visual indices are kept in sync
 _most of the time_ by a deferred rearrangement of rows.
 They become desynced when scrolling. This is to help screen
 readers to read the contents from the DOM in a natural
 order. See BodyRowContainerImpl.DeferredDomSorter for more
 about that.

 It should be noted that the entire visual range is not
 necessarily in view at any given time, although it should be
 optimised to not exceed the maximum amount of rows that can
 theoretically fit within the viewport when their associated
 spacers have zero height, except by the two rows that are
 required for tab navigation to work.

 */

/**
 * A workaround-class for GWT and JSNI.
 * <p>
 * GWT is unable to handle some method calls to Java methods in inner-classes
 * from within JSNI blocks. Having that inner class extend a non-inner-class (or
 * implement such an interface), makes it possible for JSNI to indirectly refer
 * to the inner class, by invoking methods and fields in the non-inner-class
 * API.
 *
 * @see Escalator.Scroller
 */
abstract class JsniWorkaround {
    /**
     * A JavaScript function that handles the scroll DOM event, and passes it on
     * to Java code.
     *
     * @see #createScrollListenerFunction(Escalator)
     * @see Escalator.Scroller#onScroll()
     */
    protected final JavaScriptObject scrollListenerFunction;

    /**
     * A JavaScript function that handles the mousewheel DOM event, and passes
     * it on to Java code.
     *
     * @see #createMousewheelListenerFunction(Escalator)
     * @see Escalator.Scroller#onScroll()
     */
    protected final JavaScriptObject mousewheelListenerFunction;

    /**
     * A JavaScript function that handles the touch start DOM event, and passes
     * it on to Java code.
     *
     * @see TouchHandlerBundle#touchStart(Escalator.JsniUtil.TouchHandlerBundle.CustomTouchEvent)
     */
    protected JavaScriptObject touchStartFunction;

    /**
     * A JavaScript function that handles the touch move DOM event, and passes
     * it on to Java code.
     *
     * @see TouchHandlerBundle#touchMove(Escalator.JsniUtil.TouchHandlerBundle.CustomTouchEvent)
     */
    protected JavaScriptObject touchMoveFunction;

    /**
     * A JavaScript function that handles the touch end and cancel DOM events,
     * and passes them on to Java code.
     *
     * @see TouchHandlerBundle#touchEnd(Escalator.JsniUtil.TouchHandlerBundle.CustomTouchEvent)
     */
    protected JavaScriptObject touchEndFunction;

    protected TouchHandlerBundle touchHandlerBundle;

    protected JsniWorkaround(final Escalator escalator) {
        scrollListenerFunction = createScrollListenerFunction(escalator);
        mousewheelListenerFunction = createMousewheelListenerFunction(
                escalator);

        touchHandlerBundle = new TouchHandlerBundle(escalator);
        touchStartFunction = touchHandlerBundle.getTouchStartHandler();
        touchMoveFunction = touchHandlerBundle.getTouchMoveHandler();
        touchEndFunction = touchHandlerBundle.getTouchEndHandler();
    }

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #scrollListenerFunction}.
     *
     * @param esc
     *            a reference to the current instance of {@link Escalator}
     * @see Escalator.Scroller#onScroll()
     */
    protected abstract JavaScriptObject createScrollListenerFunction(
            Escalator esc);

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #mousewheelListenerFunction}.
     *
     * @param esc
     *            a reference to the current instance of {@link Escalator}
     * @see Escalator.Scroller#onScroll()
     */
    protected abstract JavaScriptObject createMousewheelListenerFunction(
            Escalator esc);
}

/**
 * A low-level table-like widget that features a scrolling virtual viewport and
 * lazily generated rows.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class Escalator extends Widget
        implements RequiresResize, DeferredWorker, SubPartAware {

    // todo comments legend
    /*
     * [[optimize]]: There's an opportunity to rewrite the code in such a way
     * that it _might_ perform better (remember to measure, implement,
     * re-measure)
     */
    /*
     * [[mpixscroll]]: This code will require alterations that are relevant for
     * supporting the scrolling through more pixels than some browsers normally
     * would support. (i.e. when we support more than "a million" pixels in the
     * escalator DOM). NOTE: these bits can most often also be identified by
     * searching for code that call scrollElem.getScrollTop();.
     */
    /*
     * [[spacer]]: Code that is important to make spacers work.
     */

    /**
     * A utility class that contains utility methods that are usually called
     * from JSNI.
     * <p>
     * The methods are moved in this class to minimize the amount of JSNI code
     * as much as feasible.
     */
    static class JsniUtil {
        public static class TouchHandlerBundle {

            public static final String POINTER_EVENT_TYPE_TOUCH = "touch";

            public static final int SIGNIFICANT_MOVE_THRESHOLD = 3;

            /**
             * A <a href=
             * "http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html"
             * >JavaScriptObject overlay</a> for the
             * <a href="http://www.w3.org/TR/touch-events/">JavaScript
             * TouchEvent</a> object.
             * <p>
             * This needs to be used in the touch event handlers, since GWT's
             * {@link com.google.gwt.event.dom.client.TouchEvent TouchEvent}
             * can't be cast from the JSNI call, and the
             * {@link com.google.gwt.dom.client.NativeEvent NativeEvent} isn't
             * properly populated with the correct values.
             */
            private static final class CustomTouchEvent
                    extends JavaScriptObject {
                protected CustomTouchEvent() {
                }

                public native NativeEvent getNativeEvent()
                /*-{
                    return this;
                }-*/;

                public native int getPageX()
                /*-{
                    return this.targetTouches[0].pageX;
                }-*/;

                public native int getPageY()
                /*-{
                    return this.targetTouches[0].pageY;
                }-*/;

                public native String getPointerType()
                /*-{
                    return this.pointerType;
                }-*/;
            }

            private final Escalator escalator;

            public TouchHandlerBundle(final Escalator escalator) {
                this.escalator = escalator;
            }

            public native JavaScriptObject getTouchStartHandler()
            /*-{
                // we need to store "this", since it won't be preserved on call.
                var self = this;
                return $entry(function (e) {
                    self.@com.vaadin.client.widgets.Escalator.JsniUtil.TouchHandlerBundle::touchStart(*)(e);
                });
            }-*/;

            public native JavaScriptObject getTouchMoveHandler()
            /*-{
                // we need to store "this", since it won't be preserved on call.
                var self = this;
                return $entry(function (e) {
                    self.@com.vaadin.client.widgets.Escalator.JsniUtil.TouchHandlerBundle::touchMove(*)(e);
                });
            }-*/;

            public native JavaScriptObject getTouchEndHandler()
            /*-{
                // we need to store "this", since it won't be preserved on call.
                var self = this;
                return $entry(function (e) {
                    self.@com.vaadin.client.widgets.Escalator.JsniUtil.TouchHandlerBundle::touchEnd(*)(e);
                });
            }-*/;

            // Duration of the inertial scrolling simulation. Devices with
            // larger screens take longer durations.
            private static final int DURATION = Window.getClientHeight();
            // multiply scroll velocity with repeated touching
            private int acceleration = 1;
            private boolean touching = false;
            // Two movement objects for storing status and processing touches
            private Movement yMov, xMov;
            // true if moved significantly since touch start
            private boolean movedSignificantly = false;
            private double touchStartTime;
            final double MIN_VEL = 0.6, MAX_VEL = 4, F_VEL = 1500, F_ACC = 0.7,
                    F_AXIS = 1;

            // The object to deal with one direction scrolling
            private class Movement {
                final List<Double> speeds = new ArrayList<>();
                final ScrollbarBundle scroll;
                double position, offset, velocity, prevPos, prevTime, delta;
                boolean run, vertical;

                public Movement(boolean vertical) {
                    this.vertical = vertical;
                    scroll = vertical ? escalator.verticalScrollbar
                            : escalator.horizontalScrollbar;
                }

                public void startTouch(CustomTouchEvent event) {
                    speeds.clear();
                    prevPos = pagePosition(event);
                    prevTime = Duration.currentTimeMillis();
                }

                public void moveTouch(CustomTouchEvent event) {
                    double pagePosition = pagePosition(event);
                    if (pagePosition > -1) {
                        delta = prevPos - pagePosition;
                        double now = Duration.currentTimeMillis();
                        double ellapsed = now - prevTime;
                        velocity = delta / ellapsed;
                        // if last speed was so low, reset speeds and start
                        // storing again
                        if (!speeds.isEmpty() && !validSpeed(speeds.get(0))) {
                            speeds.clear();
                            run = true;
                        }
                        speeds.add(0, velocity);
                        prevTime = now;
                        prevPos = pagePosition;
                    }
                }

                public void endTouch(CustomTouchEvent event) {
                    // Compute average speed
                    velocity = 0;
                    for (double s : speeds) {
                        velocity += s / speeds.size();
                    }
                    position = scroll.getScrollPos();

                    // Compute offset, and adjust it with an easing curve so as
                    // movement is smoother.
                    offset = F_VEL * velocity * acceleration
                            * easingInOutCos(velocity, MAX_VEL);

                    // Enable or disable inertia movement in this axis
                    run = validSpeed(velocity);
                    if (run) {
                        event.getNativeEvent().preventDefault();
                    }
                }

                void validate(Movement other) {
                    if (!run || other.velocity > 0
                            && Math.abs(velocity / other.velocity) < F_AXIS) {
                        delta = offset = 0;
                        run = false;
                    }
                }

                void stepAnimation(double progress) {
                    scroll.setScrollPos(position + offset * progress);
                }

                int pagePosition(CustomTouchEvent event) {
                    // Use native event's screen x and y for IE11 and Edge
                    // since there is no touches for these browsers (#18737)
                    if (isCurrentBrowserIE11OrEdge()) {
                        return vertical
                                ? event.getNativeEvent().getClientY()
                                        + Window.getScrollTop()
                                : event.getNativeEvent().getClientX()
                                        + Window.getScrollLeft();
                    }
                    JsArray<Touch> a = event.getNativeEvent().getTouches();
                    return vertical ? a.get(0).getPageY() : a.get(0).getPageX();
                }

                boolean validSpeed(double speed) {
                    return Math.abs(speed) > MIN_VEL;
                }
            }

            // Using GWT animations which take care of native animation frames.
            private Animation animation = new Animation() {
                @Override
                public void onUpdate(double progress) {
                    xMov.stepAnimation(progress);
                    yMov.stepAnimation(progress);
                }

                @Override
                public double interpolate(double progress) {
                    return easingOutCirc(progress);
                };

                @Override
                public void onComplete() {
                    touching = false;
                    escalator.body.domSorter.reschedule();
                };

                @Override
                public void run(int duration) {
                    if (xMov.run || yMov.run) {
                        super.run(duration);
                    } else {
                        onComplete();
                    }
                };
            };

            public void touchStart(final CustomTouchEvent event) {
                if (allowTouch(event)) {
                    if (yMov == null) {
                        yMov = new Movement(true);
                        xMov = new Movement(false);
                    }
                    if (animation.isRunning()) {
                        acceleration += F_ACC;
                        event.getNativeEvent().preventDefault();
                        animation.cancel();
                    } else {
                        acceleration = 1;
                    }
                    xMov.startTouch(event);
                    yMov.startTouch(event);
                    touchStartTime = Duration.currentTimeMillis();
                    touching = true;
                    movedSignificantly = false;
                } else {
                    touching = false;
                    animation.cancel();
                    acceleration = 1;
                }
            }

            public void touchMove(final CustomTouchEvent event) {
                if (touching) {
                    if (!movedSignificantly) {
                        double distanceSquared = Math.abs(xMov.delta)
                                * Math.abs(xMov.delta)
                                + Math.abs(yMov.delta) * Math.abs(yMov.delta);
                        movedSignificantly = distanceSquared > SIGNIFICANT_MOVE_THRESHOLD
                                * SIGNIFICANT_MOVE_THRESHOLD;
                    }
                    // allow handling long press differently, without triggering
                    // scrolling
                    if (escalator.getDelayToCancelTouchScroll() >= 0
                            && !movedSignificantly
                            && Duration.currentTimeMillis()
                                    - touchStartTime > escalator
                                            .getDelayToCancelTouchScroll()) {
                        // cancel touch handling, don't prevent event
                        touching = false;
                        animation.cancel();
                        acceleration = 1;
                        return;
                    }
                    xMov.moveTouch(event);
                    yMov.moveTouch(event);
                    xMov.validate(yMov);
                    yMov.validate(xMov);
                    moveScrollFromEvent(escalator, xMov.delta, yMov.delta,
                            event.getNativeEvent());
                }
            }

            public void touchEnd(final CustomTouchEvent event) {
                if (touching) {
                    xMov.endTouch(event);
                    yMov.endTouch(event);
                    xMov.validate(yMov);
                    yMov.validate(xMov);
                    // Adjust duration so as longer movements take more duration
                    boolean vert = !xMov.run || yMov.run
                            && Math.abs(yMov.offset) > Math.abs(xMov.offset);
                    double delta = Math.abs((vert ? yMov : xMov).offset);
                    animation.run((int) (3 * DURATION * easingOutExp(delta)));
                }
            }

            // Allow touchStart for IE11 and Edge even though there is no touch
            // (#18737),
            // otherwise allow touch only if there is a single touch in the
            // event
            private boolean allowTouch(
                    final TouchHandlerBundle.CustomTouchEvent event) {
                if (isCurrentBrowserIE11OrEdge()) {
                    return (POINTER_EVENT_TYPE_TOUCH
                            .equals(event.getPointerType()));
                } else {
                    return (event.getNativeEvent().getTouches().length() == 1);
                }
            }

            private double easingInOutCos(double val, double max) {
                return 0.5 - 0.5 * Math.cos(Math.PI * Math.signum(val)
                        * Math.min(Math.abs(val), max) / max);
            }

            private double easingOutExp(double delta) {
                return (1 - Math.pow(2, -delta / 1000));
            }

            private double easingOutCirc(double progress) {
                return Math.sqrt(1 - (progress - 1) * (progress - 1));
            }
        }

        public static void moveScrollFromEvent(final Escalator escalator,
                final double deltaX, final double deltaY,
                final NativeEvent event) {

            boolean scrollPosXChanged = false;
            boolean scrollPosYChanged = false;

            if (!Double.isNaN(deltaX)) {
                double oldScrollPosX = escalator.horizontalScrollbar
                        .getScrollPos();
                escalator.horizontalScrollbar.setScrollPosByDelta(deltaX);
                if (oldScrollPosX != escalator.horizontalScrollbar
                        .getScrollPos()) {
                    scrollPosXChanged = true;
                }
            }

            if (!Double.isNaN(deltaY)) {
                double oldScrollPosY = escalator.verticalScrollbar
                        .getScrollPos();
                escalator.verticalScrollbar.setScrollPosByDelta(deltaY);
                if (oldScrollPosY != escalator.verticalScrollbar
                        .getScrollPos()) {
                    scrollPosYChanged = true;
                }
            }

            /*
             * Only prevent if internal scrolling happened. If there's no more
             * room to scroll internally, allow the event to pass further.
             */
            final boolean warrantedYScroll = deltaY != 0 && scrollPosYChanged
                    && escalator.verticalScrollbar.showsScrollHandle();
            final boolean warrantedXScroll = deltaX != 0 && scrollPosXChanged
                    && escalator.horizontalScrollbar.showsScrollHandle();
            if (warrantedYScroll || warrantedXScroll) {
                event.preventDefault();
            }
        }
    }

    /**
     * ScrollDestination case-specific handling logic.
     */
    private static double getScrollPos(final ScrollDestination destination,
            final double targetStartPx, final double targetEndPx,
            final double viewportStartPx, final double viewportEndPx,
            final double padding) {

        final double viewportLength = viewportEndPx - viewportStartPx;

        switch (destination) {

        /*
         * Scroll as little as possible to show the target element. If the
         * element fits into view, this works as START or END depending on the
         * current scroll position. If the element does not fit into view, this
         * works as START.
         */
        case ANY: {
            final double startScrollPos = targetStartPx - padding;
            final double endScrollPos = targetEndPx + padding - viewportLength;

            if (startScrollPos < viewportStartPx) {
                return startScrollPos;
            } else if (targetEndPx + padding > viewportEndPx) {
                return endScrollPos;
            } else {
                // NOOP, it's already visible
                return viewportStartPx;
            }
        }

        /*
         * Scrolls so that the element is shown at the end of the viewport. The
         * viewport will, however, not scroll before its first element.
         */
        case END: {
            return targetEndPx + padding - viewportLength;
        }

        /*
         * Scrolls so that the element is shown in the middle of the viewport.
         * The viewport will, however, not scroll beyond its contents, given
         * more elements than what the viewport is able to show at once. Under
         * no circumstances will the viewport scroll before its first element.
         */
        case MIDDLE: {
            final double targetMiddle = targetStartPx
                    + (targetEndPx - targetStartPx) / 2;
            return targetMiddle - viewportLength / 2;
        }

        /*
         * Scrolls so that the element is shown at the start of the viewport.
         * The viewport will, however, not scroll beyond its contents.
         */
        case START: {
            return targetStartPx - padding;
        }

        /*
         * Throw an error if we're here. This can only mean that
         * ScrollDestination has been carelessly amended..
         */
        default: {
            throw new IllegalArgumentException(
                    "Internal: ScrollDestination has been modified, "
                            + "but Escalator.getScrollPos has not been updated "
                            + "to match new values.");
        }
        }

    }

    /** An inner class that handles all logic related to scrolling. */
    private class Scroller extends JsniWorkaround {
        private double lastScrollTop = 0;
        private double lastScrollLeft = 0;

        public Scroller() {
            super(Escalator.this);
        }

        @Override
        protected native JavaScriptObject createScrollListenerFunction(
                Escalator esc)
        /*-{
            var vScroll = esc.@com.vaadin.client.widgets.Escalator::verticalScrollbar;
            var vScrollElem = vScroll.@com.vaadin.client.widget.escalator.ScrollbarBundle::getElement()();
        
            var hScroll = esc.@com.vaadin.client.widgets.Escalator::horizontalScrollbar;
            var hScrollElem = hScroll.@com.vaadin.client.widget.escalator.ScrollbarBundle::getElement()();
        
            return $entry(function(e) {
                var target = e.target;
        
                // in case the scroll event was native (i.e. scrollbars were dragged, or
                // the scrollTop/Left was manually modified), the bundles have old cache
                // values. We need to make sure that the caches are kept up to date.
                if (target === vScrollElem) {
                    vScroll.@com.vaadin.client.widget.escalator.ScrollbarBundle::updateScrollPosFromDom()();
                } else if (target === hScrollElem) {
                    hScroll.@com.vaadin.client.widget.escalator.ScrollbarBundle::updateScrollPosFromDom()();
                } else {
                    $wnd.console.error("unexpected scroll target: "+target);
                }
            });
        }-*/;

        @Override
        protected native JavaScriptObject createMousewheelListenerFunction(
                Escalator esc)
        /*-{
            return $entry(function(e) {
                var deltaX = e.deltaX ? e.deltaX : -0.5*e.wheelDeltaX;
                var deltaY = e.deltaY ? e.deltaY : -0.5*e.wheelDeltaY;
        
                // Delta mode 0 is in pixels; we don't need to do anything...
        
                // A delta mode of 1 means we're scrolling by lines instead of pixels
                // We need to scale the number of lines by the default line height
                if (e.deltaMode === 1) {
                    var brc = esc.@com.vaadin.client.widgets.Escalator::body;
                    deltaY *= brc.@com.vaadin.client.widgets.Escalator.AbstractRowContainer::getDefaultRowHeight()();
                }
        
                // Other delta modes aren't supported
                if ((e.deltaMode !== undefined) && (e.deltaMode >= 2 || e.deltaMode < 0)) {
                    var msg = "Unsupported wheel delta mode \"" + e.deltaMode + "\"";
        
                    // Print warning message
                    esc.@com.vaadin.client.widgets.Escalator::logWarning(*)(msg);
                }
        
                // IE8 has only delta y
                if (isNaN(deltaY)) {
                    deltaY = -0.5*e.wheelDelta;
                }
        
                @com.vaadin.client.widgets.Escalator.JsniUtil::moveScrollFromEvent(*)(esc, deltaX, deltaY, e);
            });
        }-*/;

        /**
         * Recalculates the virtual viewport represented by the scrollbars, so
         * that the sizes of the scroll handles appear correct in the browser
         */
        public void recalculateScrollbarsForVirtualViewport() {
            double scrollContentHeight = body.calculateTotalRowHeight()
                    + body.spacerContainer.getSpacerHeightsSum();
            double scrollContentWidth = columnConfiguration.calculateRowWidth();
            double tableWrapperHeight = heightOfEscalator;
            double tableWrapperWidth = widthOfEscalator;

            boolean verticalScrollNeeded = scrollContentHeight > tableWrapperHeight
                    + WidgetUtil.PIXEL_EPSILON - header.getHeightOfSection()
                    - footer.getHeightOfSection();
            boolean horizontalScrollNeeded = scrollContentWidth > tableWrapperWidth
                    + WidgetUtil.PIXEL_EPSILON;

            // One dimension got scrollbars, but not the other. Recheck time!
            if (verticalScrollNeeded != horizontalScrollNeeded) {
                if (!verticalScrollNeeded && horizontalScrollNeeded) {
                    verticalScrollNeeded = scrollContentHeight > tableWrapperHeight
                            + WidgetUtil.PIXEL_EPSILON
                            - header.getHeightOfSection()
                            - footer.getHeightOfSection()
                            - horizontalScrollbar.getScrollbarThickness();
                } else {
                    horizontalScrollNeeded = scrollContentWidth > tableWrapperWidth
                            + WidgetUtil.PIXEL_EPSILON
                            - verticalScrollbar.getScrollbarThickness();
                }
            }

            // let's fix the table wrapper size, since it's now stable.
            if (verticalScrollNeeded) {
                tableWrapperWidth -= verticalScrollbar.getScrollbarThickness();
                tableWrapperWidth = Math.max(0, tableWrapperWidth);
            }
            if (horizontalScrollNeeded) {
                tableWrapperHeight -= horizontalScrollbar
                        .getScrollbarThickness();
                tableWrapperHeight = Math.max(0, tableWrapperHeight);
            }
            tableWrapper.getStyle().setHeight(tableWrapperHeight, Unit.PX);
            tableWrapper.getStyle().setWidth(tableWrapperWidth, Unit.PX);

            double footerHeight = footer.getHeightOfSection();
            double headerHeight = header.getHeightOfSection();
            double vScrollbarHeight = Math.max(0,
                    tableWrapperHeight - footerHeight - headerHeight);
            verticalScrollbar.setOffsetSize(vScrollbarHeight);
            verticalScrollbar.setScrollSize(scrollContentHeight);

            /*
             * If decreasing the amount of frozen columns, and scrolled to the
             * right, the scroll position might reset. So we need to remember
             * the scroll position, and re-apply it once the scrollbar size has
             * been adjusted.
             */
            double prevScrollPos = horizontalScrollbar.getScrollPos();

            double unfrozenPixels = columnConfiguration
                    .getCalculatedColumnsWidth(Range.between(
                            columnConfiguration.getFrozenColumnCount(),
                            columnConfiguration.getColumnCount()));
            double frozenPixels = scrollContentWidth - unfrozenPixels;
            double hScrollOffsetWidth = tableWrapperWidth - frozenPixels;
            horizontalScrollbar.setOffsetSize(hScrollOffsetWidth);
            horizontalScrollbar.setScrollSize(unfrozenPixels);
            horizontalScrollbar.getElement().getStyle().setLeft(frozenPixels,
                    Unit.PX);
            horizontalScrollbar.setScrollPos(prevScrollPos);

            /*
             * only show the scrollbar wrapper if the scrollbar itself is
             * visible.
             */
            if (horizontalScrollbar.showsScrollHandle()) {
                horizontalScrollbarDeco.getStyle().clearDisplay();
            } else {
                horizontalScrollbarDeco.getStyle().setDisplay(Display.NONE);
            }

            /*
             * only show corner background divs if the vertical scrollbar is
             * visible.
             */
            Style hCornerStyle = headerDeco.getStyle();
            Style fCornerStyle = footerDeco.getStyle();
            if (verticalScrollbar.showsScrollHandle()) {
                hCornerStyle.clearDisplay();
                fCornerStyle.clearDisplay();

                if (horizontalScrollbar.showsScrollHandle()) {
                    double offset = horizontalScrollbar.getScrollbarThickness();
                    fCornerStyle.setBottom(offset, Unit.PX);
                } else {
                    fCornerStyle.clearBottom();
                }
            } else {
                hCornerStyle.setDisplay(Display.NONE);
                fCornerStyle.setDisplay(Display.NONE);
            }
        }

        /**
         * Logical scrolling event handler for the entire widget.
         */
        public void onScroll() {

            final double scrollTop = verticalScrollbar.getScrollPos();
            final double scrollLeft = horizontalScrollbar.getScrollPos();
            if (lastScrollLeft != scrollLeft) {
                for (int i = 0; i < columnConfiguration.frozenColumns; i++) {
                    header.updateFreezePosition(i, scrollLeft);
                    body.updateFreezePosition(i, scrollLeft);
                    footer.updateFreezePosition(i, scrollLeft);
                }

                position.set(headElem, -scrollLeft, 0);
                position.set(footElem, -scrollLeft, 0);

                lastScrollLeft = scrollLeft;
            }

            body.setBodyScrollPosition(scrollLeft, scrollTop);

            lastScrollTop = scrollTop;
            body.updateEscalatorRowsOnScroll();
            body.spacerContainer.updateSpacerDecosVisibility();
            /*
             * TODO [[optimize]]: Might avoid a reflow by first calculating new
             * scrolltop and scrolleft, then doing the escalator magic based on
             * those numbers and only updating the positions after that.
             */
        }

        public native void attachScrollListener(Element element)
        /*
         * Attaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
             if (element.addEventListener) {
                 element.addEventListener("scroll", this.@com.vaadin.client.widgets.JsniWorkaround::scrollListenerFunction);
             } else {
                 element.attachEvent("onscroll", this.@com.vaadin.client.widgets.JsniWorkaround::scrollListenerFunction);
             }
        }-*/;

        public native void detachScrollListener(Element element)
        /*
         * Detaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            if (element.addEventListener) {
                element.removeEventListener("scroll", this.@com.vaadin.client.widgets.JsniWorkaround::scrollListenerFunction);
            } else {
                element.detachEvent("onscroll", this.@com.vaadin.client.widgets.JsniWorkaround::scrollListenerFunction);
            }
        }-*/;

        public native void attachMousewheelListener(Element element)
        /*
         * Attaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            // firefox likes "wheel", while others use "mousewheel"
            var eventName = 'onmousewheel' in element ? 'mousewheel' : 'wheel';
            element.addEventListener(eventName, this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
        }-*/;

        public native void detachMousewheelListener(Element element)
        /*
         * Detaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            // firefox likes "wheel", while others use "mousewheel"
            var eventName = element.onwheel===undefined?"mousewheel":"wheel";
            element.removeEventListener(eventName, this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
        }-*/;

        public native void attachTouchListeners(Element element)
        /*
         * Detaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            element.addEventListener("touchstart", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
            element.addEventListener("touchmove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
            element.addEventListener("touchend", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            element.addEventListener("touchcancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
        }-*/;

        public native void detachTouchListeners(Element element)
        /*
         * Detaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            element.removeEventListener("touchstart", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
            element.removeEventListener("touchmove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
            element.removeEventListener("touchend", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            element.removeEventListener("touchcancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
        }-*/;

        /**
         * Using pointerdown, pointermove, pointerup, and pointercancel for IE11
         * and Edge instead of touch* listeners (#18737)
         *
         * @param element
         */
        public native void attachPointerEventListeners(Element element)
        /*
         * Attaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            element.addEventListener("pointerdown", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
            element.addEventListener("pointermove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
            element.addEventListener("pointerup", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            element.addEventListener("pointercancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
        }-*/;

        /**
         * Using pointerdown, pointermove, pointerup, and pointercancel for IE11
         * and Edge instead of touch* listeners (#18737)
         *
         * @param element
         */
        public native void detachPointerEventListeners(Element element)
        /*
         * Detaching events with JSNI instead of the GWT event mechanism because
         * GWT didn't provide enough details in events, or triggering the event
         * handlers with GWT bindings was unsuccessful. Maybe, with more time
         * and skill, it could be done with better success. JavaScript overlay
         * types might work. This might also get rid of the JsniWorkaround
         * class.
         */
        /*-{
            element.removeEventListener("pointerdown", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
            element.removeEventListener("pointermove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
            element.removeEventListener("pointerup", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            element.removeEventListener("pointercancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
        }-*/;

        public void scrollToColumn(final int columnIndex,
                final ScrollDestination destination, final int padding) {
            assert columnIndex >= columnConfiguration.frozenColumns : "Can't scroll to a frozen column";

            /*
             * To cope with frozen columns, we just pretend those columns are
             * not there at all when calculating the position of the target
             * column and the boundaries of the viewport. The resulting
             * scrollLeft will be correct without compensation since the DOM
             * structure effectively means that scrollLeft also ignores the
             * frozen columns.
             */
            final double frozenPixels = columnConfiguration
                    .getCalculatedColumnsWidth(Range.withLength(0,
                            columnConfiguration.frozenColumns));

            final double targetStartPx = columnConfiguration
                    .getCalculatedColumnsWidth(Range.withLength(0, columnIndex))
                    - frozenPixels;
            final double targetEndPx = targetStartPx
                    + columnConfiguration.getColumnWidthActual(columnIndex);

            final double viewportStartPx = getScrollLeft();
            double viewportEndPx = viewportStartPx
                    + getBoundingWidth(getElement()) - frozenPixels;
            if (verticalScrollbar.showsScrollHandle()) {
                viewportEndPx -= WidgetUtil.getNativeScrollbarSize();
            }

            final double scrollLeft = getScrollPos(destination, targetStartPx,
                    targetEndPx, viewportStartPx, viewportEndPx, padding);

            /*
             * note that it doesn't matter if the scroll would go beyond the
             * content, since the browser will adjust for that, and everything
             * fall into line accordingly.
             */
            setScrollLeft(scrollLeft);
        }

        public void scrollToRow(final int rowIndex,
                final ScrollDestination destination, final double padding) {
            body.scrollToRowSpacerOrBoth(rowIndex, destination, padding,
                    ScrollType.ROW);
        }
    }

    /**
     * Helper class that helps to implement the WAI-ARIA functionality for the
     * Grid and TreeGrid component.
     * <p>
     * The following WAI-ARIA attributes are added through this class:
     *
     * <ul>
     * <li>aria-rowcount (since 8.2)</li>
     * <li>roles provided by {@link AriaGridRole} (since 8.2)</li>
     * </ul>
     *
     * @since 8.2
     */
    public class AriaGridHelper {

        /**
         * This field contains the total number of rows from the grid including
         * rows from thead, tbody and tfoot.
         *
         * @since 8.2
         */
        private int allRows;

        /**
         * Adds the given numberOfRows to allRows and calls
         * {@link #updateAriaRowCount()}.
         *
         * @param numberOfRows
         *            number of rows that were added to the grid
         *
         * @since 8.2
         */
        public void addRows(int numberOfRows) {
            allRows += numberOfRows;
            updateAriaRowCount();
        }

        /**
         * Removes the given numberOfRows from allRows and calls
         * {@link #updateAriaRowCount()}.
         *
         * @param numberOfRows
         *            number of rows that were removed from the grid
         *
         * @since 8.2
         */
        public void removeRows(int numberOfRows) {
            allRows -= numberOfRows;
            updateAriaRowCount();
        }

        /**
         * Sets the aria-rowcount attribute with the current value of
         * {@link AriaGridHelper#allRows} if the grid is attached and
         * {@link AriaGridHelper#allRows} > 0.
         *
         * @since 8.2
         */
        public void updateAriaRowCount() {
            if (!isAttached() || 0 > allRows) {

                return;
            }

            getTable().setAttribute("aria-rowcount", String.valueOf(allRows));
        }

        /**
         * Sets the {@code role} attribute to the given element.
         *
         * @param element
         *            element that should get the role attribute
         * @param role
         *            role to be added
         *
         * @since 8.2
         */
        public void updateRole(final Element element, AriaGridRole role) {
            element.setAttribute("role", role.getName());
        }
    }

    /**
     * Holds the currently used aria roles within the grid for rows and cells.
     *
     * @since 8.2
     */
    public enum AriaGridRole {

        ROW("row"), ROWHEADER("rowheader"), ROWGROUP("rowgroup"), GRIDCELL(
                "gridcell"), COLUMNHEADER("columnheader");

        private final String name;

        AriaGridRole(String name) {
            this.name = name;
        }

        /**
         * Return the name of the {@link AriaGridRole}.
         *
         * @return String name to be used as role attribute
         */
        public String getName() {
            return name;
        }
    }

    public abstract class AbstractRowContainer implements RowContainer {
        private EscalatorUpdater updater = EscalatorUpdater.NULL;

        private int rows;

        /**
         * The table section element ({@code <thead>}, {@code <tbody>} or
         * {@code <tfoot>}) the rows (i.e. <code>&lt;tr&gt;</code> tags) are
         * contained in.
         */
        protected final TableSectionElement root;

        /**
         * The primary style name of the escalator. Most commonly provided by
         * Escalator as "v-escalator".
         */
        private String primaryStyleName = null;

        private boolean defaultRowHeightShouldBeAutodetected = true;

        private double defaultRowHeight = INITIAL_DEFAULT_ROW_HEIGHT;

        private boolean initialColumnSizesCalculated = false;

        private boolean autodetectingRowHeightLater = false;

        public AbstractRowContainer(
                final TableSectionElement rowContainerElement) {
            root = rowContainerElement;
            ariaGridHelper.updateRole(root, AriaGridRole.ROWGROUP);
        }

        @Override
        public TableSectionElement getElement() {
            return root;
        }

        /**
         * Gets the tag name of an element to represent a cell in a row.
         * <p>
         * Usually {@code "th"} or {@code "td"}.
         * <p>
         * <em>Note:</em> To actually <em>create</em> such an element, use
         * {@link #createCellElement(double)} instead.
         *
         * @return the tag name for the element to represent cells as
         * @see #createCellElement(double)
         */
        protected abstract String getCellElementTagName();

        /**
         * Gets the role attribute of an element to represent a cell in a row.
         * <p>
         * Usually {@link AriaGridRole#GRIDCELL} except for a cell in the
         * header.
         *
         * @return the role attribute for the element to represent cells
         *
         * @since 8.2
         */
        protected AriaGridRole getCellElementRole() {
            return AriaGridRole.GRIDCELL;
        }

        /**
         * Gets the role attribute of an element to represent a row in a grid.
         * <p>
         * Usually {@link AriaGridRole#ROW} except for a row in the header.
         *
         * @return the role attribute for the element to represent rows
         *
         * @since 8.2
         */
        protected AriaGridRole getRowElementRole() {
            return AriaGridRole.ROW;
        }

        @Override
        public EscalatorUpdater getEscalatorUpdater() {
            return updater;
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for rows or columns
         * when this method is called.
         *
         * @see #hasColumnAndRowData()
         */
        @Override
        public void setEscalatorUpdater(
                final EscalatorUpdater escalatorUpdater) {
            if (escalatorUpdater == null) {
                throw new IllegalArgumentException(
                        "escalator updater cannot be null");
            }

            updater = escalatorUpdater;

            if (hasColumnAndRowData() && getRowCount() > 0) {
                refreshRows(0, getRowCount());
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there are no rows in the DOM when
         * this method is called.
         *
         * @see #hasSomethingInDom()
         */
        @Override
        public void removeRows(final int index, final int numberOfRows) {
            assertArgumentsAreValidAndWithinRange(index, numberOfRows);

            rows -= numberOfRows;
            ariaGridHelper.removeRows(numberOfRows);

            if (!isAttached()) {
                return;
            }

            if (hasSomethingInDom()) {
                paintRemoveRows(index, numberOfRows);
            }
        }

        /**
         * Removes those row elements from the DOM that correspond to the given
         * range of logical indices. This may be fewer than {@code numberOfRows}
         * , even zero, if not all the removed rows are actually visible.
         * <p>
         * The implementation must call
         * {@link #paintRemoveRow(TableRowElement, int)} for each row that is
         * removed from the DOM.
         *
         * @param index
         *            the logical index of the first removed row
         * @param numberOfRows
         *            number of logical rows to remove
         */
        protected abstract void paintRemoveRows(final int index,
                final int numberOfRows);

        /**
         * Removes a row element from the DOM, invoking
         * {@link #getEscalatorUpdater()}
         * {@link EscalatorUpdater#preDetach(Row, Iterable) preDetach} and
         * {@link EscalatorUpdater#postDetach(Row, Iterable) postDetach} before
         * and after removing the row, respectively.
         * <p>
         * This method must be called for each removed DOM row by any
         * {@link #paintRemoveRows(int, int)} implementation.
         *
         * @param tr
         *            the row element to remove.
         * @param logicalRowIndex
         *            logical index of the row that is to be removed
         */
        protected void paintRemoveRow(final TableRowElement tr,
                final int logicalRowIndex) {

            flyweightRow.setup(tr, logicalRowIndex,
                    columnConfiguration.getCalculatedColumnWidths());

            getEscalatorUpdater().preDetach(flyweightRow,
                    flyweightRow.getCells());

            tr.removeFromParent();

            getEscalatorUpdater().postDetach(flyweightRow,
                    flyweightRow.getCells());

            /*
             * the "assert" guarantees that this code is run only during
             * development/debugging.
             */
            assert flyweightRow.teardown();

        }

        protected void assertArgumentsAreValidAndWithinRange(final int index,
                final int numberOfRows)
                throws IllegalArgumentException, IndexOutOfBoundsException {
            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            if (index < 0 || index + numberOfRows > getRowCount()) {
                throw new IndexOutOfBoundsException("The given " + "row range ("
                        + index + ".." + (index + numberOfRows)
                        + ") was outside of the current number of rows ("
                        + getRowCount() + ")");
            }
        }

        @Override
        public int getRowCount() {
            return rows;
        }

        /**
         * This method calculates the current row count directly from the DOM.
         * <p>
         * While Escalator is stable, this value should equal to
         * {@link #getRowCount()}, but while row counts are being updated, these
         * two values might differ for a short while.
         * <p>
         * Any extra content, such as spacers for the body, should not be
         * included in this count.
         *
         * @since 7.5.0
         *
         * @return the actual DOM count of rows
         */
        public abstract int getDomRowCount();

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for columns when
         * this method is called.
         *
         * @see #hasColumnAndRowData()
         */
        @Override
        public void insertRows(final int index, final int numberOfRows) {
            if (index < 0 || index > getRowCount()) {
                throw new IndexOutOfBoundsException("The given index (" + index
                        + ") was outside of the current number of rows (0.."
                        + getRowCount() + ")");
            }

            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            rows += numberOfRows;
            ariaGridHelper.addRows(numberOfRows);
            /*
             * only add items in the DOM if the widget itself is attached to the
             * DOM. We can't calculate sizes otherwise.
             */
            if (isAttached()) {
                paintInsertRows(index, numberOfRows);

                /*
                 * We are inserting the first rows in this container. We
                 * potentially need to set the widths for the cells for the
                 * first time.
                 */
                if (rows == numberOfRows) {
                    Scheduler.get().scheduleFinally(() -> {
                        if (initialColumnSizesCalculated) {
                            return;
                        }
                        initialColumnSizesCalculated = true;

                        Map<Integer, Double> colWidths = new HashMap<>();
                        for (int i = 0; i < getColumnConfiguration()
                                .getColumnCount(); i++) {
                            Double width = Double.valueOf(
                                    getColumnConfiguration().getColumnWidth(i));
                            Integer col = Integer.valueOf(i);
                            colWidths.put(col, width);
                        }
                        getColumnConfiguration().setColumnWidths(colWidths);
                    });
                }
            }
        }

        /**
         * Actually add rows into the DOM, now that everything can be
         * calculated.
         *
         * @param visualIndex
         *            the DOM index to add rows into
         * @param numberOfRows
         *            the number of rows to insert
         */
        protected abstract void paintInsertRows(final int visualIndex,
                final int numberOfRows);

        protected List<TableRowElement> paintInsertStaticRows(
                final int visualIndex, final int numberOfRows) {
            assert isAttached() : "Can't paint rows if Escalator is not attached";

            final List<TableRowElement> addedRows = new ArrayList<>();

            if (numberOfRows < 1) {
                return addedRows;
            }

            Node referenceRow;
            if (root.getChildCount() != 0 && visualIndex != 0) {
                // get the row node we're inserting stuff after
                referenceRow = root.getChild(visualIndex - 1);
            } else {
                // index is 0, so just prepend.
                referenceRow = null;
            }

            for (int row = visualIndex; row < visualIndex
                    + numberOfRows; row++) {
                final TableRowElement tr = TableRowElement.as(DOM.createTR());
                addedRows.add(tr);
                tr.addClassName(getStylePrimaryName() + "-row");
                ariaGridHelper.updateRole(tr, getRowElementRole());

                for (int col = 0; col < columnConfiguration
                        .getColumnCount(); col++) {
                    final double colWidth = columnConfiguration
                            .getColumnWidthActual(col);
                    final TableCellElement cellElem = createCellElement(
                            colWidth);
                    tr.appendChild(cellElem);
                    // Set stylename and position if new cell is frozen
                    if (col < columnConfiguration.frozenColumns) {
                        cellElem.addClassName("frozen");
                        position.set(cellElem, scroller.lastScrollLeft, 0);
                    }
                    if (columnConfiguration.frozenColumns > 0
                            && col == columnConfiguration.frozenColumns - 1) {
                        cellElem.addClassName("last-frozen");
                    }
                }

                referenceRow = paintInsertRow(referenceRow, tr, row);
            }
            reapplyRowWidths();

            recalculateSectionHeight();

            return addedRows;
        }

        /**
         * Inserts a single row into the DOM, invoking
         * {@link #getEscalatorUpdater()}
         * {@link EscalatorUpdater#preAttach(Row, Iterable) preAttach} and
         * {@link EscalatorUpdater#postAttach(Row, Iterable) postAttach} before
         * and after inserting the row, respectively. The row should have its
         * cells already inserted.
         *
         * @param referenceRow
         *            the row after which to insert or null if insert as first
         * @param tr
         *            the row to be inserted
         * @param logicalRowIndex
         *            the logical index of the inserted row
         * @return the inserted row to be used as the new reference
         */
        protected Node paintInsertRow(Node referenceRow,
                final TableRowElement tr, int logicalRowIndex) {
            flyweightRow.setup(tr, logicalRowIndex,
                    columnConfiguration.getCalculatedColumnWidths());

            getEscalatorUpdater().preAttach(flyweightRow,
                    flyweightRow.getCells());

            referenceRow = insertAfterReferenceAndUpdateIt(root, tr,
                    referenceRow);

            getEscalatorUpdater().postAttach(flyweightRow,
                    flyweightRow.getCells());
            updater.update(flyweightRow, flyweightRow.getCells());

            /*
             * the "assert" guarantees that this code is run only during
             * development/debugging.
             */
            assert flyweightRow.teardown();
            return referenceRow;
        }

        private Node insertAfterReferenceAndUpdateIt(final Element parent,
                final Element elem, final Node referenceNode) {
            if (referenceNode != null) {
                parent.insertAfter(elem, referenceNode);
            } else {
                /*
                 * referencenode being null means we have offset 0, i.e. make it
                 * the first row
                 */
                /*
                 * TODO [[optimize]]: Is insertFirst or append faster for an
                 * empty root?
                 */
                parent.insertFirst(elem);
            }
            return elem;
        }

        protected abstract void recalculateSectionHeight();

        /**
         * Returns the height of all rows in the row container.
         */
        protected double calculateTotalRowHeight() {
            return getDefaultRowHeight() * getRowCount();
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for columns when
         * this method is called.
         *
         * @see #hasColumnAndRowData()
         */
        @Override
        // overridden because of JavaDoc
        public void refreshRows(final int index, final int numberOfRows) {
            Range rowRange = Range.withLength(index, numberOfRows);
            Range colRange = Range.withLength(0,
                    getColumnConfiguration().getColumnCount());
            refreshCells(rowRange, colRange);
        }

        protected abstract void refreshCells(Range logicalRowRange,
                Range colRange);

        void refreshRow(TableRowElement tr, int logicalRowIndex) {
            refreshRow(tr, logicalRowIndex, Range.withLength(0,
                    getColumnConfiguration().getColumnCount()));
        }

        void refreshRow(final TableRowElement tr, final int logicalRowIndex,
                Range colRange) {
            flyweightRow.setup(tr, logicalRowIndex,
                    columnConfiguration.getCalculatedColumnWidths());
            Iterable<FlyweightCell> cellsToUpdate = flyweightRow
                    .getCells(colRange.getStart(), colRange.length());
            updater.update(flyweightRow, cellsToUpdate);

            /*
             * the "assert" guarantees that this code is run only during
             * development/debugging.
             */
            assert flyweightRow.teardown();
        }

        /**
         * Create and setup an empty cell element.
         *
         * @param width
         *            the width of the cell, in pixels
         *
         * @return a set-up empty cell element
         */
        public TableCellElement createCellElement(final double width) {
            final TableCellElement cellElem = TableCellElement
                    .as(DOM.createElement(getCellElementTagName()));

            final double height = getDefaultRowHeight();
            assert height >= 0 : "defaultRowHeight was negative. There's a setter leak somewhere.";
            cellElem.getStyle().setHeight(height, Unit.PX);

            if (width >= 0) {
                cellElem.getStyle().setWidth(width, Unit.PX);
            }
            cellElem.addClassName(getStylePrimaryName() + "-cell");
            ariaGridHelper.updateRole(cellElem, getCellElementRole());
            return cellElem;
        }

        @Override
        public TableRowElement getRowElement(int index) {
            return getTrByVisualIndex(index);
        }

        /**
         * Gets the child element that is visually at a certain index.
         *
         * @param index
         *            the index of the element to retrieve
         * @return the element at position {@code index}
         * @throws IndexOutOfBoundsException
         *             if {@code index} is not valid within {@link #root}
         */
        protected abstract TableRowElement getTrByVisualIndex(int index)
                throws IndexOutOfBoundsException;

        protected void paintRemoveColumns(final int offset,
                final int numberOfColumns) {
            for (int i = 0; i < getDomRowCount(); i++) {
                TableRowElement row = getTrByVisualIndex(i);
                flyweightRow.setup(row, i,
                        columnConfiguration.getCalculatedColumnWidths());

                Iterable<FlyweightCell> attachedCells = flyweightRow
                        .getCells(offset, numberOfColumns);
                getEscalatorUpdater().preDetach(flyweightRow, attachedCells);

                for (int j = 0; j < numberOfColumns; j++) {
                    row.getCells().getItem(offset).removeFromParent();
                }

                Iterable<FlyweightCell> detachedCells = flyweightRow
                        .getUnattachedCells(offset, numberOfColumns);
                getEscalatorUpdater().postDetach(flyweightRow, detachedCells);

                assert flyweightRow.teardown();
            }
        }

        protected void paintInsertColumns(final int offset,
                final int numberOfColumns, boolean frozen) {

            for (int row = 0; row < getDomRowCount(); row++) {
                final TableRowElement tr = getTrByVisualIndex(row);
                int logicalRowIndex = getLogicalRowIndex(tr);
                paintInsertCells(tr, logicalRowIndex, offset, numberOfColumns);
            }
            reapplyRowWidths();

            if (frozen) {
                for (int col = offset; col < offset + numberOfColumns; col++) {
                    setColumnFrozen(col, true);
                }
            }
        }

        /**
         * Inserts new cell elements into a single row element, invoking
         * {@link #getEscalatorUpdater()}
         * {@link EscalatorUpdater#preAttach(Row, Iterable) preAttach} and
         * {@link EscalatorUpdater#postAttach(Row, Iterable) postAttach} before
         * and after inserting the cells, respectively.
         * <p>
         * Precondition: The row must be already attached to the DOM and the
         * FlyweightCell instances corresponding to the new columns added to
         * {@code flyweightRow}.
         *
         * @param tr
         *            the row in which to insert the cells
         * @param logicalRowIndex
         *            the index of the row
         * @param offset
         *            the index of the first cell
         * @param numberOfCells
         *            the number of cells to insert
         */
        private void paintInsertCells(final TableRowElement tr,
                int logicalRowIndex, final int offset,
                final int numberOfCells) {

            assert root.isOrHasChild(
                    tr) : "The row must be attached to the document";

            flyweightRow.setup(tr, logicalRowIndex,
                    columnConfiguration.getCalculatedColumnWidths());

            Iterable<FlyweightCell> cells = flyweightRow
                    .getUnattachedCells(offset, numberOfCells);

            for (FlyweightCell cell : cells) {
                final double colWidth = columnConfiguration
                        .getColumnWidthActual(cell.getColumn());
                final TableCellElement cellElem = createCellElement(colWidth);
                cell.setElement(cellElem);
            }

            getEscalatorUpdater().preAttach(flyweightRow, cells);

            Node referenceCell;
            if (offset != 0) {
                referenceCell = tr.getChild(offset - 1);
            } else {
                referenceCell = null;
            }

            for (FlyweightCell cell : cells) {
                referenceCell = insertAfterReferenceAndUpdateIt(tr,
                        cell.getElement(), referenceCell);
            }

            getEscalatorUpdater().postAttach(flyweightRow, cells);
            getEscalatorUpdater().update(flyweightRow, cells);

            assert flyweightRow.teardown();
        }

        public void setColumnFrozen(int column, boolean frozen) {
            toggleFrozenColumnClass(column, frozen, "frozen");

            if (frozen) {
                updateFreezePosition(column, scroller.lastScrollLeft);
            }
        }

        private void toggleFrozenColumnClass(int column, boolean frozen,
                String className) {
            final NodeList<TableRowElement> childRows = root.getRows();

            for (int row = 0; row < childRows.getLength(); row++) {
                final TableRowElement tr = childRows.getItem(row);
                if (!rowCanBeFrozen(tr)) {
                    continue;
                }

                TableCellElement cell = tr.getCells().getItem(column);
                if (frozen) {
                    cell.addClassName(className);
                } else {
                    cell.removeClassName(className);
                    position.reset(cell);
                }
            }
        }

        public void setColumnLastFrozen(int column, boolean lastFrozen) {
            toggleFrozenColumnClass(column, lastFrozen, "last-frozen");
        }

        public void updateFreezePosition(int column, double scrollLeft) {
            final NodeList<TableRowElement> childRows = root.getRows();

            for (int row = 0; row < childRows.getLength(); row++) {
                final TableRowElement tr = childRows.getItem(row);

                if (rowCanBeFrozen(tr)) {
                    TableCellElement cell = tr.getCells().getItem(column);
                    position.set(cell, scrollLeft, 0);
                }
            }
        }

        /**
         * Checks whether a row is an element, or contains such elements, that
         * can be frozen.
         * <p>
         * In practice, this applies for all header and footer rows. For body
         * rows, it applies for all rows except spacer rows.
         *
         * @since 7.5.0
         *
         * @param tr
         *            the row element to check whether it, or any of its its
         *            descendants can be frozen
         * @return <code>true</code> if the given element, or any of its
         *         descendants, can be frozen
         */
        protected abstract boolean rowCanBeFrozen(TableRowElement tr);

        /**
         * Iterates through all the cells in a column and returns the width of
         * the widest element in this RowContainer.
         *
         * @param index
         *            the index of the column to inspect
         * @return the pixel width of the widest element in the indicated column
         */
        public double calculateMaxColWidth(int index) {
            TableRowElement row = TableRowElement
                    .as(root.getFirstChildElement());
            double maxWidth = 0;
            while (row != null) {
                final TableCellElement cell = row.getCells().getItem(index);
                final boolean isVisible = !cell.getStyle().getDisplay()
                        .equals(Display.NONE.getCssName());
                if (isVisible) {
                    maxWidth = Math.max(maxWidth, getBoundingWidth(cell));
                }
                row = TableRowElement.as(row.getNextSiblingElement());
            }
            return maxWidth;
        }

        /**
         * Reapplies all the cells' widths according to the calculated widths in
         * the column configuration.
         */
        public void reapplyColumnWidths() {
            Element row = root.getFirstChildElement();
            while (row != null) {
                // Only handle non-spacer rows
                if (!body.spacerContainer.isSpacer(row)) {
                    Element cell = row.getFirstChildElement();
                    int columnIndex = 0;
                    while (cell != null) {
                        final double width = getCalculatedColumnWidthWithColspan(
                                cell, columnIndex);

                        /*
                         * TODO Should Escalator implement ProvidesResize at
                         * some point, this is where we need to do that.
                         */
                        cell.getStyle().setWidth(width, Unit.PX);

                        cell = cell.getNextSiblingElement();
                        columnIndex++;
                    }
                }
                row = row.getNextSiblingElement();
            }

            reapplyRowWidths();
        }

        private double getCalculatedColumnWidthWithColspan(final Element cell,
                final int columnIndex) {
            final int colspan = cell.getPropertyInt(FlyweightCell.COLSPAN_ATTR);
            Range spannedColumns = Range.withLength(columnIndex, colspan);

            /*
             * Since browsers don't explode with overflowing colspans, escalator
             * shouldn't either.
             */
            if (spannedColumns.getEnd() > columnConfiguration
                    .getColumnCount()) {
                spannedColumns = Range.between(columnIndex,
                        columnConfiguration.getColumnCount());
            }
            return columnConfiguration
                    .getCalculatedColumnsWidth(spannedColumns);
        }

        /**
         * Applies the total length of the columns to each row element.
         * <p>
         * <em>Note:</em> In contrast to {@link #reapplyColumnWidths()}, this
         * method only modifies the width of the {@code
         *
        <tr>
         * } element, not the cells within.
         */
        protected void reapplyRowWidths() {
            double rowWidth = columnConfiguration.calculateRowWidth();
            if (rowWidth < 0) {
                return;
            }

            Element row = root.getFirstChildElement();
            while (row != null) {
                // IF there is a rounding error when summing the columns, we
                // need to round the tr width up to ensure that columns fit and
                // do not wrap
                // E.g.122.95+123.25+103.75+209.25+83.52+88.57+263.45+131.21+126.85+113.13=1365.9299999999998
                // For this we must set 1365.93 or the last column will wrap
                row.getStyle().setWidth(WidgetUtil.roundSizeUp(rowWidth),
                        Unit.PX);
                row = row.getNextSiblingElement();
            }
        }

        /**
         * The primary style name for the container.
         *
         * @param primaryStyleName
         *            the style name to use as prefix for all row and cell style
         *            names.
         */
        protected void setStylePrimaryName(String primaryStyleName) {
            String oldStyle = getStylePrimaryName();
            if (SharedUtil.equals(oldStyle, primaryStyleName)) {
                return;
            }

            this.primaryStyleName = primaryStyleName;

            // Update already rendered rows and cells
            Element row = root.getRows().getItem(0);
            while (row != null) {
                UIObject.setStylePrimaryName(row, primaryStyleName + "-row");
                Element cell = TableRowElement.as(row).getCells().getItem(0);
                while (cell != null) {
                    assert TableCellElement.is(cell);
                    UIObject.setStylePrimaryName(cell,
                            primaryStyleName + "-cell");
                    cell = cell.getNextSiblingElement();
                }
                row = row.getNextSiblingElement();
            }
        }

        /**
         * Returns the primary style name of the container.
         *
         * @return The primary style name or <code>null</code> if not set.
         */
        protected String getStylePrimaryName() {
            return primaryStyleName;
        }

        @Override
        public void setDefaultRowHeight(double px)
                throws IllegalArgumentException {
            if (px < 1) {
                throw new IllegalArgumentException(
                        "Height must be positive. " + px + " was given.");
            }

            defaultRowHeightShouldBeAutodetected = false;
            defaultRowHeight = px;
            reapplyDefaultRowHeights();
            applyHeightByRows();
        }

        @Override
        public double getDefaultRowHeight() {
            return defaultRowHeight;
        }

        /**
         * The default height of rows has (most probably) changed.
         * <p>
         * Make sure that the displayed rows with a default height are updated
         * in height and top position.
         * <p>
         * <em>Note:</em>This implementation should not call
         * {@link Escalator#recalculateElementSizes()} - it is done by the
         * discretion of the caller of this method.
         */
        protected abstract void reapplyDefaultRowHeights();

        protected void reapplyRowHeight(final TableRowElement tr,
                final double heightPx) {
            assert heightPx >= 0 : "Height must not be negative";

            Element cellElem = tr.getFirstChildElement();
            while (cellElem != null) {
                cellElem.getStyle().setHeight(heightPx, Unit.PX);
                cellElem = cellElem.getNextSiblingElement();
            }

            /*
             * no need to apply height to tr-element, it'll be resized
             * implicitly.
             */
        }

        protected void setRowPosition(final TableRowElement tr, final int x,
                final double y) {
            positions.set(tr, x, y);
        }

        /**
         * Returns <em>the assigned</em> top position for the given element.
         * <p>
         * <em>Note:</em> This method does not calculate what a row's top
         * position should be. It just returns an assigned value, correct or
         * not.
         *
         * @param tr
         *            the table row element to measure
         * @return the current top position for {@code tr}
         * @see BodyRowContainerImpl#getRowTop(int)
         */
        protected double getRowTop(final TableRowElement tr) {
            return positions.getTop(tr);
        }

        protected void removeRowPosition(TableRowElement tr) {
            positions.remove(tr);
        }

        /**
         * Triggers delayed auto-detection of default row height if it hasn't
         * been set by that point and the Escalator is both attached and
         * displayed.
         */
        public void autodetectRowHeightLater() {
            autodetectingRowHeightLater = true;
            Scheduler.get().scheduleFinally(() -> {
                if (defaultRowHeightShouldBeAutodetected && isAttached()
                        && WidgetUtil.isDisplayed(getElement())) {
                    autodetectRowHeightNow();
                    defaultRowHeightShouldBeAutodetected = false;
                }
                autodetectingRowHeightLater = false;
            });
        }

        @Override
        public boolean isAutodetectingRowHeightLater() {
            return autodetectingRowHeightLater;
        }

        private void fireRowHeightChangedEventFinally() {
            if (!rowHeightChangedEventFired) {
                rowHeightChangedEventFired = true;
                Scheduler.get().scheduleFinally(() -> {
                    fireEvent(new RowHeightChangedEvent());
                    rowHeightChangedEventFired = false;
                });
            }
        }

        /**
         * Auto-detect row height immediately, if possible. If Escalator isn't
         * attached and displayed yet, auto-detecting cannot be performed
         * correctly. In such cases auto-detecting is left to wait for these
         * conditions to change, and will be performed when they do.
         */
        public void autodetectRowHeightNow() {
            if (!isAttached() || !WidgetUtil.isDisplayed(getElement())) {
                // Run again when attached and displayed
                defaultRowHeightShouldBeAutodetected = true;
                return;
            }

            final double oldRowHeight = defaultRowHeight;

            final Element detectionTr = DOM.createTR();
            detectionTr.setClassName(getStylePrimaryName() + "-row");

            final Element cellElem = DOM.createElement(getCellElementTagName());
            cellElem.setClassName(getStylePrimaryName() + "-cell");
            cellElem.setInnerText("Ij");

            detectionTr.appendChild(cellElem);
            root.appendChild(detectionTr);
            double boundingHeight = getBoundingHeight(cellElem);
            defaultRowHeight = Math.max(1.0d, boundingHeight);
            root.removeChild(detectionTr);

            if (root.hasChildNodes()) {
                reapplyDefaultRowHeights();
                applyHeightByRows();
            }

            if (oldRowHeight != defaultRowHeight) {
                fireRowHeightChangedEventFinally();
            }
        }

        @Override
        public Cell getCell(final Element element) {
            if (element == null) {
                throw new IllegalArgumentException("Element cannot be null");
            }

            /*
             * Ensure that element is not root nor the direct descendant of root
             * (a row or spacer) and ensure the element is inside the dom
             * hierarchy of the root element. If not, return null.
             */
            if (root == element || element.getParentElement() == root
                    || !root.isOrHasChild(element)) {
                return null;
            }

            /*
             * Ensure element is the cell element by iterating up the DOM
             * hierarchy until reaching cell element.
             */
            Element cellElementCandidate = element;
            while (cellElementCandidate.getParentElement()
                    .getParentElement() != root) {
                cellElementCandidate = cellElementCandidate.getParentElement();
            }
            final TableCellElement cellElement = TableCellElement
                    .as(cellElementCandidate);

            // Find dom column
            int domColumnIndex = -1;
            for (Element e = cellElement; e != null; e = e
                    .getPreviousSiblingElement()) {
                domColumnIndex++;
            }

            // Find dom row
            int domRowIndex = -1;
            for (Element e = cellElement.getParentElement(); e != null; e = e
                    .getPreviousSiblingElement()) {
                domRowIndex++;
            }

            return new Cell(domRowIndex, domColumnIndex, cellElement);
        }

        double measureCellWidth(TableCellElement cell, boolean withContent) {
            /*
             * To get the actual width of the contents, we need to get the cell
             * content without any hardcoded height or width.
             *
             * But we don't want to modify the existing column, because that
             * might trigger some unnecessary listeners and whatnot. So,
             * instead, we make a deep clone of that cell, but without any
             * explicit dimensions, and measure that instead.
             */

            TableCellElement cellClone = TableCellElement
                    .as((Element) cell.cloneNode(withContent));
            if (!withContent || columnConfiguration
                    .getColumnWidth(cell.getCellIndex()) < 0) {
                clearRelativeWidthContents(cellClone);
            }
            cellClone.getStyle().clearHeight();
            cellClone.getStyle().clearWidth();

            cell.getParentElement().insertBefore(cellClone, cell);
            double requiredWidth = getBoundingWidth(cellClone);
            if (BrowserInfo.get().isIE()) {
                /*
                 * IE browsers have some issues with subpixels. Occasionally
                 * content is overflown even if not necessary. Increase the
                 * counted required size by 0.01 just to be on the safe side.
                 */
                requiredWidth += 0.01;
            }

            cellClone.removeFromParent();

            return requiredWidth;
        }

        /**
         * Contents of an element that is configured to have relative width
         * shouldn't be taken into consideration when measuring minimum widths.
         * Thus any such contents within the element hierarchy need to be
         * cleared out for accurate results. The element itself should remain,
         * however, in case it has styles that affect the end results.
         *
         * @param elem
         *            an element that might have unnecessary content that
         *            interferes with minimum width calculations
         */
        private void clearRelativeWidthContents(Element elem) {
            try {
                String width = elem.getStyle().getWidth();
                if (width != null && width.endsWith("%")) {
                    if (elem.hasChildNodes()) {
                        elem.removeAllChildren();
                        // add a fake child so that :empty behavior doesn't
                        // change
                        elem.setInnerHTML("<a/>");
                    } else {
                        elem.setInnerHTML(null);
                    }
                }
            } catch (JavaScriptException e) {
                // no width set, move on
            }
            for (int i = 0; i < elem.getChildCount(); ++i) {
                Node node = elem.getChild(i);
                if (node instanceof Element) {
                    clearRelativeWidthContents((Element) node);
                }
            }
        }

        /**
         * Gets the minimum width needed to display the cell properly.
         *
         * @param colIndex
         *            index of column to measure
         * @param withContent
         *            <code>true</code> if content is taken into account,
         *            <code>false</code> if not
         * @return cell width needed for displaying correctly
         */
        double measureMinCellWidth(int colIndex, boolean withContent) {
            assert isAttached() : "Can't measure max width of cell, since Escalator is not attached to the DOM.";

            double minCellWidth = -1;
            NodeList<TableRowElement> rows = root.getRows();

            for (int row = 0; row < rows.getLength(); row++) {

                TableCellElement cell = rows.getItem(row).getCells()
                        .getItem(colIndex);

                if (cell != null && !cellIsPartOfSpan(cell)) {
                    double cellWidth = measureCellWidth(cell, withContent);
                    minCellWidth = Math.max(minCellWidth, cellWidth);
                }
            }

            return minCellWidth;
        }

        private boolean cellIsPartOfSpan(TableCellElement cell) {
            boolean cellHasColspan = cell.getColSpan() > 1;
            boolean cellIsHidden = Display.NONE.getCssName()
                    .equals(cell.getStyle().getDisplay());
            return cellHasColspan || cellIsHidden;
        }

        void refreshColumns(int index, int numberOfColumns) {
            if (getRowCount() > 0) {
                Range rowRange = Range.withLength(0, getRowCount());
                Range colRange = Range.withLength(index, numberOfColumns);
                refreshCells(rowRange, colRange);
            }
        }

        /**
         * The height of this table section.
         * <p>
         * Note that {@link Escalator#getBody() the body} will calculate its
         * height, while the others will return a precomputed value.
         *
         * @since 7.5.0
         *
         * @return the height of this table section
         */
        protected abstract double getHeightOfSection();

        /**
         * Gets the logical row index for the given table row element.
         *
         * @param tr
         *            the table row element inside this container.
         * @return the logical index of the given element
         */
        public int getLogicalRowIndex(final TableRowElement tr) {
            // Note: BodyRowContainerImpl overrides this behaviour, since the
            // physical index and logical index don't match there. For header
            // and footer there is a match.
            return tr.getSectionRowIndex();
        };

    }

    private abstract class AbstractStaticRowContainer
            extends AbstractRowContainer {

        /** The height of the combined rows in the DOM. Never negative. */
        private double heightOfSection = 0;

        public AbstractStaticRowContainer(
                final TableSectionElement headElement) {
            super(headElement);
        }

        @Override
        public int getDomRowCount() {
            return root.getChildCount();
        }

        @Override
        protected void paintRemoveRows(final int index,
                final int numberOfRows) {
            for (int i = index; i < index + numberOfRows; i++) {
                final TableRowElement tr = root.getRows().getItem(index);
                paintRemoveRow(tr, index);
            }
            recalculateSectionHeight();
        }

        @Override
        protected TableRowElement getTrByVisualIndex(final int index)
                throws IndexOutOfBoundsException {
            if (index >= 0 && index < root.getChildCount()) {
                return root.getRows().getItem(index);
            } else {
                throw new IndexOutOfBoundsException(
                        "No such visual index: " + index);
            }
        }

        @Override
        public void insertRows(int index, int numberOfRows) {
            super.insertRows(index, numberOfRows);
            recalculateElementSizes();
            applyHeightByRows();
        }

        @Override
        public void removeRows(int index, int numberOfRows) {

            /*
             * While the rows in a static section are removed, the scrollbar is
             * temporarily shrunk and then re-expanded. This leads to the fact
             * that the scroll position is scooted up a bit. This means that we
             * need to reset the position here.
             *
             * If Escalator, at some point, gets a JIT evaluation functionality,
             * this re-setting is a strong candidate for removal.
             */
            double oldScrollPos = verticalScrollbar.getScrollPos();

            super.removeRows(index, numberOfRows);
            recalculateElementSizes();
            applyHeightByRows();

            verticalScrollbar.setScrollPos(oldScrollPos);
        }

        @Override
        protected void reapplyDefaultRowHeights() {
            if (root.getChildCount() == 0) {
                return;
            }

            Profiler.enter(
                    "Escalator.AbstractStaticRowContainer.reapplyDefaultRowHeights");

            Element tr = root.getRows().getItem(0);
            while (tr != null) {
                reapplyRowHeight(TableRowElement.as(tr), getDefaultRowHeight());
                tr = tr.getNextSiblingElement();
            }

            /*
             * Because all rows are immediately displayed in the static row
             * containers, the section's overall height has most probably
             * changed.
             */
            recalculateSectionHeight();

            Profiler.leave(
                    "Escalator.AbstractStaticRowContainer.reapplyDefaultRowHeights");
        }

        @Override
        protected void recalculateSectionHeight() {
            Profiler.enter(
                    "Escalator.AbstractStaticRowContainer.recalculateSectionHeight");

            double newHeight = calculateTotalRowHeight();
            if (newHeight != heightOfSection) {
                heightOfSection = newHeight;
                sectionHeightCalculated();

                /*
                 * We need to update the scrollbar dimension at this point. If
                 * we are scrolled too far down and the static section shrinks,
                 * the body will try to render rows that don't exist during
                 * body.verifyEscalatorCount. This is because the logical row
                 * indices are calculated from the scrollbar position.
                 */
                verticalScrollbar.setOffsetSize(
                        heightOfEscalator - header.getHeightOfSection()
                                - footer.getHeightOfSection());

                body.verifyEscalatorCount();
                body.spacerContainer.updateSpacerDecosVisibility();
            }

            Profiler.leave(
                    "Escalator.AbstractStaticRowContainer.recalculateSectionHeight");
        }

        /**
         * Informs the row container that the height of its respective table
         * section has changed.
         * <p>
         * These calculations might affect some layouting logic, such as the
         * body is being offset by the footer, the footer needs to be readjusted
         * according to its height, and so on.
         * <p>
         * A table section is either header, body or footer.
         */
        protected abstract void sectionHeightCalculated();

        @Override
        protected void refreshCells(Range logicalRowRange, Range colRange) {
            assertArgumentsAreValidAndWithinRange(logicalRowRange.getStart(),
                    logicalRowRange.length());

            if (!isAttached()) {
                return;
            }

            Profiler.enter("Escalator.AbstractStaticRowContainer.refreshCells");

            if (hasColumnAndRowData()) {
                for (int row = logicalRowRange.getStart(); row < logicalRowRange
                        .getEnd(); row++) {
                    final TableRowElement tr = getTrByVisualIndex(row);
                    refreshRow(tr, row, colRange);
                }
            }

            Profiler.leave("Escalator.AbstractStaticRowContainer.refreshCells");
        }

        @Override
        protected void paintInsertRows(int visualIndex, int numberOfRows) {
            paintInsertStaticRows(visualIndex, numberOfRows);
        }

        @Override
        protected boolean rowCanBeFrozen(TableRowElement tr) {
            assert root.isOrHasChild(
                    tr) : "Row does not belong to this table section";
            return true;
        }

        @Override
        protected double getHeightOfSection() {
            return Math.max(0, heightOfSection);
        }
    }

    private class HeaderRowContainer extends AbstractStaticRowContainer {
        public HeaderRowContainer(final TableSectionElement headElement) {
            super(headElement);
        }

        @Override
        protected void sectionHeightCalculated() {
            double heightOfSection = getHeightOfSection();
            bodyElem.getStyle().setMarginTop(heightOfSection, Unit.PX);
            spacerDecoContainer.getStyle().setMarginTop(heightOfSection,
                    Unit.PX);
            verticalScrollbar.getElement().getStyle().setTop(heightOfSection,
                    Unit.PX);
            headerDeco.getStyle().setHeight(heightOfSection, Unit.PX);
        }

        @Override
        protected String getCellElementTagName() {
            return "th";
        }

        @Override
        protected AriaGridRole getRowElementRole() {
            return AriaGridRole.ROWHEADER;
        }

        @Override
        protected AriaGridRole getCellElementRole() {
            return AriaGridRole.COLUMNHEADER;
        }

        @Override
        public void setStylePrimaryName(String primaryStyleName) {
            super.setStylePrimaryName(primaryStyleName);
            UIObject.setStylePrimaryName(root, primaryStyleName + "-header");
        }
    }

    private class FooterRowContainer extends AbstractStaticRowContainer {
        public FooterRowContainer(final TableSectionElement footElement) {
            super(footElement);
        }

        @Override
        public void setStylePrimaryName(String primaryStyleName) {
            super.setStylePrimaryName(primaryStyleName);
            UIObject.setStylePrimaryName(root, primaryStyleName + "-footer");
        }

        @Override
        protected String getCellElementTagName() {
            return "td";
        }

        @Override
        protected void sectionHeightCalculated() {
            double headerHeight = header.getHeightOfSection();
            double footerHeight = footer.getHeightOfSection();
            int vscrollHeight = (int) Math
                    .floor(heightOfEscalator - headerHeight - footerHeight);

            final boolean horizontalScrollbarNeeded = columnConfiguration
                    .calculateRowWidth() > widthOfEscalator;
            if (horizontalScrollbarNeeded) {
                vscrollHeight -= horizontalScrollbar.getScrollbarThickness();
            }

            footerDeco.getStyle().setHeight(footer.getHeightOfSection(),
                    Unit.PX);

            verticalScrollbar.setOffsetSize(vscrollHeight);
        }
    }

    private class BodyRowContainerImpl extends AbstractRowContainer
            implements BodyRowContainer {
        /*
         * TODO [[optimize]]: check whether a native JsArray might be faster
         * than LinkedList
         */
        /**
         * The order in which row elements are rendered visually in the browser,
         * with the help of CSS tricks. Usually has nothing to do with the DOM
         * order.
         *
         * @see #sortDomElements()
         */
        private final LinkedList<TableRowElement> visualRowOrder = new LinkedList<>();

        /**
         * The logical index of the topmost row.
         *
         * @deprecated Use the accessors {@link #setTopRowLogicalIndex(int)},
         *             {@link #updateTopRowLogicalIndex(int)} and
         *             {@link #getTopRowLogicalIndex()} instead
         */
        @Deprecated
        private int topRowLogicalIndex = 0;

        /**
         * A callback function to be executed after new rows are added to the
         * escalator.
         */
        private Consumer<List<TableRowElement>> newEscalatorRowCallback;

        /**
         * Set the logical index of the first dom row in visual order.
         * <p>
         * NOTE: this is not necessarily the first dom row in the dom tree, just
         * the one positioned to the top with CSS. See maintenance notes at the
         * top of this class for further information.
         *
         * @param topRowLogicalIndex
         *            logical index of the first dom row in visual order, might
         *            not match the dom tree order
         */
        private void setTopRowLogicalIndex(int topRowLogicalIndex) {
            if (LogConfiguration.loggingIsEnabled(Level.INFO)) {
                Logger.getLogger("Escalator.BodyRowContainer")
                        .fine("topRowLogicalIndex: " + this.topRowLogicalIndex
                                + " -> " + topRowLogicalIndex);
            }
            assert topRowLogicalIndex >= 0 : "topRowLogicalIndex became negative (top left cell contents: "
                    + visualRowOrder.getFirst().getCells().getItem(0)
                            .getInnerText()
                    + ") ";
            /*
             * if there's a smart way of evaluating and asserting the max index,
             * this would be a nice place to put it. I haven't found out an
             * effective and generic solution.
             */

            this.topRowLogicalIndex = topRowLogicalIndex;
        }

        /**
         * Returns the logical index of the first dom row in visual order. This
         * also gives the offset between the logical and visual indexes.
         * <p>
         * NOTE: this is not necessarily the first dom row in the dom tree, just
         * the one positioned to the top with CSS. See maintenance notes at the
         * top of this class for further information.
         *
         * @return logical index of the first dom row in visual order, might not
         *         match the dom tree order
         */
        public int getTopRowLogicalIndex() {
            return topRowLogicalIndex;
        }

        /**
         * Updates the logical index of the first dom row in visual order with
         * the given difference.
         * <p>
         * NOTE: this is not necessarily the first dom row in the dom tree, just
         * the one positioned to the top with CSS. See maintenance notes at the
         * top of this class for further information.
         *
         * @param diff
         *            the amount to increase or decrease the logical index of
         *            the first dom row in visual order
         */
        private void updateTopRowLogicalIndex(int diff) {
            setTopRowLogicalIndex(topRowLogicalIndex + diff);
        }

        private class DeferredDomSorter {
            private static final int SORT_DELAY_MILLIS = 50;

            // as it happens, 3 frames = 50ms @ 60fps.
            private static final int REQUIRED_FRAMES_PASSED = 3;

            private final AnimationCallback frameCounter = new AnimationCallback() {
                @Override
                public void execute(double timestamp) {
                    framesPassed++;
                    boolean domWasSorted = sortIfConditionsMet();
                    if (!domWasSorted) {
                        animationHandle = AnimationScheduler.get()
                                .requestAnimationFrame(this);
                    } else {
                        waiting = false;
                    }
                }
            };

            private int framesPassed;
            private double startTime;
            private AnimationHandle animationHandle;

            /** <code>true</code> if a sort is scheduled */
            public boolean waiting = false;

            public void reschedule() {
                waiting = true;
                resetConditions();
                animationHandle = AnimationScheduler.get()
                        .requestAnimationFrame(frameCounter);
            }

            private boolean sortIfConditionsMet() {
                boolean enoughFramesHavePassed = framesPassed >= REQUIRED_FRAMES_PASSED;
                boolean enoughTimeHasPassed = (Duration.currentTimeMillis()
                        - startTime) >= SORT_DELAY_MILLIS;
                boolean notTouchActivity = !scroller.touchHandlerBundle.touching;
                boolean conditionsMet = enoughFramesHavePassed
                        && enoughTimeHasPassed && notTouchActivity;

                if (conditionsMet) {
                    resetConditions();
                    sortDomElements();
                }

                return conditionsMet;
            }

            private void resetConditions() {
                if (animationHandle != null) {
                    animationHandle.cancel();
                    animationHandle = null;
                }
                startTime = Duration.currentTimeMillis();
                framesPassed = 0;
            }
        }

        private DeferredDomSorter domSorter = new DeferredDomSorter();

        private final SpacerContainer spacerContainer = new SpacerContainer();

        private boolean insertingOrRemoving = false;

        public BodyRowContainerImpl(final TableSectionElement bodyElement) {
            super(bodyElement);
        }

        @Override
        public void setStylePrimaryName(String primaryStyleName) {
            super.setStylePrimaryName(primaryStyleName);
            UIObject.setStylePrimaryName(root, primaryStyleName + "-body");
            spacerContainer.setStylePrimaryName(primaryStyleName);
        }

        public void updateEscalatorRowsOnScroll() {
            if (visualRowOrder.isEmpty()) {
                return;
            }

            boolean rowsWereMoved = false;

            final double topElementPosition;
            final double nextRowBottomOffset;
            SpacerContainer.SpacerImpl topSpacer = spacerContainer
                    .getSpacer(getTopRowLogicalIndex() - 1);

            if (topSpacer != null) {
                topElementPosition = topSpacer.getTop();
                nextRowBottomOffset = topSpacer.getHeight()
                        + getDefaultRowHeight();
            } else {
                topElementPosition = getRowTop(visualRowOrder.getFirst());
                nextRowBottomOffset = getDefaultRowHeight();
            }

            // TODO [[mpixscroll]]
            final double scrollTop = tBodyScrollTop;
            final double sectionHeight = getHeightOfSection();

            /*
             * Calculate how the visual range is situated in relation to the
             * viewport. Negative value means part of visual range is hidden
             * above or below the viewport, positive value means there is a gap
             * at the top or the bottom of the viewport, zero means exact match.
             * If there is a gap, some rows that are out of view may need to be
             * recycled from the opposite end.
             */
            final double viewportOffsetTop = topElementPosition - scrollTop;
            final double viewportOffsetBottom = scrollTop + sectionHeight
                    - getRowTop(
                            getTopRowLogicalIndex() + visualRowOrder.size());

            /*
             * You can only scroll far enough to leave a gap if visualRowOrder
             * contains a maximal amount of rows and there is at least one more
             * outside of the visual range. Consequently there can only be a gap
             * in one end of the viewport at a time.
             */
            if (viewportOffsetTop > 0 || (viewportOffsetTop == 0
                    && getTopRowLogicalIndex() > 0)) {
                /*
                 * Scrolling up. Either there's empty room on top, or there
                 * should be a buffer row for tab navigation on top, but there
                 * isn't.
                 */
                recycleRowsUpOnScroll(viewportOffsetTop);

                rowsWereMoved = true;
            } else if ((viewportOffsetBottom > 0
                    && (viewportOffsetTop + nextRowBottomOffset <= 0))
                    || (viewportOffsetBottom == 0 && (getTopRowLogicalIndex()
                            + visualRowOrder.size() < getRowCount() - 2))) {
                /*
                 * Scrolling down. Either there's empty room at the bottom and
                 * the viewport has been scrolled more than the topmost visual
                 * row, or there should be a buffer row at the bottom to ensure
                 * tab navigation works, but there isn't.
                 */
                recycleRowsDownOnScroll(topElementPosition, scrollTop);

                // Moving rows may have removed more spacers and created another
                // gap, this time the scroll position needs adjusting. The last
                // row within visual range should be just below the viewport as
                // a buffer for helping with tab navigation, unless it's the
                // last row altogether.
                int lastRowInVisualRange = getTopRowLogicalIndex()
                        + visualRowOrder.size() - 1;
                double expectedBottom = getRowTop(lastRowInVisualRange);
                if (lastRowInVisualRange == getRowCount() - 1) {
                    expectedBottom += getDefaultRowHeight() + spacerContainer
                            .getSpacerHeight(lastRowInVisualRange);
                }
                if (expectedBottom < scrollTop + sectionHeight) {
                    double expectedTop = Math.max(0,
                            expectedBottom - sectionHeight);
                    setBodyScrollPosition(tBodyScrollLeft, expectedTop);
                    setScrollTop(expectedTop);
                }

                rowsWereMoved = true;
            }

            if (rowsWereMoved) {
                fireRowVisibilityChangeEvent();

                // schedule updating of the physical indexes
                domSorter.reschedule();
            }
        }

        /**
         * Recycling rows up for {@link #updateEscalatorRowsOnScroll()}.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param viewportOffsetTop
         */
        private void recycleRowsUpOnScroll(double viewportOffsetTop) {
            /*
             * We can ignore spacers here, because we keep enough rows within
             * the visual range to fill the viewport completely whether or not
             * any spacers are shown. There is a small tradeoff of having some
             * rows rendered even if they are outside of the viewport, but this
             * simplifies the handling significantly (we can't know what height
             * any individual spacer has before it has been rendered, which
             * happens with a delay) and keeps the visual range size stable
             * while scrolling. Consequently, even if there are spacers within
             * the current visual range, repositioning this many rows won't
             * cause us to run out of rows at the bottom.
             *
             * The viewportOffsetTop is positive and we round up, and
             * visualRowOrder can't be empty since we are scrolling, so there is
             * always going to be at least one row to move. There should also be
             * one buffer row that actually falls outside of the viewport, in
             * order to ensure that tabulator navigation works if the rows have
             * components in them. The buffer row is only needed if filling the
             * gap doesn't bring us to the top row already.
             */
            int rowsToFillTheGap = (int) Math
                    .ceil(viewportOffsetTop / getDefaultRowHeight());
            // ensure we don't try to move more rows than are available
            // above
            rowsToFillTheGap = Math.min(rowsToFillTheGap,
                    getTopRowLogicalIndex());
            // add the buffer row if there is room for it
            if (rowsToFillTheGap < getTopRowLogicalIndex()) {
                ++rowsToFillTheGap;
            }
            // we may have scrolled up past all the rows and beyond, can
            // only recycle as many rows as we have
            int rowsToRecycle = Math.min(rowsToFillTheGap,
                    visualRowOrder.size());

            // select the rows to recycle from the end of the visual range
            int end = visualRowOrder.size();
            int start = end - rowsToRecycle;

            /*
             * Calculate the logical index for insertion point based on how many
             * rows would be needed to fill the gap. Because we are recycling
             * rows to the top the insertion index will also be the new top row
             * logical index.
             */
            int newTopRowLogicalIndex = getTopRowLogicalIndex()
                    - rowsToFillTheGap;

            // recycle the rows and move them to their new positions
            moveAndUpdateEscalatorRows(Range.between(start, end), 0,
                    newTopRowLogicalIndex);

            setTopRowLogicalIndex(newTopRowLogicalIndex);
        }

        /**
         * Recycling rows down for {@link #updateEscalatorRowsOnScroll()}.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param topElementPosition
         * @param scrollTop
         */
        private void recycleRowsDownOnScroll(double topElementPosition,
                double scrollTop) {
            /*
             * It's better to have any extra rows below than above, so move as
             * many of them as possible regardless of how many are needed to
             * fill the gap, as long as one buffer row remains at the top. It
             * should not be possible to scroll down enough to create a gap
             * without it being possible to recycle rows to fill the gap, so
             * viewport itself doesn't need adjusting no matter what.
             */

            // we already have the rows and spacers here and we don't want
            // to recycle rows that are going to stay visible, so the
            // spacers have to be taken into account
            double extraRowPxAbove = getRowHeightsSumBetweenPxExcludingSpacers(
                    topElementPosition, scrollTop);

            // how many rows fit within that extra space and can be
            // recycled, rounded towards zero to avoid moving any partially
            // visible rows
            int rowsToCoverTheExtra = (int) Math
                    .floor(extraRowPxAbove / getDefaultRowHeight());
            // leave one to ensure there is a buffer row to help with tab
            // navigation
            if (rowsToCoverTheExtra > 0) {
                --rowsToCoverTheExtra;
            }
            /*
             * Don't move more rows than there are to move, but also don't move
             * more rows than should exist at the bottom. However, it's not
             * possible to scroll down beyond available rows, so there is always
             * at least one row to recycle.
             */
            int rowsToRecycle = Math.min(
                    Math.min(rowsToCoverTheExtra, visualRowOrder.size()),
                    getRowCount() - getTopRowLogicalIndex()
                            - visualRowOrder.size());

            // are only some of the rows getting recycled instead of all
            // of them
            boolean partialMove = rowsToRecycle < visualRowOrder.size();

            // calculate the logical index where the rows should be moved
            int logicalTargetIndex;
            if (partialMove) {
                /*
                 * We scroll so little that we can just keep adding the rows
                 * immediately below the current escalator.
                 */
                logicalTargetIndex = getTopRowLogicalIndex()
                        + visualRowOrder.size();
            } else {
                /*
                 * Since all escalator rows are getting recycled all spacers are
                 * going to get removed and the calculations have to ignore the
                 * spacers again in order to figure out which rows are to be
                 * displayed. In practice we may end up scrolling further down
                 * than the scroll position indicated initially as the spacers
                 * that get removed give room for more rows than expected.
                 *
                 * We can rely on calculations here because there won't be any
                 * old rows left to end up mismatched with.
                 */
                logicalTargetIndex = (int) Math
                        .floor(scrollTop / getDefaultRowHeight());

                /*
                 * Make sure we don't try to move rows below the actual row
                 * count, even if some of the rows end up hidden at the top as a
                 * result. This won't leave us with any old rows in any case,
                 * because we already checked earlier that there is room to
                 * recycle all the rows. It's only a question of how the new
                 * visual range gets positioned in relation to the viewport.
                 */
                if (logicalTargetIndex
                        + visualRowOrder.size() > getRowCount()) {
                    logicalTargetIndex = getRowCount() - visualRowOrder.size();
                }
            }

            /*
             * Recycle the rows and move them to their new positions. Since we
             * are moving the viewport downwards, the visual target index is
             * always at the bottom and matches the length of the visual range.
             * Note: Due to how moveAndUpdateEscalatorRows works, this will work
             * out even if we move all the rows, and try to place them
             * "at the end".
             */
            moveAndUpdateEscalatorRows(Range.between(0, rowsToRecycle),
                    visualRowOrder.size(), logicalTargetIndex);

            // top row logical index needs to be updated differently
            // depending on which update strategy was used, since the rows
            // are being moved down
            if (partialMove) {
                // move down by the amount of recycled rows
                updateTopRowLogicalIndex(rowsToRecycle);
            } else {
                // the insertion index is the new top row logical index
                setTopRowLogicalIndex(logicalTargetIndex);
            }
        }

        /**
         * Calculates how much of the given range contains only rows with
         * spacers excluded.
         *
         * @param y1
         *            start position
         * @param y2
         *            end position
         * @return position difference excluding any space taken up by spacers
         */
        private double getRowHeightsSumBetweenPxExcludingSpacers(double y1,
                double y2) {
            assert y1 < y2 : "y1 must be smaller than y2";

            double viewportPx = y2 - y1;
            double spacerPx = spacerContainer.getSpacerHeightsSumBetweenPx(y1,
                    SpacerInclusionStrategy.PARTIAL, y2,
                    SpacerInclusionStrategy.PARTIAL);

            return viewportPx - spacerPx;
        }

        @Override
        public void insertRows(int index, int numberOfRows) {
            insertingOrRemoving = true;
            super.insertRows(index, numberOfRows);
            insertingOrRemoving = false;

            if (heightMode == HeightMode.UNDEFINED) {
                setHeightByRows(getRowCount());
            }
        }

        @Override
        public void removeRows(int index, int numberOfRows) {
            insertingOrRemoving = true;
            super.removeRows(index, numberOfRows);
            insertingOrRemoving = false;

            if (heightMode == HeightMode.UNDEFINED) {
                setHeightByRows(getRowCount());
            }
        }

        @Override
        protected void paintInsertRows(final int index,
                final int numberOfRows) {
            assert index >= 0
                    && index < getRowCount() : "Attempting to insert a row "
                            + "outside of the available range.";
            assert numberOfRows > 0 : "Attempting to insert a non-positive "
                    + "amount of rows, something must be wrong.";

            if (numberOfRows <= 0) {
                return;
            }
            /*
             * NOTE: this method handles and manipulates logical, visual, and
             * physical indexes a lot. If you don't remember what those mean and
             * how they relate to each other, see the top of this class for
             * Maintenance Notes.
             *
             * At the beginning of this method the logical index of the data
             * provider has already been updated to include the new rows, but
             * visual and physical indexes have not, nor has the spacer indexing
             * been updated, and the topRowLogicalIndex may be out of date as
             * well.
             */

            // top of visible area before any rows are actually added
            double scrollTop = getScrollTop();

            // logical index of the first row within the visual range before any
            // rows are actually added
            int oldTopRowLogicalIndex = getTopRowLogicalIndex();

            // length of the visual range before any rows are actually added
            int oldVisualRangeLength = visualRowOrder.size();

            /*
             * If there is room for more dom rows within the maximum visual
             * range, add them. Calling this method repositions all the rows and
             * spacers below the insertion point and updates the spacer indexes
             * accordingly.
             *
             * TODO: Details rows should be added and populated here, since they
             * have variable heights and affect the position calculations.
             * Currently that's left to be triggered at the end and with a
             * delay. If any new spacers exist, everything below them is going
             * to be repositioned again for every spacer addition.
             */
            final List<TableRowElement> addedRows = fillAndPopulateEscalatorRowsIfNeeded(
                    index - oldTopRowLogicalIndex, index, numberOfRows);

            // is the insertion point for new rows below visual range (viewport
            // is irrelevant)
            final boolean newRowsInsertedBelowVisualRange = index >= oldVisualRangeLength
                    + oldTopRowLogicalIndex;

            // is the insertion point for new rows above initial visual range
            final boolean newRowsInsertedAboveVisualRange = index <= oldTopRowLogicalIndex;

            // is the insertion point for new rows above viewport
            final boolean newRowsInsertedAboveCurrentViewport = getRowTop(
                    index) < scrollTop;

            if (newRowsInsertedBelowVisualRange) {
                /*
                 * There is no change to scroll position, and all other changes
                 * to positioning and indexing are out of visual range or
                 * already done (if addedRows is not empty).
                 */
            } else if (newRowsInsertedAboveVisualRange && addedRows.isEmpty()
                    && newRowsInsertedAboveCurrentViewport) {
                /*
                 * This section can only be reached if the insertion point is
                 * above the visual range, the visual range already covers a
                 * maximal amount of rows, and we are scrolled down enough that
                 * the top row is either partially or completely hidden. The
                 * last two points happen by default if the first row of the
                 * visual range has any other logical index than zero. Any other
                 * use cases involving the top row within the visual range need
                 * different handling.
                 */
                paintInsertRowsAboveViewPort(index, numberOfRows,
                        oldTopRowLogicalIndex);
            } else if (newRowsInsertedAboveCurrentViewport) {
                /*
                 * Rows were inserted within the visual range but above the
                 * viewport. This includes the use case where the insertion
                 * point is just above the visual range and we are scrolled down
                 * a bit but the visual range doesn't have maximal amount of
                 * rows yet (can only happen with spacers in play), so more rows
                 * were added to the visual range but no rows need to be
                 * recycled.
                 */
                paintInsertRowsWithinVisualRangeButAboveViewport(index,
                        numberOfRows, oldTopRowLogicalIndex, addedRows.size());
            } else {
                /*
                 * Rows added within visual range and either within or below the
                 * viewport. Recycled rows come from the END of the visual
                 * range.
                 */
                paintInsertRowsWithinVisualRangeAndWithinOrBelowViewport(index,
                        numberOfRows, oldTopRowLogicalIndex, addedRows.size());
            }

            /*
             * Calling insertRows will always change the number of rows - update
             * the scrollbar sizes. This calculation isn't affected by actual
             * dom rows amount or contents except for spacer heights. Spacers
             * that don't fit the visual range are considered to have no height
             * and might affect scrollbar calculations aversely, but that can't
             * be avoided since they have unknown and variable heights.
             */
            scroller.recalculateScrollbarsForVirtualViewport();
        }

        /**
         * Row insertion handling for {@link #paintInsertRows(int, int)} when
         * the range will be inserted above the visual range.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param index
         * @param numberOfRows
         * @param oldTopRowLogicalIndex
         */
        private void paintInsertRowsAboveViewPort(int index, int numberOfRows,
                int oldTopRowLogicalIndex) {
            /*
             * Because there is no need to expand the visual range, no row or
             * spacer contents get updated. All rows, spacers, and scroll
             * position simply need to be shifted down accordingly and the
             * spacer indexes need updating.
             */
            spacerContainer.updateSpacerIndexesForRowAndAfter(index,
                    oldTopRowLogicalIndex + visualRowOrder.size(),
                    numberOfRows);

            // height of a single row
            double defaultRowHeight = getDefaultRowHeight();

            // height of new rows, out of visual range so spacers assumed to
            // have no height
            double newRowsHeight = numberOfRows * defaultRowHeight;

            // update the positions
            moveViewportAndContent(index, newRowsHeight, newRowsHeight,
                    newRowsHeight);

            // top row logical index moves down by the number of new rows
            updateTopRowLogicalIndex(numberOfRows);
        }

        /**
         * Row insertion handling for {@link #paintInsertRows(int, int)} when
         * the range will be inserted within the visual range above the
         * viewport.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param index
         * @param numberOfRows
         * @param oldTopRowLogicalIndex
         * @param addedRowCount
         */
        private void paintInsertRowsWithinVisualRangeButAboveViewport(int index,
                int numberOfRows, int oldTopRowLogicalIndex,
                int addedRowCount) {
            /*
             * Unless we are scrolled all the way to the top the visual range is
             * always out of view because we need a buffer row for tabulator
             * navigation. Depending on the scroll position and spacers there
             * might even be several rendered rows above the viewport,
             * especially when we are scrolled all the way to the bottom.
             *
             * Even though the new rows will be initially out of view they still
             * need to be correctly populated and positioned. Their contents
             * won't be refreshed if they become visible later on (e.g. when a
             * spacer gets hidden, which causes more rows to fit within the
             * viewport) because they are expected to be already up to date.
             *
             * Note that it's not possible to insert content so that it's
             * partially visible at the top. A partially visible row at top will
             * still be the exact same partially visible row after the
             * insertion, no matter which side of that row the new content gets
             * inserted to. This section handles the use case where the new
             * content is inserted above the partially visible row.
             *
             * Because the insertion point is out of view above the viewport,
             * the only thing that should change for the end user visually is
             * the scroll handle, which gets a new position and possibly turns a
             * bit smaller if a lot of rows got inserted.
             *
             * From a technical point of view this also means that any rows that
             * might need to get recycled should be taken from the BEGINNING of
             * the visual range, above the insertion point. There might still be
             * some "extra" rows below the viewport as well, but those should be
             * left alone. They are going to be needed where they are if any
             * spacers get closed or reduced in size.
             *
             * On a practical level we need to tweak the virtual viewport --
             * scroll handle positions, row and spacer positions, and ensure the
             * scroll area height is calculated correctly. Viewport should
             * remain in a fixed position in relation to the existing rows and
             * display no new rows. If any rows get recycled and have spacers
             * either before or after the update the height of those spacers
             * affects the position calculations.
             *
             * Insertion point can be anywhere from just before the previous
             * first row of the visual range to just before the first actually
             * visible row. The insertion shifts down the content below
             * insertion point, which excludes any dom rows that remain above
             * the insertion point after recycling is finished. After the rows
             * below insertion point have been moved the viewport needs to be
             * shifted down a similar amount to regain its old relative position
             * again.
             *
             * The visual range only ever contains at most as many rows as would
             * fit within the viewport without any spacers with one extra row on
             * both at the top and at the bottom as buffer rows, so the amount
             * of rows that needs to be checked is always reasonably limited.
             */
            // insertion index within the visual range
            int visualTargetIndex = index - oldTopRowLogicalIndex;

            // how many dom rows before insertion point versus how many new
            // rows didn't get their own dom rows -- smaller amount
            // determines how many rows can and need to be recycled
            int rowsToUpdate = Math.min(visualTargetIndex,
                    numberOfRows - addedRowCount);

            // height of a single row
            double defaultRowHeight = getDefaultRowHeight();

            boolean rowVisibilityChanged = false;
            if (rowsToUpdate > 0) {
                // recycle the rows and update the positions, adjust
                // logical index for inserted rows that won't fit within
                // visual range
                int logicalIndex = index + numberOfRows - rowsToUpdate;
                if (visualTargetIndex > 0) {
                    // move after any added dom rows
                    moveAndUpdateEscalatorRows(Range.between(0, rowsToUpdate),
                            visualTargetIndex + addedRowCount, logicalIndex);
                } else {
                    // move before any added dom rows
                    moveAndUpdateEscalatorRows(Range.between(0, rowsToUpdate),
                            visualTargetIndex, logicalIndex);
                }

                // adjust viewport down to maintain the initial position
                double newRowsHeight = numberOfRows * defaultRowHeight;
                double newSpacerHeights = spacerContainer
                        .getSpacerHeightsSumUntilIndex(
                                logicalIndex + rowsToUpdate)
                        - spacerContainer.getSpacerHeightsSumUntilIndex(index);

                /*
                 * FIXME: spacers haven't been added yet and they can cause
                 * escalator contents to shift after the fact in a way that
                 * can't be countered for here.
                 *
                 * FIXME: verticalScrollbar internal state causes this update to
                 * fail partially and the next attempt at scrolling causes
                 * things to jump.
                 *
                 * Couldn't find a quick fix to either problem and this use case
                 * is somewhat marginal so left them here for now.
                 */
                moveViewportAndContent(null, 0, 0,
                        newSpacerHeights + newRowsHeight);

                rowVisibilityChanged = true;
            } else {
                // no rows to recycle but update the spacer indexes
                spacerContainer.updateSpacerIndexesForRowAndAfter(index,
                        index + numberOfRows - addedRowCount,
                        numberOfRows - addedRowCount);

                double newRowsHeight = numberOfRows * defaultRowHeight;
                if (addedRowCount > 0) {
                    // update the viewport, rows and spacers were
                    // repositioned already by the method for adding dom
                    // rows
                    moveViewportAndContent(null, 0, 0, newRowsHeight);

                    rowVisibilityChanged = true;
                } else {
                    // all changes are actually above the viewport after
                    // all, update all positions
                    moveViewportAndContent(index, newRowsHeight, newRowsHeight,
                            newRowsHeight);
                }
            }

            if (numberOfRows > addedRowCount) {
                /*
                 * If there are more new rows than how many new dom rows got
                 * added, the top row logical index necessarily gets shifted
                 * down by that difference because recycling doesn't replace any
                 * logical rows, just shifts them off the visual range, and the
                 * inserted rows that don't fit to the visual range also push
                 * the other rows down. If every new row got new dom rows as
                 * well the top row logical index doesn't change, because the
                 * insertion point was within the visual range.
                 */
                updateTopRowLogicalIndex(numberOfRows - addedRowCount);
            }

            if (rowVisibilityChanged) {
                fireRowVisibilityChangeEvent();
            }
            if (rowsToUpdate > 0) {
                // update the physical index
                sortDomElements();
            }
        }

        /**
         * Row insertion handling for {@link #paintInsertRows(int, int)} when
         * the range will be inserted within the visual range either within or
         * below the viewport.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param index
         * @param numberOfRows
         * @param oldTopRowLogicalIndex
         * @param addedRowCount
         */
        private void paintInsertRowsWithinVisualRangeAndWithinOrBelowViewport(
                int index, int numberOfRows, int oldTopRowLogicalIndex,
                int addedRowCount) {
            // insertion index within the visual range
            int visualIndex = index - oldTopRowLogicalIndex;

            // how many dom rows after insertion point versus how many new
            // rows to add -- smaller amount determines how many rows can or
            // need to be recycled, excluding the rows that already got new
            // dom rows
            int rowsToUpdate = Math.max(
                    Math.min(visualRowOrder.size() - visualIndex, numberOfRows)
                            - addedRowCount,
                    0);

            if (rowsToUpdate > 0) {
                moveAndUpdateEscalatorRows(
                        Range.between(visualRowOrder.size() - rowsToUpdate,
                                visualRowOrder.size()),
                        visualIndex + addedRowCount, index + addedRowCount);

                fireRowVisibilityChangeEvent();

                // update the physical index
                sortDomElements();
            }
        }

        /**
         * Move escalator rows around, and make sure everything gets
         * appropriately repositioned and repainted. In the case of insertion or
         * removal, following spacer indexes get updated as well.
         *
         * @param visualSourceRange
         *            the range of rows to move to a new place
         * @param visualTargetIndex
         *            the visual index where the rows will be placed to
         * @param logicalTargetIndex
         *            the logical index to be assigned to the first moved row
         */
        private void moveAndUpdateEscalatorRows(final Range visualSourceRange,
                final int visualTargetIndex, final int logicalTargetIndex)
                throws IllegalArgumentException {

            if (visualSourceRange.isEmpty()) {
                return;
            }
            int sourceRangeLength = visualSourceRange.length();
            int domRowCount = getDomRowCount();
            int rowCount = getRowCount();

            assert visualSourceRange.getStart() >= 0 : "Visual source start "
                    + "must be 0 or greater (was "
                    + visualSourceRange.getStart() + ")";

            assert logicalTargetIndex >= 0 : "Logical target must be 0 or "
                    + "greater (was " + logicalTargetIndex + ")";

            assert visualTargetIndex >= 0 : "Visual target must be 0 or greater (was "
                    + visualTargetIndex + ")";

            assert visualTargetIndex <= domRowCount : "Visual target "
                    + "must not be greater than the number of escalator rows (was "
                    + visualTargetIndex + ", escalator rows " + domRowCount
                    + ")";

            assert logicalTargetIndex
                    + sourceRangeLength <= rowCount : "Logical "
                            + "target leads to rows outside of the data range ("
                            + Range.withLength(logicalTargetIndex,
                                    sourceRangeLength)
                            + " goes beyond " + Range.withLength(0, rowCount)
                            + ")";

            /*
             * Since we move a range into another range, the indices might move
             * about. Having 10 rows, if we move 0..1 to index 10 (to the end of
             * the collection), the target range will end up being 8..9, instead
             * of 10..11.
             *
             * This applies only if we move elements forward in the collection,
             * not backward.
             */
            final int adjustedVisualTargetIndex;
            if (visualSourceRange.getStart() < visualTargetIndex) {
                adjustedVisualTargetIndex = visualTargetIndex
                        - sourceRangeLength;
            } else {
                adjustedVisualTargetIndex = visualTargetIndex;
            }

            int oldTopRowLogicalIndex = getTopRowLogicalIndex();

            // first moved row's logical index before move
            int oldSourceRangeLogicalStart = oldTopRowLogicalIndex
                    + visualSourceRange.getStart();

            // new top row logical index
            int newTopRowLogicalIndex = logicalTargetIndex
                    - adjustedVisualTargetIndex;

            // variables for update types that require special handling
            boolean recycledToTop = logicalTargetIndex < oldTopRowLogicalIndex;
            boolean recycledFromTop = visualSourceRange.getStart() == 0;
            boolean scrollingUp = recycledToTop
                    && visualSourceRange.getEnd() == visualRowOrder.size();
            boolean scrollingDown = recycledFromTop
                    && logicalTargetIndex >= oldTopRowLogicalIndex
                            + visualRowOrder.size();

            if (visualSourceRange.getStart() != adjustedVisualTargetIndex) {
                /*
                 * Reorder the rows to their correct places within
                 * visualRowOrder (unless rows are moved back to their original
                 * places)
                 */

                /*
                 * TODO [[optimize]]: move whichever set is smaller: the ones
                 * explicitly moved, or the others. So, with 10 escalator rows,
                 * if we are asked to move idx[0..8] to the end of the list,
                 * it's faster to just move idx[9] to the beginning.
                 */

                final List<TableRowElement> removedRows = new ArrayList<>(
                        sourceRangeLength);
                for (int i = 0; i < sourceRangeLength; i++) {
                    final TableRowElement tr = visualRowOrder
                            .remove(visualSourceRange.getStart());
                    removedRows.add(tr);
                }
                visualRowOrder.addAll(adjustedVisualTargetIndex, removedRows);
            }

            // refresh contents of rows to be recycled, returns the combined
            // height of the spacers that got removed from visual range
            double spacerHeightsOfRecycledRowsBefore = refreshRecycledRowContents(
                    logicalTargetIndex, adjustedVisualTargetIndex,
                    sourceRangeLength, oldSourceRangeLogicalStart);

            boolean movedDown = adjustedVisualTargetIndex != visualTargetIndex;
            boolean recycledToOrFromTop = recycledToTop || recycledFromTop;

            // update spacer indexes unless we are scrolling -- with scrolling
            // the remaining spacers are where they belong, the recycled ones
            // were already removed, and new ones will be added with delay
            if (!(scrollingUp || scrollingDown)) {
                if (recycledToOrFromTop) {
                    updateSpacerIndexesForMoveWhenRecycledToOrFromTop(
                            oldSourceRangeLogicalStart, sourceRangeLength,
                            oldTopRowLogicalIndex, newTopRowLogicalIndex,
                            recycledFromTop);
                } else {
                    updateSpacerIndexesForMoveWhenNotRecycledToOrFromTop(
                            logicalTargetIndex, oldSourceRangeLogicalStart,
                            sourceRangeLength, movedDown);
                }
            }

            // Would be useful if new spacer heights could be determined
            // here already but their contents are populated with delay.
            // If the heights ever become available immediately, the
            // handling that follows needs to be updated to take the new
            // spacer heights into account.

            repositionMovedRows(adjustedVisualTargetIndex, sourceRangeLength,
                    newTopRowLogicalIndex);

            // variables for reducing the amount of necessary parameters
            boolean scrollingDownAndNoSpacersRemoved = scrollingDown
                    && spacerHeightsOfRecycledRowsBefore <= 0d;
            boolean spacerHeightsChanged = spacerHeightsOfRecycledRowsBefore > 0d;

            repositionRowsShiftedByTheMove(visualSourceRange, visualTargetIndex,
                    adjustedVisualTargetIndex, newTopRowLogicalIndex,
                    scrollingDownAndNoSpacersRemoved, scrollingUp,
                    recycledToTop);

            repositionRowsBelowMovedAndShiftedIfNeeded(visualSourceRange,
                    visualTargetIndex, adjustedVisualTargetIndex,
                    newTopRowLogicalIndex, (scrollingUp || scrollingDown),
                    recycledToOrFromTop, spacerHeightsChanged);
        }

        /**
         * Refresh the contents of the affected rows for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)}
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param logicalTargetIndex
         * @param adjustedVisualTargetIndex
         * @param sourceRangeLength
         * @param spacerHeightsBeforeMoveTotal
         * @param oldSourceRangeLogicalStart
         * @return the combined height of any removed spacers
         */
        private double refreshRecycledRowContents(int logicalTargetIndex,
                int adjustedVisualTargetIndex, int sourceRangeLength,
                int oldSourceRangeLogicalStart) {
            final ListIterator<TableRowElement> iter = visualRowOrder
                    .listIterator(adjustedVisualTargetIndex);
            double removedSpacerHeights = 0d;
            for (int i = 0; i < sourceRangeLength; ++i) {
                final TableRowElement tr = iter.next();
                int logicalIndex = logicalTargetIndex + i;

                // clear old spacer
                SpacerContainer.SpacerImpl spacer = spacerContainer
                        .getSpacer(oldSourceRangeLogicalStart + i);
                if (spacer != null) {
                    double spacerHeight = spacer.getHeight();
                    removedSpacerHeights += spacerHeight;
                    spacerContainer
                            .removeSpacer(oldSourceRangeLogicalStart + i);
                }

                refreshRow(tr, logicalIndex);
            }
            return removedSpacerHeights;
        }

        /**
         * Update the spacer indexes to correspond with logical indexes for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)} when the move
         * recycles rows to or from top
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param oldSourceRangeLogicalStart
         * @param sourceRangeLength
         * @param oldTopRowLogicalIndex
         * @param newTopRowLogicalIndex
         * @param recycledFromTop
         */
        private void updateSpacerIndexesForMoveWhenRecycledToOrFromTop(
                int oldSourceRangeLogicalStart, int sourceRangeLength,
                int oldTopRowLogicalIndex, int newTopRowLogicalIndex,
                boolean recycledFromTop) {
            if (recycledFromTop) {
                // first rows are getting recycled thanks to insertion or
                // removal, all the indexes below need to be updated
                // accordingly
                int indexesToShift;
                if (newTopRowLogicalIndex != oldTopRowLogicalIndex) {
                    indexesToShift = newTopRowLogicalIndex
                            - oldTopRowLogicalIndex;
                } else {
                    indexesToShift = -sourceRangeLength;
                }
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        oldSourceRangeLogicalStart + sourceRangeLength,
                        oldTopRowLogicalIndex + visualRowOrder.size(),
                        indexesToShift);
            } else {
                // rows recycled to the top, move the remaining spacer
                // indexes up
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        oldSourceRangeLogicalStart + sourceRangeLength,
                        getRowCount() + sourceRangeLength, -sourceRangeLength);
            }
        }

        /**
         * Update the spacer indexes to correspond with logical indexes for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)} when the move
         * does not recycle rows to or from top
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param logicalTargetIndex
         * @param oldSourceRangeLogicalStart
         * @param sourceRangeLength
         * @param movedDown
         */
        private void updateSpacerIndexesForMoveWhenNotRecycledToOrFromTop(
                int logicalTargetIndex, int oldSourceRangeLogicalStart,
                int sourceRangeLength, boolean movedDown) {
            if (movedDown) {
                // move the shifted spacer indexes up to fill the freed
                // space
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        oldSourceRangeLogicalStart + sourceRangeLength,
                        logicalTargetIndex + sourceRangeLength,
                        -sourceRangeLength);
            } else {
                // move the shifted spacer indexes down to fill the freed
                // space
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        logicalTargetIndex, oldSourceRangeLogicalStart,
                        sourceRangeLength);
            }
        }

        /**
         * Reposition the rows that were moved for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)}
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param adjustedVisualTargetIndex
         * @param sourceRangeLength
         * @param newTopRowLogicalIndex
         */
        private void repositionMovedRows(int adjustedVisualTargetIndex,
                int sourceRangeLength, int newTopRowLogicalIndex) {
            int start = adjustedVisualTargetIndex;
            updateRowPositions(newTopRowLogicalIndex + start, start,
                    sourceRangeLength);
        }

        /**
         * Reposition the rows that were shifted by the move for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)}
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param visualSourceRange
         * @param visualTargetIndex
         * @param adjustedVisualTargetIndex
         * @param newTopRowLogicalIndex
         * @param scrollingDownAndNoSpacersRemoved
         * @param scrollingUp
         * @param recycledToTop
         */
        private void repositionRowsShiftedByTheMove(Range visualSourceRange,
                int visualTargetIndex, int adjustedVisualTargetIndex,
                int newTopRowLogicalIndex,
                boolean scrollingDownAndNoSpacersRemoved, boolean scrollingUp,
                boolean recycledToTop) {
            if (visualSourceRange.length() == visualRowOrder.size()) {
                // all rows got updated and were repositioned already
                return;
            }
            if (scrollingDownAndNoSpacersRemoved || scrollingUp) {
                // scrolling, no spacers got removed from or added above any
                // remaining rows so everything is where it belongs already
                // (there is no check for added spacers because adding happens
                // with delay, whether any spacers are coming or not they don't
                // exist yet and thus can't be taken into account here)
                return;
            }

            if (adjustedVisualTargetIndex != visualTargetIndex) {
                // rows moved down, shifted rows need to be moved up

                int start = visualSourceRange.getStart();
                updateRowPositions(newTopRowLogicalIndex + start, start,
                        adjustedVisualTargetIndex - start);
            } else {
                // rows moved up, shifted rows need to be repositioned
                // unless it's just a recycling and no spacer heights
                // above got updated

                if (recycledToTop) {
                    // rows below the shifted ones need to be moved up (which is
                    // done in the next helper method) but the shifted rows
                    // themselves are already where they belong
                    // (this should only be done if no spacers were added, but
                    // we can't know that yet so we'll have to adjust for them
                    // afterwards if any do appear)
                    return;
                }

                int start = adjustedVisualTargetIndex
                        + visualSourceRange.length();
                updateRowPositions(newTopRowLogicalIndex + start, start,
                        visualSourceRange.getEnd() - start);
            }
        }

        /**
         * If necessary, reposition the rows that are below those rows that got
         * moved or shifted for
         * {@link #moveAndUpdateEscalatorRows(Range, int, int)}
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param visualSourceRange
         * @param visualTargetIndex
         * @param adjustedVisualTargetIndex
         * @param newTopRowLogicalIndex
         * @param scrolling
         * @param recycledToOrFromTop
         * @param spacerHeightsChanged
         */
        private void repositionRowsBelowMovedAndShiftedIfNeeded(
                Range visualSourceRange, int visualTargetIndex,
                int adjustedVisualTargetIndex, int newTopRowLogicalIndex,
                boolean scrolling, boolean recycledToOrFromTop,
                boolean spacerHeightsChanged) {
            /*
             * There is no need to check if any rows preceding the source and
             * target range need their positions adjusted, but rows below both
             * may very well need it if spacer heights changed or rows got
             * inserted or removed instead of just moved around.
             *
             * When scrolling to either direction all the rows already got
             * processed by earlier stages, there are no unprocessed rows left
             * either above or below.
             */
            if (!scrolling && (recycledToOrFromTop || spacerHeightsChanged)) {

                int firstBelow;
                if (adjustedVisualTargetIndex != visualTargetIndex) {
                    // rows moved down
                    firstBelow = adjustedVisualTargetIndex
                            + visualSourceRange.length();
                } else {
                    // rows moved up
                    firstBelow = visualSourceRange.getEnd();
                }
                updateRowPositions(newTopRowLogicalIndex + firstBelow,
                        firstBelow, visualRowOrder.size() - firstBelow);
            }
        }

        @Override
        public void updateRowPositions(int index, int numberOfRows) {
            Range visibleRowRange = getVisibleRowRange();
            Range rangeToUpdate = Range.withLength(index, numberOfRows);
            Range intersectingRange = visibleRowRange
                    .partitionWith(rangeToUpdate)[1];

            if (intersectingRange.isEmpty()) {
                // no overlap with the visual range, ignore the positioning
                return;
            }

            int adjustedIndex = intersectingRange.getStart();
            int adjustedVisualIndex = adjustedIndex - getTopRowLogicalIndex();

            updateRowPositions(adjustedIndex, adjustedVisualIndex,
                    intersectingRange.length());

            // make sure there is no unnecessary gap
            adjustScrollPositionIfNeeded();

            scroller.recalculateScrollbarsForVirtualViewport();
        }

        /**
         * Re-calculates and updates the positions of rows and spacers within
         * the given range. Doesn't touch the scroll positions.
         *
         * @param logicalIndex
         *            logical index of the first row to reposition
         * @param visualIndex
         *            visual index of the first row to reposition
         * @param numberOfRows
         *            the number of rows to reposition
         */
        private void updateRowPositions(int logicalIndex, int visualIndex,
                int numberOfRows) {
            double newRowTop = getRowTop(logicalIndex);
            for (int i = 0; i < numberOfRows; ++i) {
                TableRowElement tr = visualRowOrder.get(visualIndex + i);
                setRowPosition(tr, 0, newRowTop);
                newRowTop += getDefaultRowHeight();

                SpacerContainer.SpacerImpl spacer = spacerContainer
                        .getSpacer(logicalIndex + i);
                if (spacer != null) {
                    spacer.setPosition(0, newRowTop);
                    newRowTop += spacer.getHeight();
                }
            }
        }

        /**
         * Checks whether there is an unexpected gap below the visible rows and
         * adjusts the viewport if necessary.
         */
        private void adjustScrollPositionIfNeeded() {
            double scrollTop = getScrollTop();
            int firstBelowVisualRange = getTopRowLogicalIndex()
                    + visualRowOrder.size();
            double gapBelow = scrollTop + getHeightOfSection()
                    - getRowTop(firstBelowVisualRange);
            boolean bufferRowNeeded = gapBelow == 0
                    && firstBelowVisualRange < getRowCount();
            if (scrollTop > 0 && (gapBelow > 0 || bufferRowNeeded)) {
                /*
                 * This situation can be reached e.g. by removing a spacer.
                 * Scroll position must be adjusted accordingly but no more than
                 * there is room to scroll up. If a buffer row is needed make
                 * sure the last row ends up at least slightly below the
                 * viewport.
                 */
                double adjustedGap = Math.max(gapBelow,
                        bufferRowNeeded ? 1 : 0);
                double yDeltaScroll = Math.min(adjustedGap, scrollTop);
                moveViewportAndContent(null, 0, 0, -yDeltaScroll);
            }
        }

        /**
         * Adjust the scroll position and move the contained rows.
         * <p>
         * The difference between using this method and simply scrolling is that
         * this method "takes the rows and spacers with it" and renders them
         * appropriately. The viewport may be scrolled any arbitrary amount, and
         * the contents are moved appropriately, but always snapped into a
         * plausible place.
         * <p>
         * <dl>
         * <dt>Example 1</dt>
         * <dd>An Escalator with default row height 20px. Adjusting the scroll
         * position with 7.5px will move the viewport 7.5px down, but leave the
         * row where it is.</dd>
         * <dt>Example 2</dt>
         * <dd>An Escalator with default row height 20px. Adjusting the scroll
         * position with 27.5px will move the viewport 27.5px down, and place
         * the row at 20px.</dd>
         * </dl>
         *
         * @deprecated This method isn't used by Escalator anymore since Vaadin
         *             8.9 and the general row handling logic has been
         *             rewritten, so attempting to call this method may lead to
         *             unexpected consequences. This method is likely to get
         *             removed soon.
         * @param yDelta
         *            the delta of pixels by which to move the viewport and
         *            content. A positive value moves everything downwards,
         *            while a negative value moves everything upwards
         */
        @Deprecated
        public void moveViewportAndContent(final double yDelta) {

            if (yDelta == 0) {
                return;
            }

            double newTop = tBodyScrollTop + yDelta;
            verticalScrollbar.setScrollPos(newTop);

            final double defaultRowHeight = getDefaultRowHeight();
            double rowPxDelta = yDelta - (yDelta % defaultRowHeight);
            int rowIndexDelta = (int) (yDelta / defaultRowHeight);
            if (!WidgetUtil.pixelValuesEqual(rowPxDelta, 0)) {

                Collection<SpacerContainer.SpacerImpl> spacers = spacerContainer
                        .getSpacersAfterPx(tBodyScrollTop,
                                SpacerInclusionStrategy.PARTIAL);
                for (SpacerContainer.SpacerImpl spacer : spacers) {
                    spacer.setPositionDiff(0, rowPxDelta);
                    spacer.setRowIndex(spacer.getRow() + rowIndexDelta);
                }

                for (TableRowElement tr : visualRowOrder) {
                    setRowPosition(tr, 0, getRowTop(tr) + rowPxDelta);
                }
            }

            setBodyScrollPosition(tBodyScrollLeft, newTop);
        }

        /**
         * Move rows, spacers, and/or viewport up or down. For rows and spacers
         * either everything within visual range is affected (index
         * {@code null}) or only those from the given row index forward.
         * <p>
         * This method does not update spacer indexes.
         *
         * @param index
         *            the logical index from which forward the rows and spacers
         *            should be updated, or {@code null} if all of them
         * @param yDeltaRows
         *            how much rows should be shifted in pixels
         * @param yDeltaSpacers
         *            how much spacers should be shifted in pixels
         * @param yDeltaScroll
         *            how much scroll position should be shifted in pixels
         */
        private void moveViewportAndContent(Integer index,
                final double yDeltaRows, final double yDeltaSpacers,
                final double yDeltaScroll) {

            if (!WidgetUtil.pixelValuesEqual(yDeltaScroll, 0d)) {
                double newTop = tBodyScrollTop + yDeltaScroll;
                verticalScrollbar.setScrollPos(newTop);
                setBodyScrollPosition(tBodyScrollLeft, newTop);
            }

            if (!WidgetUtil.pixelValuesEqual(yDeltaSpacers, 0d)) {
                Collection<SpacerContainer.SpacerImpl> spacers;
                if (index == null) {
                    spacers = spacerContainer.getSpacersAfterPx(tBodyScrollTop,
                            SpacerInclusionStrategy.PARTIAL);
                } else {
                    spacers = spacerContainer.getSpacersForRowAndAfter(index);
                }
                for (SpacerContainer.SpacerImpl spacer : spacers) {
                    spacer.setPositionDiff(0, yDeltaSpacers);
                }
            }

            if (!WidgetUtil.pixelValuesEqual(yDeltaRows, 0d)) {
                if (index == null) {
                    // move all visible rows to the desired direction
                    for (TableRowElement tr : visualRowOrder) {
                        setRowPosition(tr, 0, getRowTop(tr) + yDeltaRows);
                    }
                } else {
                    // move all visible rows, including the index row, to the
                    // desired direction
                    shiftRowPositions(index - 1, yDeltaRows);
                }
            }
        }

        /**
         * Adds new physical escalator rows to the DOM at the given visual index
         * if there's still a need for more escalator rows.
         * <p>
         * If Escalator already is at (or beyond) max capacity, this method does
         * nothing to the DOM.
         * <p>
         * Calling this method repositions all the rows and spacers below the
         * insertion point.
         *
         * @param visualIndex
         *            the index at which to add new escalator rows to DOM
         * @param logicalIndex
         *            the logical index that corresponds with the first new
         *            escalator row, should usually be the same as visual index
         *            because there is still need for new rows, but this is not
         *            always the case e.g. if row height is changed
         * @param numberOfRows
         *            the number of rows to add at <code>index</code>
         * @return a list of the added rows
         */
        private List<TableRowElement> fillAndPopulateEscalatorRowsIfNeeded(
                final int visualIndex, final int logicalIndex,
                final int numberOfRows) {

            /*
             * We want to maintain enough rows to fill the entire viewport even
             * if their spacers have no height. If their spacers do have height
             * some of these rows may end up outside of the viewport, but that's
             * ok.
             */
            final int escalatorRowsStillFit = getMaxVisibleRowCount()
                    - getDomRowCount();
            final int escalatorRowsNeeded = Math.min(numberOfRows,
                    escalatorRowsStillFit);

            if (escalatorRowsNeeded > 0) {
                int rowsBeforeAddition = visualRowOrder.size();

                // this is AbstractRowContainer method and not easily overridden
                // to consider logical indexes separately from visual indexes,
                // so as a workaround we create the rows as if those two were
                // the same and then update the contents if needed
                final List<TableRowElement> addedRows = paintInsertStaticRows(
                        visualIndex, escalatorRowsNeeded);
                visualRowOrder.addAll(visualIndex, addedRows);

                if (visualIndex != logicalIndex) {
                    // row got populated with wrong contents, need to update
                    int adjustedLogicalIndex = 0;
                    if (visualIndex == 0) {
                        // added to the beginning of visual range, use the
                        // end of insertion range because the beginning might
                        // not fit completely
                        adjustedLogicalIndex = logicalIndex + numberOfRows
                                - addedRows.size();
                    } else {
                        // added anywhere else, use the beginning of
                        // insertion range and the rest of the rows get
                        // recycled below if there is room for them
                        adjustedLogicalIndex = logicalIndex;
                    }
                    for (int i = 0; i < addedRows.size(); ++i) {
                        TableRowElement tr = addedRows.get(i);
                        refreshRow(tr, adjustedLogicalIndex + i);
                    }
                }

                // if something is getting inserted instead of just being
                // brought to visual range, the rows below the insertion point
                // need to have their spacer indexes updated accordingly
                if (logicalIndex >= getTopRowLogicalIndex()
                        && visualIndex < rowsBeforeAddition) {
                    spacerContainer.updateSpacerIndexesForRowAndAfter(
                            logicalIndex, getRowCount(), addedRows.size());
                }

                // update the positions of the added rows and the rows below
                // them
                // TODO: this can lead to moving things around twice in case
                // some rows didn't get new dom rows (e.g. when expanding a
                // TreeGrid node with more children than can fit within the max
                // visual range size), consider moving this update elsewhere
                double rowTop = getRowTop(logicalIndex);
                for (int i = visualIndex; i < visualRowOrder.size(); i++) {

                    final TableRowElement tr = visualRowOrder.get(i);

                    setRowPosition(tr, 0, rowTop);
                    rowTop += getDefaultRowHeight();
                    SpacerContainer.SpacerImpl spacer = spacerContainer
                            .getSpacer(logicalIndex - visualIndex + i);
                    if (spacer != null) {
                        spacer.setPosition(0, rowTop);
                        rowTop += spacer.getHeight();
                    }
                }

                // Execute the registered callback function for newly created
                // rows
                Optional.ofNullable(newEscalatorRowCallback)
                        .ifPresent(callback -> callback.accept(addedRows));

                return addedRows;
            } else {
                return Collections.emptyList();
            }
        }

        private int getMaxVisibleRowCount() {
            double heightOfSection = getHeightOfSection();
            // By including the possibly shown scrollbar height, we get a
            // consistent count and do not add/remove rows whenever a scrollbar
            // is shown. Make sure that two extra rows are included for
            // assisting with tab navigation on both sides of the viewport.
            heightOfSection += horizontalScrollbarDeco.getOffsetHeight();
            double defaultRowHeight = getDefaultRowHeight();
            final int maxVisibleRowCount = (int) Math
                    .ceil(heightOfSection / defaultRowHeight) + 2;

            /*
             * maxVisibleRowCount can become negative if the headers and footers
             * start to overlap. This is a crazy situation, but Vaadin blinks
             * the components a lot, so it's feasible.
             */
            return Math.max(0, maxVisibleRowCount);
        }

        @Override
        protected void paintRemoveRows(final int index,
                final int numberOfRows) {
            if (numberOfRows == 0) {
                return;
            }
            /*
             * NOTE: this method handles and manipulates logical, visual, and
             * physical indexes a lot. If you don't remember what those mean and
             * how they relate to each other, see the top of this class for
             * Maintenance Notes.
             *
             * At the beginning of this method the logical index of the data
             * provider has already been updated to include the new rows, but
             * visual and physical indexes have not, nor has the spacer indexing
             * been updated, and the topRowLogicalIndex may be out of date as
             * well.
             */

            // logical index of the first old row, also the difference between
            // logical index and visual index before any rows have been removed
            final int oldTopRowLogicalIndex = getTopRowLogicalIndex();
            // length of the visual range before anything gets removed
            final int oldVisualRangeLength = visualRowOrder.size();

            // logical range of the removed rows
            final Range removedRowsLogicalRange = Range.withLength(index,
                    numberOfRows);

            // check which parts of the removed range fall within or beyond the
            // visual range
            final Range[] partitions = removedRowsLogicalRange
                    .partitionWith(Range.withLength(oldTopRowLogicalIndex,
                            oldVisualRangeLength));
            final Range removedLogicalAbove = partitions[0];
            final Range removedLogicalBelow = partitions[2];
            final Range removedLogicalWithin = partitions[1];

            if (removedLogicalBelow.length() == numberOfRows) {
                /*
                 * Rows were removed entirely from below the visual range. No
                 * rows to recycle or scroll position to adjust, just need to
                 * recalculate scrollbar height. No need to touch the spacer
                 * indexing or the physical index.
                 */
                scroller.recalculateScrollbarsForVirtualViewport();

                // Visual range contents remain the same, no need to fire a
                // RowVisibilityChangeEvent.
            } else if (removedLogicalAbove.length() == numberOfRows) {
                /*
                 * Rows were removed entirely from above the visual range. No
                 * rows to recycle, just need to update the spacer indexing and
                 * the content positions. No need to touch the physical index.
                 */

                // update the logical indexes of remaining spacers
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        oldTopRowLogicalIndex,
                        oldTopRowLogicalIndex + oldVisualRangeLength,
                        -numberOfRows);

                // default height of a single row
                final double defaultRowHeight = getDefaultRowHeight();

                // how much viewport, rows, and spacers should be shifted based
                // on the removed rows, assume there were no spacers to remove
                final double yDelta = numberOfRows * defaultRowHeight;

                // shift everything up
                moveViewportAndContent(null, -yDelta, -yDelta, -yDelta);

                // update the top row logical index according to any removed
                // rows
                updateTopRowLogicalIndex(-numberOfRows);

                // update scrollbar
                scroller.recalculateScrollbarsForVirtualViewport();

                // Visual range contents remain the same, no need to fire a
                // RowVisibilityChangeEvent.
            } else {
                /*
                 * Rows are being removed at least partially from within the
                 * visual range. This is where things get tricky. We might have
                 * to scroll up or down or nowhere at all, depending on the
                 * situation.
                 */

                // Visual range contents changed, RowVisibilityChangeEvent will
                // be triggered within this method
                paintRemoveRowsWithinVisualRange(index, numberOfRows,
                        oldTopRowLogicalIndex, oldVisualRangeLength,
                        removedLogicalAbove.length(), removedLogicalWithin);
            }
        }

        /**
         * Row removal handling for {@link #paintRemoveRows(int, int)} when the
         * removed range intersects the visual range at least partially.
         * <p>
         * NOTE: This method should not be called directly from anywhere else.
         *
         * @param index
         * @param numberOfRows
         * @param oldTopRowLogicalIndex
         * @param oldVisualRangeLength
         * @param removedAboveLength
         * @param removedLogicalWithin
         */
        private void paintRemoveRowsWithinVisualRange(int index,
                int numberOfRows, int oldTopRowLogicalIndex,
                int oldVisualRangeLength, int removedAboveLength,
                Range removedLogicalWithin) {
            /*
             * Calculating where the visual range should start after the
             * removals is not entirely trivial.
             *
             * Initially, any rows removed from within the visual range won't
             * affect the top index, even if they are removed from the
             * beginning, as the rows are also removed from the logical index.
             * Likewise we don't need to care about rows removed from below the
             * visual range. On the other hand, any rows removed from above the
             * visual range do shift the index down.
             *
             * However, in all of these cases, if there aren't enough rows below
             * the visual range to replace the content removed from within the
             * visual range, more rows need to be brought in from above the old
             * visual range in turn. This shifts the index down even further.
             */

            // scroll position before any rows or spacers are removed
            double scrollTop = getScrollTop();

            Range removedVisualWithin = convertToVisual(removedLogicalWithin);
            int remainingVisualRangeRowCount = visualRowOrder.size()
                    - removedVisualWithin.length();

            int newTopRowLogicalIndex = oldTopRowLogicalIndex
                    - removedAboveLength;
            int rowsToIncludeFromBelow = Math.min(
                    getRowCount() - newTopRowLogicalIndex
                            - remainingVisualRangeRowCount,
                    removedLogicalWithin.length());
            int rowsToIncludeFromAbove = removedLogicalWithin.length()
                    - rowsToIncludeFromBelow;
            int rowsToRemoveFromDom = 0;
            if (rowsToIncludeFromAbove > 0) {
                // don't try to bring in more rows than exist, it's possible
                // to remove enough rows that visual range won't be full
                // anymore
                rowsToRemoveFromDom = Math
                        .max(rowsToIncludeFromAbove - newTopRowLogicalIndex, 0);
                rowsToIncludeFromAbove -= rowsToRemoveFromDom;

                newTopRowLogicalIndex -= rowsToIncludeFromAbove;
            }

            int visualIndexToRemove = Math.max(index - oldTopRowLogicalIndex,
                    0);

            // remove extra dom rows and their spacers if any
            double removedFromDomSpacerHeights = 0d;
            if (rowsToRemoveFromDom > 0) {
                for (int i = 0; i < rowsToRemoveFromDom; ++i) {
                    TableRowElement tr = visualRowOrder
                            .remove(visualIndexToRemove);

                    // logical index of this row before anything got removed
                    int logicalRowIndex = oldTopRowLogicalIndex
                            + visualIndexToRemove + i;
                    double spacerHeight = spacerContainer
                            .getSpacerHeight(logicalRowIndex);
                    removedFromDomSpacerHeights += spacerHeight;
                    spacerContainer.removeSpacer(logicalRowIndex);

                    paintRemoveRow(tr, removedVisualWithin.getStart());
                    removeRowPosition(tr);
                }

                // update the associated row indexes for remaining spacers,
                // even for those rows that are going to get recycled
                spacerContainer.updateSpacerIndexesForRowAndAfter(
                        oldTopRowLogicalIndex + visualIndexToRemove
                                + rowsToRemoveFromDom,
                        oldTopRowLogicalIndex + oldVisualRangeLength,
                        -rowsToRemoveFromDom);
            }

            // add new content from below visual range, if there is any
            if (rowsToIncludeFromBelow > 0) {
                // removed rows are recycled to just below the old visual
                // range, calculate the logical index of the insertion
                // point that is just below the existing rows, taking into
                // account that the indexing has changed with the removal
                int firstBelow = newTopRowLogicalIndex + rowsToIncludeFromAbove
                        + remainingVisualRangeRowCount;

                moveAndUpdateEscalatorRows(
                        Range.withLength(visualIndexToRemove,
                                rowsToIncludeFromBelow),
                        visualRowOrder.size(), firstBelow);
            }

            // add new content from above visual range, if there is any
            // -- this is left last because most of the time it isn't even
            // needed
            if (rowsToIncludeFromAbove > 0) {
                moveAndUpdateEscalatorRows(
                        Range.withLength(visualIndexToRemove,
                                rowsToIncludeFromAbove),
                        0, newTopRowLogicalIndex);
            }

            // recycling updates all relevant row and spacer positions but
            // if we only removed DOM rows and didn't recycle any we still
            // need to shift up the rows below the removal point
            if (rowsToIncludeFromAbove <= 0 && rowsToIncludeFromBelow <= 0) {
                // update the positions for the rows and spacers below the
                // removed ones, assume there is no need to update scroll
                // position since the final check adjusts that if needed
                double yDelta = numberOfRows * getDefaultRowHeight()
                        + removedFromDomSpacerHeights;
                moveViewportAndContent(
                        newTopRowLogicalIndex + visualIndexToRemove, -yDelta,
                        -yDelta, 0);
            }

            setTopRowLogicalIndex(newTopRowLogicalIndex);

            scroller.recalculateScrollbarsForVirtualViewport();

            // calling this method also triggers adding new spacers to the
            // recycled rows, if any are needed
            fireRowVisibilityChangeEvent();

            // populating the spacers might take a while, delay calculations
            // or the viewport might get adjusted too high
            Scheduler.get().scheduleFinally(() -> {
                // make sure there isn't a gap at the bottom after removal
                // and adjust the viewport if there is

                // FIXME: this should be doable with
                // adjustScrollPositionIfNeeded() but it uses current
                // scrollTop, which may have ended in wrong position and
                // results in assuming too big gap and consequently
                // scrolling up too much
                double extraSpaceAtBottom = scrollTop + getHeightOfSection()
                        - getRowTop(getTopRowLogicalIndex()
                                + visualRowOrder.size());
                if (extraSpaceAtBottom > 0 && scrollTop > 0) {
                    // we need to move the viewport up to adjust, while the
                    // rows and spacers can remain where they are
                    double yDeltaScroll = Math.min(extraSpaceAtBottom,
                            scrollTop);
                    moveViewportAndContent(null, 0, 0, -yDeltaScroll);
                }
            });

            // update physical index
            sortDomElements();
        }

        @Override
        public int getLogicalRowIndex(final TableRowElement tr) {
            assert tr
                    .getParentNode() == root : "The given element isn't a row element in the body";
            int internalIndex = visualRowOrder.indexOf(tr);
            return getTopRowLogicalIndex() + internalIndex;
        }

        @Override
        protected void recalculateSectionHeight() {
            // NOOP for body, since it doesn't make any sense.
        }

        /**
         * Adjusts the row index and number to be relevant for the current
         * virtual viewport.
         * <p>
         * It converts a logical range of rows index to the matching visual
         * range, truncating the resulting range with the viewport.
         * <p>
         * <ul>
         * <li>Escalator contains logical rows 0..100
         * <li>Current viewport showing logical rows 20..29
         * <li>convertToVisual([20..29]) &rarr; [0..9]
         * <li>convertToVisual([15..24]) &rarr; [0..4]
         * <li>convertToVisual([25..29]) &rarr; [5..9]
         * <li>convertToVisual([26..39]) &rarr; [6..9]
         * <li>convertToVisual([0..5]) &rarr; [0..-1] <em>(empty)</em>
         * <li>convertToVisual([35..1]) &rarr; [0..-1] <em>(empty)</em>
         * <li>convertToVisual([0..100]) &rarr; [0..9]
         * </ul>
         *
         * @return a logical range converted to a visual range, truncated to the
         *         current viewport. The first visual row has the index 0.
         */
        private Range convertToVisual(final Range logicalRange) {

            if (logicalRange.isEmpty()) {
                return logicalRange;
            } else if (visualRowOrder.isEmpty()) {
                // empty range
                return Range.withLength(0, 0);
            }

            final int currentTopRowIndex = getTopRowLogicalIndex();

            final Range[] partitions = logicalRange
                    .partitionWith(getVisibleRowRange());
            final Range insideRange = partitions[1];
            return insideRange.offsetBy(-currentTopRowIndex);
        }

        @Override
        protected String getCellElementTagName() {
            return "td";
        }

        @Override
        protected double getHeightOfSection() {
            final int tableHeight = tableWrapper.getOffsetHeight();
            final double footerHeight = footer.getHeightOfSection();
            final double headerHeight = header.getHeightOfSection();

            double heightOfSection = tableHeight - footerHeight - headerHeight;
            return Math.max(0, heightOfSection);
        }

        @Override
        protected void refreshCells(Range logicalRowRange, Range colRange) {
            Profiler.enter("Escalator.BodyRowContainer.refreshRows");

            final Range visualRange = convertToVisual(logicalRowRange);

            if (!visualRange.isEmpty()) {
                final int firstLogicalRowIndex = getLogicalRowIndex(
                        visualRowOrder.getFirst());
                for (int rowNumber = visualRange
                        .getStart(); rowNumber < visualRange
                                .getEnd(); rowNumber++) {
                    refreshRow(visualRowOrder.get(rowNumber),
                            firstLogicalRowIndex + rowNumber, colRange);
                }
            }

            Profiler.leave("Escalator.BodyRowContainer.refreshRows");
        }

        @Override
        protected TableRowElement getTrByVisualIndex(final int index)
                throws IndexOutOfBoundsException {
            if (index >= 0 && index < visualRowOrder.size()) {
                return visualRowOrder.get(index);
            } else {
                throw new IndexOutOfBoundsException(
                        "No such visual index: " + index);
            }
        }

        @Override
        public TableRowElement getRowElement(int index) {
            if (index < 0 || index >= getRowCount()) {
                throw new IndexOutOfBoundsException(
                        "No such logical index: " + index);
            }
            int visualIndex = index
                    - getLogicalRowIndex(visualRowOrder.getFirst());
            if (visualIndex >= 0 && visualIndex < visualRowOrder.size()) {
                return super.getRowElement(visualIndex);
            } else {
                throw new IllegalStateException("Row with logical index "
                        + index + " is currently not available in the DOM");
            }
        }

        private void setBodyScrollPosition(final double scrollLeft,
                final double scrollTop) {
            tBodyScrollLeft = scrollLeft;
            tBodyScrollTop = scrollTop;
            position.set(bodyElem, -tBodyScrollLeft, -tBodyScrollTop);
            position.set(spacerDecoContainer, 0, -tBodyScrollTop);
        }

        /**
         * Make sure that there is a correct amount of escalator rows: Add more
         * if needed, or remove any superfluous ones.
         * <p>
         * This method should be called when e.g. the height of the Escalator
         * changes.
         * <p>
         * <em>Note:</em> This method will make sure that the escalator rows are
         * placed in the proper places. By default new rows are added below, but
         * if the content is scrolled down, the rows are populated on top
         * instead.
         */
        public void verifyEscalatorCount() {
            /*
             * This method indeed has a smell very similar to paintRemoveRows
             * and paintInsertRows.
             *
             * Unfortunately, the code of those can't trivially be shared, since
             * there are some slight differences in the respective
             * responsibilities. The "paint" methods fake the addition and
             * removal of rows, and make sure to either push existing data out
             * of view, or draw new data into view. Only in some special cases
             * will the DOM element count change.
             *
             * This method, however, has the explicit responsibility to verify
             * that when "something" happens, we still have the correct amount
             * of escalator rows in the DOM, and if not, we make sure to modify
             * that count. Only in some special cases do we need to take into
             * account other things than simply modifying the DOM element count.
             */

            Profiler.enter("Escalator.BodyRowContainer.verifyEscalatorCount");

            if (!isAttached()) {
                return;
            }

            int oldTopRowLogicalIndex = getTopRowLogicalIndex();
            int oldVisualRangeLength = visualRowOrder.size();

            final int maxVisibleRowCount = getMaxVisibleRowCount();
            final int neededEscalatorRows = Math.min(maxVisibleRowCount,
                    body.getRowCount());

            final int rowDiff = neededEscalatorRows - oldVisualRangeLength;

            if (rowDiff > 0) {
                // more rows are needed

                // calculate the indexes for adding rows below the last row of
                // the visual range
                final int visualTargetIndex = oldVisualRangeLength;
                final int logicalTargetIndex;
                if (!visualRowOrder.isEmpty()) {
                    logicalTargetIndex = oldTopRowLogicalIndex
                            + visualTargetIndex;
                } else {
                    logicalTargetIndex = 0;
                }

                // prioritise adding to the bottom so that there's less chance
                // for a gap if a details row is later closed (e.g. by user)
                final int addToBottom = Math.min(rowDiff,
                        getRowCount() - logicalTargetIndex);
                final int addToTop = Math.max(rowDiff - addToBottom, 0);

                if (addToTop > 0) {
                    fillAndPopulateEscalatorRowsIfNeeded(0,
                            oldTopRowLogicalIndex - addToTop, addToTop);

                    updateTopRowLogicalIndex(-addToTop);
                }
                if (addToBottom > 0) {
                    // take into account that rows may have got added to top as
                    // well, affects visual but not logical indexing
                    fillAndPopulateEscalatorRowsIfNeeded(
                            visualTargetIndex + addToTop, logicalTargetIndex,
                            addToBottom);

                    // adding new rows due to resizing may have created a gap in
                    // the middle, check whether the existing rows need moving
                    double rowTop = getRowTop(oldTopRowLogicalIndex);
                    if (rowTop > getRowTop(visualRowOrder.get(addToTop))) {
                        for (int i = addToTop; i < visualTargetIndex; i++) {

                            final TableRowElement tr = visualRowOrder.get(i);

                            setRowPosition(tr, 0, rowTop);
                            rowTop += getDefaultRowHeight();
                            SpacerContainer.SpacerImpl spacer = spacerContainer
                                    .getSpacer(oldTopRowLogicalIndex + i);
                            if (spacer != null) {
                                spacer.setPosition(0, rowTop);
                                rowTop += spacer.getHeight();
                            }
                        }
                    }
                }
            } else if (rowDiff < 0) {
                // rows need to be removed

                // prioritise removing rows from above the viewport as they are
                // less likely to be needed in a hurry -- the rows below are
                // more likely to slide into view when spacer contents are
                // updated

                // top of visible area before any rows are actually added
                double scrollTop = getScrollTop();

                // visual index of the first actually visible row, including
                // spacer
                int oldFirstVisibleVisualIndex = -1;
                ListIterator<TableRowElement> iter = visualRowOrder
                        .listIterator(0);
                for (int i = 0; i < visualRowOrder.size(); ++i) {
                    if (positions.getTop(iter.next()) > scrollTop) {
                        break;
                    }
                    oldFirstVisibleVisualIndex = i;
                }

                int rowsToRemoveFromAbove = Math.max(0, Math
                        .min(Math.abs(rowDiff), oldFirstVisibleVisualIndex));

                boolean spacersRemovedFromAbove = false;
                if (rowsToRemoveFromAbove > 0) {
                    double initialSpacerHeightSum = spacerContainer
                            .getSpacerHeightsSum();
                    iter = visualRowOrder.listIterator(0);
                    for (int i = 0; i < rowsToRemoveFromAbove; ++i) {
                        final Element first = iter.next();
                        first.removeFromParent();
                        iter.remove();

                        spacerContainer.removeSpacer(oldTopRowLogicalIndex + i);
                    }
                    spacersRemovedFromAbove = initialSpacerHeightSum != spacerContainer
                            .getSpacerHeightsSum();
                }

                // if there weren't enough rows above, remove the rest from
                // below
                int rowsToRemoveFromBelow = Math.abs(rowDiff)
                        - rowsToRemoveFromAbove;
                if (rowsToRemoveFromBelow > 0) {
                    iter = visualRowOrder.listIterator(visualRowOrder.size());
                    for (int i = 1; i <= rowsToRemoveFromBelow; ++i) {
                        final Element last = iter.previous();
                        last.removeFromParent();
                        iter.remove();

                        spacerContainer.removeSpacer(oldTopRowLogicalIndex
                                + oldVisualRangeLength - i);
                    }
                }

                updateTopRowLogicalIndex(rowsToRemoveFromAbove);

                if (spacersRemovedFromAbove) {
                    updateRowPositions(oldTopRowLogicalIndex, 0,
                            visualRowOrder.size());
                }

                // removing rows might cause a gap at the bottom
                adjustScrollPositionIfNeeded();
            }

            if (rowDiff != 0) {
                scroller.recalculateScrollbarsForVirtualViewport();

                fireRowVisibilityChangeEvent();
            }

            Profiler.leave("Escalator.BodyRowContainer.verifyEscalatorCount");
        }

        @Override
        protected void reapplyDefaultRowHeights() {
            if (visualRowOrder.isEmpty()) {
                return;
            }

            Profiler.enter(
                    "Escalator.BodyRowContainer.reapplyDefaultRowHeights");

            double spacerHeightsAboveViewport = spacerContainer
                    .getSpacerHeightsSumUntilPx(
                            verticalScrollbar.getScrollPos());
            double allSpacerHeights = spacerContainer.getSpacerHeightsSum();

            /* step 1: resize and reposition rows */

            // there should be no spacers above the visual range
            double spacerHeights = 0;
            for (int i = 0; i < visualRowOrder.size(); i++) {
                TableRowElement tr = visualRowOrder.get(i);
                reapplyRowHeight(tr, getDefaultRowHeight());

                final int logicalIndex = getTopRowLogicalIndex() + i;
                double y = logicalIndex * getDefaultRowHeight() + spacerHeights;
                setRowPosition(tr, 0, y);
                SpacerContainer.SpacerImpl spacer = spacerContainer
                        .getSpacer(logicalIndex);
                if (spacer != null) {
                    spacer.setPosition(0, y + getDefaultRowHeight());
                    spacerHeights += spacer.getHeight();
                }
            }

            /*
             * step 2: move scrollbar so that it corresponds to its previous
             * place
             */

            // scrollRatio has to be calculated without spacers for it to be
            // comparable between different row heights
            double scrollRatio = (verticalScrollbar.getScrollPos()
                    - spacerHeightsAboveViewport)
                    / (verticalScrollbar.getScrollSize() - allSpacerHeights);
            scroller.recalculateScrollbarsForVirtualViewport();
            // spacer heights have to be added back for setting new scrollPos
            verticalScrollbar.setScrollPos(
                    (int) ((getDefaultRowHeight() * getRowCount() * scrollRatio)
                            + spacerHeightsAboveViewport));
            setBodyScrollPosition(horizontalScrollbar.getScrollPos(),
                    verticalScrollbar.getScrollPos());
            scroller.onScroll();

            /*
             * step 3: make sure we have the correct amount of escalator rows.
             */
            verifyEscalatorCount();

            Profiler.leave(
                    "Escalator.BodyRowContainer.reapplyDefaultRowHeights");
        }

        /**
         * Sorts the rows in the DOM to correspond to the visual order.
         *
         * @see #visualRowOrder
         */
        private void sortDomElements() {
            final String profilingName = "Escalator.BodyRowContainer.sortDomElements";
            Profiler.enter(profilingName);

            /*
             * Focus is lost from an element if that DOM element is (or any of
             * its parents are) removed from the document. Therefore, we sort
             * everything around that row instead.
             */
            final TableRowElement focusedRow = getRowWithFocus();

            if (focusedRow != null) {
                assert focusedRow
                        .getParentElement() == root : "Trying to sort around a row that doesn't exist in body";
                assert visualRowOrder.contains(focusedRow)
                        || body.spacerContainer.isSpacer(
                                focusedRow) : "Trying to sort around a row that doesn't exist in visualRowOrder or is not a spacer.";
            }

            /*
             * Two cases handled simultaneously:
             *
             * 1) No focus on rows. We iterate visualRowOrder backwards, and
             * take the respective element in the DOM, and place it as the first
             * child in the body element. Then we take the next-to-last from
             * visualRowOrder, and put that first, pushing the previous row as
             * the second child. And so on...
             *
             * 2) Focus on some row within Escalator body. Again, we iterate
             * visualRowOrder backwards. This time, we use the focused row as a
             * pivot: Instead of placing rows from the bottom of visualRowOrder
             * and placing it first, we place it underneath the focused row.
             * Once we hit the focused row, we don't move it (to not reset
             * focus) but change sorting mode. After that, we place all rows as
             * the first child.
             */

            List<TableRowElement> orderedBodyRows = new ArrayList<>(
                    visualRowOrder);
            Map<Integer, SpacerContainer.SpacerImpl> spacers = body.spacerContainer
                    .getSpacers();

            /*
             * Start at -1 to include a spacer that is rendered above the
             * viewport, but its parent row is still not shown
             */
            for (int i = -1; i < visualRowOrder.size(); i++) {
                SpacerContainer.SpacerImpl spacer = spacers
                        .remove(Integer.valueOf(getTopRowLogicalIndex() + i));

                if (spacer != null) {
                    orderedBodyRows.add(i + 1, spacer.getRootElement());
                    spacer.show();
                }
            }
            /*
             * At this point, invisible spacers aren't reordered, so their
             * position in the DOM will remain undefined.
             */

            // If a spacer was not reordered, it means that it's out of visual
            // range. This should never happen with default Grid implementations
            // but it's possible on an extended Escalator.
            for (SpacerContainer.SpacerImpl unmovedSpacer : spacers.values()) {
                unmovedSpacer.hide();
            }

            /*
             * If we have a focused row, start in the mode where we put
             * everything underneath that row. Otherwise, all rows are placed as
             * first child.
             */
            boolean insertFirst = (focusedRow == null);

            final ListIterator<TableRowElement> i = orderedBodyRows
                    .listIterator(orderedBodyRows.size());
            while (i.hasPrevious()) {
                TableRowElement tr = i.previous();

                if (tr == focusedRow) {
                    insertFirst = true;
                } else if (insertFirst) {
                    // remove row explicitly to work around an IE11 bug (#9850)
                    if (BrowserInfo.get().isIE11()
                            && tr.equals(root.getFirstChildElement())) {
                        root.removeChild(tr);
                    }
                    root.insertFirst(tr);
                } else {
                    root.insertAfter(tr, focusedRow);
                }
            }

            Profiler.leave(profilingName);
        }

        /**
         * Get the {@literal <tbody>} row that contains (or has) focus.
         *
         * @return The {@literal <tbody>} row that contains a focused DOM
         *         element, or <code>null</code> if focus is outside of a body
         *         row.
         */
        private TableRowElement getRowWithFocus() {
            TableRowElement rowContainingFocus = null;

            final Element focusedElement = WidgetUtil.getFocusedElement();

            if (focusedElement != null && root.isOrHasChild(focusedElement)) {
                Element e = focusedElement;

                while (e != null && e != root) {
                    /*
                     * You never know if there's several tables embedded in a
                     * cell... We'll take the deepest one.
                     */
                    if (TableRowElement.is(e)) {
                        rowContainingFocus = TableRowElement.as(e);
                    }
                    e = e.getParentElement();
                }
            }

            return rowContainingFocus;
        }

        /**
         * Returns the cell object which contains information about the cell or
         * spacer the element is in. As an implementation detail each spacer is
         * a row with one cell, but they are stored in their own container and
         * share the indexing with the regular rows.
         *
         * @param element
         *            The element to get the cell for. If element is not present
         *            in row or spacer container then <code>null</code> is
         *            returned.
         *
         * @return the cell reference of the element, or <code>null</code> if
         *         element is not present in the {@link RowContainer} or the
         *         {@link SpacerContainer}.
         */
        @Override
        public Cell getCell(Element element) {
            Cell cell = super.getCell(element);
            if (cell == null) {
                return null;
            }

            // Convert DOM coordinates to logical coordinates for rows
            TableRowElement rowElement = (TableRowElement) cell.getElement()
                    .getParentElement();
            if (!visualRowOrder.contains(rowElement)) {
                for (Entry<Integer, SpacerContainer.SpacerImpl> entry : spacerContainer
                        .getSpacers().entrySet()) {
                    if (rowElement.equals(entry.getValue().getRootElement())) {
                        return new Cell(entry.getKey(), cell.getColumn(),
                                cell.getElement());
                    }
                }
                return null;
            }
            return new Cell(getLogicalRowIndex(rowElement), cell.getColumn(),
                    cell.getElement());
        }

        @Override
        public void setSpacer(int rowIndex, double height)
                throws IllegalArgumentException {
            spacerContainer.setSpacer(rowIndex, height);
        }

        @Override
        public boolean spacerExists(int rowIndex) {
            return spacerContainer.spacerExists(rowIndex);
        }

        @Override
        public void setSpacerUpdater(SpacerUpdater spacerUpdater)
                throws IllegalArgumentException {
            spacerContainer.setSpacerUpdater(spacerUpdater);
        }

        @Override
        public SpacerUpdater getSpacerUpdater() {
            return spacerContainer.getSpacerUpdater();
        }

        /**
         * <em>Calculates</em> the expected top position of a row at a logical
         * index, regardless if there is one there currently or not.
         * <p>
         * This method relies on fixed row height (by
         * {@link #getDefaultRowHeight()}) and can only take into account
         * spacers that are within visual range. Any scrolling might invalidate
         * these results, so this method shouldn't be used to estimate scroll
         * positions.
         *
         * @param logicalIndex
         *            the logical index of the row for which to calculate the
         *            top position
         * @return the position where the row should currently be, were it to
         *         exist
         * @see #getRowTop(TableRowElement)
         */
        private double getRowTop(int logicalIndex) {
            double top = spacerContainer
                    .getSpacerHeightsSumUntilIndex(logicalIndex);
            return top + (logicalIndex * getDefaultRowHeight());
        }

        public void shiftRowPositions(int row, double diff) {
            for (TableRowElement tr : getVisibleRowsAfter(row)) {
                setRowPosition(tr, 0, getRowTop(tr) + diff);
            }
        }

        private List<TableRowElement> getVisibleRowsAfter(int logicalRow) {
            Range visibleRowLogicalRange = getVisibleRowRange();

            boolean allRowsAreInView = logicalRow < visibleRowLogicalRange
                    .getStart();
            boolean noRowsAreInView = logicalRow >= visibleRowLogicalRange
                    .getEnd() - 1;

            if (allRowsAreInView) {
                return Collections.unmodifiableList(visualRowOrder);
            } else if (noRowsAreInView) {
                return Collections.emptyList();
            } else {
                int fromIndex = (logicalRow - visibleRowLogicalRange.getStart())
                        + 1;
                int toIndex = visibleRowLogicalRange.length();
                List<TableRowElement> sublist = visualRowOrder
                        .subList(fromIndex, toIndex);
                return Collections.unmodifiableList(sublist);
            }
        }

        @Override
        public int getDomRowCount() {
            return root.getChildCount()
                    - spacerContainer.getSpacersInDom().size();
        }

        @Override
        protected boolean rowCanBeFrozen(TableRowElement tr) {
            return visualRowOrder.contains(tr);
        }

        void reapplySpacerWidths() {
            spacerContainer.reapplySpacerWidths();
        }

        void scrollToRowSpacerOrBoth(int targetRowIndex,
                ScrollDestination destination, double padding,
                ScrollType scrollType) {
            if (!ensureScrollingAllowed()) {
                return;
            }
            validateScrollDestination(destination, (int) padding);
            // ignore the special case of -1 index spacer from the row index
            // validation
            if (!(targetRowIndex == -1 && !ScrollType.ROW.equals(scrollType))) {
                // throws an IndexOutOfBoundsException if not valid
                verifyValidRowIndex(targetRowIndex);
            }
            int oldTopRowLogicalIndex = getTopRowLogicalIndex();
            int visualRangeLength = visualRowOrder.size();
            int paddingInRows = 0;
            if (!WidgetUtil.pixelValuesEqual(padding, 0d)) {
                paddingInRows = (int) Math
                        .ceil(Double.valueOf(padding) / getDefaultRowHeight());
            }

            // calculate the largest index necessary to include at least
            // partially below the top of the viewport and the smallest index
            // necessary to include at least partially above the bottom of the
            // viewport (target row itself might not be if padding is negative)
            int firstVisibleIndexIfScrollingUp = targetRowIndex - paddingInRows;
            int lastVisibleIndexIfScrollingDown = targetRowIndex
                    + paddingInRows;

            int oldFirstBelowIndex = oldTopRowLogicalIndex + visualRangeLength;
            int newTopRowLogicalIndex;
            int logicalTargetIndex;
            switch (destination) {
            case ANY:
                // scroll as little as possible, take into account that there
                // needs to be a buffer row at both ends if there is room for
                // one
                boolean newRowsNeededAbove = (firstVisibleIndexIfScrollingUp < oldTopRowLogicalIndex)
                        || (firstVisibleIndexIfScrollingUp == oldTopRowLogicalIndex
                                && targetRowIndex > 0);
                boolean rowsNeededBelow = (lastVisibleIndexIfScrollingDown >= oldFirstBelowIndex)
                        || ((lastVisibleIndexIfScrollingDown == oldFirstBelowIndex
                                - 1) && (oldFirstBelowIndex < getRowCount()));
                if (newRowsNeededAbove) {
                    // scroll up, add buffer row if it fits
                    logicalTargetIndex = Math
                            .max(firstVisibleIndexIfScrollingUp - 1, 0);
                    newTopRowLogicalIndex = logicalTargetIndex;
                } else if (rowsNeededBelow) {
                    // scroll down, add buffer row if it fits
                    newTopRowLogicalIndex = Math.min(
                            lastVisibleIndexIfScrollingDown + 1,
                            getRowCount() - 1) - visualRangeLength + 1;
                    if (newTopRowLogicalIndex
                            - oldTopRowLogicalIndex < visualRangeLength) {
                        // partial recycling, target index at the end of
                        // current range
                        logicalTargetIndex = oldFirstBelowIndex;
                    } else {
                        // full recycling, target index the same as the new
                        // top row index
                        logicalTargetIndex = newTopRowLogicalIndex;
                    }
                } else {
                    // no need to recycle rows but viewport might need
                    // adjusting regardless
                    logicalTargetIndex = -1;
                    newTopRowLogicalIndex = oldTopRowLogicalIndex;
                }
                break;
            case END:
                // target row at the bottom of the viewport
                newTopRowLogicalIndex = lastVisibleIndexIfScrollingDown + 1
                        - visualRangeLength + 1;
                newTopRowLogicalIndex = ensureTopRowLogicalIndexSanity(
                        newTopRowLogicalIndex);
                if ((newTopRowLogicalIndex > oldTopRowLogicalIndex)
                        && (newTopRowLogicalIndex
                                - oldTopRowLogicalIndex < visualRangeLength)) {
                    // partial recycling, target index at the end of
                    // current range
                    logicalTargetIndex = oldFirstBelowIndex;
                } else {
                    // full recycling, target index the same as the new
                    // top row index
                    logicalTargetIndex = newTopRowLogicalIndex;
                }
                break;
            case MIDDLE:
                // target row at the middle of the viewport, padding has to be
                // zero or we never would have reached this far
                newTopRowLogicalIndex = targetRowIndex - visualRangeLength / 2;
                newTopRowLogicalIndex = ensureTopRowLogicalIndexSanity(
                        newTopRowLogicalIndex);
                if (newTopRowLogicalIndex < oldTopRowLogicalIndex) {
                    logicalTargetIndex = newTopRowLogicalIndex;
                } else if (newTopRowLogicalIndex > oldTopRowLogicalIndex) {
                    if (newTopRowLogicalIndex
                            - oldTopRowLogicalIndex < visualRangeLength) {
                        // partial recycling, target index at the end of
                        // current range
                        logicalTargetIndex = oldFirstBelowIndex;
                    } else {
                        // full recycling, target index the same as the new
                        // top row index
                        logicalTargetIndex = newTopRowLogicalIndex;
                    }
                } else {
                    logicalTargetIndex = -1;
                }
                break;
            case START:
                // target row at the top of the viewport, include buffer
                // row if there is room for one
                newTopRowLogicalIndex = firstVisibleIndexIfScrollingUp - 1;
                newTopRowLogicalIndex = ensureTopRowLogicalIndexSanity(
                        newTopRowLogicalIndex);
                if (getVisibleRowRange().contains(newTopRowLogicalIndex)) {
                    logicalTargetIndex = oldTopRowLogicalIndex
                            + visualRangeLength;
                } else {
                    logicalTargetIndex = newTopRowLogicalIndex;
                }
                break;
            default:
                String msg = "Internal: Unsupported ScrollDestination: ";
                throw new IllegalArgumentException(msg + destination.name());
            }

            // adjust visual range if necessary
            if (newTopRowLogicalIndex < oldTopRowLogicalIndex) {
                adjustVisualRangeUpForScrollToRowSpacerOrBoth(
                        oldTopRowLogicalIndex, visualRangeLength,
                        logicalTargetIndex);
            } else if (newTopRowLogicalIndex > oldTopRowLogicalIndex) {
                adjustVisualRangeDownForScrollToRowSpacerOrBoth(
                        oldTopRowLogicalIndex, visualRangeLength,
                        newTopRowLogicalIndex, logicalTargetIndex);
            }
            boolean rowsWereMoved = newTopRowLogicalIndex != oldTopRowLogicalIndex;

            // update scroll position if necessary
            adjustScrollPositionForScrollToRowSpacerOrBoth(targetRowIndex,
                    destination, padding, scrollType);

            if (rowsWereMoved) {
                fireRowVisibilityChangeEvent();

                // schedule updating of the physical indexes
                domSorter.reschedule();
            }
        }

        /**
         * Modifies the proposed top row logical index to fit within the logical
         * range and to not leave gaps if it is avoidable.
         *
         * @param proposedTopRowLogicalIndex
         * @return an adjusted index, or the original if no changes were
         *         necessary
         */
        private int ensureTopRowLogicalIndexSanity(
                int proposedTopRowLogicalIndex) {
            int newTopRowLogicalIndex = Math.max(proposedTopRowLogicalIndex, 0);
            int visualRangeLength = visualRowOrder.size();
            if (newTopRowLogicalIndex + visualRangeLength > getRowCount()) {
                newTopRowLogicalIndex = getRowCount() - visualRangeLength;
            }
            return newTopRowLogicalIndex;
        }

        /**
         * Checks that scrolling is allowed and resets the scroll position if
         * it's not.
         *
         * @return {@code true} if scrolling is allowed, {@code false} otherwise
         */
        private boolean ensureScrollingAllowed() {
            if (isScrollLocked(Direction.VERTICAL)) {
                // no scrolling can happen
                if (getScrollTop() != tBodyScrollTop) {
                    setBodyScrollPosition(tBodyScrollLeft, getScrollTop());
                }
                return false;
            }
            return true;
        }

        /**
         * Adjusts visual range up for
         * {@link #scrollToRowSpacerOrBoth(int, ScrollDestination, double, boolean, boolean)},
         * reuse at your own peril.
         *
         * @param oldTopRowLogicalIndex
         * @param visualRangeLength
         * @param logicalTargetIndex
         */
        private void adjustVisualRangeUpForScrollToRowSpacerOrBoth(
                int oldTopRowLogicalIndex, int visualRangeLength,
                int logicalTargetIndex) {
            // recycle at most the visual range's worth of rows to fill
            // the gap between the new visualTargetIndex and the existing
            // rows
            int rowsToRecycle = Math.min(
                    oldTopRowLogicalIndex - logicalTargetIndex,
                    visualRangeLength);
            // recycle from the end to the beginning
            moveAndUpdateEscalatorRows(
                    Range.withLength(visualRangeLength - rowsToRecycle,
                            rowsToRecycle),
                    0, logicalTargetIndex);
            // update the index
            setTopRowLogicalIndex(logicalTargetIndex);
        }

        /**
         * Adjusts visual range down for
         * {@link #scrollToRowSpacerOrBoth(int, ScrollDestination, double, boolean, boolean)},
         * reuse at your own peril.
         *
         * @param oldTopRowLogicalIndex
         * @param visualRangeLength
         * @param newTopRowLogicalIndex
         * @param logicalTargetIndex
         */
        private void adjustVisualRangeDownForScrollToRowSpacerOrBoth(
                int oldTopRowLogicalIndex, int visualRangeLength,
                int newTopRowLogicalIndex, int logicalTargetIndex) {
            // recycle at most the visual range's worth of rows to fill
            // the gap between the new visualTargetIndex and the existing
            // rows
            int rowsToRecycle;
            if (newTopRowLogicalIndex
                    - oldTopRowLogicalIndex >= visualRangeLength) {
                // full recycling
                rowsToRecycle = visualRangeLength;
            } else {
                // partial recycling
                rowsToRecycle = newTopRowLogicalIndex - oldTopRowLogicalIndex;
            }
            // recycle from the beginning to the end
            moveAndUpdateEscalatorRows(Range.withLength(0, rowsToRecycle),
                    visualRangeLength, logicalTargetIndex);
            // update the index
            setTopRowLogicalIndex(newTopRowLogicalIndex);
        }

        /**
         * Adjusts scroll position for
         * {@link #scrollToRowSpacerOrBoth(int, ScrollDestination, double, boolean, boolean)},
         * reuse at your own peril.
         *
         * @param targetRowIndex
         * @param destination
         * @param padding
         * @param scrollType
         */
        private void adjustScrollPositionForScrollToRowSpacerOrBoth(
                int targetRowIndex, ScrollDestination destination,
                double padding, ScrollType scrollType) {
            /*
             * attempting to scroll above first row or below last row would get
             * automatically corrected later but that causes unnecessary
             * calculations, so try not to overshoot
             */
            double sectionHeight = getHeightOfSection();
            double rowTop = getRowTop(targetRowIndex);
            double spacerHeight = spacerContainer
                    .getSpacerHeight(targetRowIndex);

            double scrollTop;
            switch (destination) {
            case ANY:
                if (!ScrollType.SPACER.equals(scrollType)
                        && Math.max(rowTop - padding, 0) < getScrollTop()) {
                    // within visual range but row top above the viewport or not
                    // enough padding, shift a little
                    scrollTop = Math.max(rowTop - padding, 0);
                } else if (ScrollType.SPACER.equals(scrollType)
                        && Math.max(rowTop + getDefaultRowHeight() - padding,
                                0) < getScrollTop()) {
                    // within visual range but spacer top above the viewport or
                    // not enough padding, shift a little
                    scrollTop = Math
                            .max(rowTop + getDefaultRowHeight() - padding, 0);
                } else if (ScrollType.ROW.equals(scrollType)
                        && rowTop + getDefaultRowHeight()
                                + padding > getScrollTop() + sectionHeight) {
                    // within visual range but end of row below the viewport
                    // or not enough padding, shift a little
                    scrollTop = rowTop + getDefaultRowHeight() - sectionHeight
                            + padding;
                    // ensure that we don't overshoot beyond bottom
                    scrollTop = Math.min(scrollTop,
                            getRowTop(getRowCount() - 1) + getDefaultRowHeight()
                                    + spacerContainer
                                            .getSpacerHeight(getRowCount() - 1)
                                    - sectionHeight);
                    // if padding is set we want to overshoot or undershoot,
                    // otherwise make sure the top of the row is in view
                    if (padding == 0) {
                        scrollTop = Math.min(scrollTop, rowTop);
                    }
                } else if (rowTop + getDefaultRowHeight() + spacerHeight
                        + padding > getScrollTop() + sectionHeight) {
                    // within visual range but end of spacer below the viewport
                    // or not enough padding, shift a little
                    scrollTop = rowTop + getDefaultRowHeight() + spacerHeight
                            - sectionHeight + padding;
                    // ensure that we don't overshoot beyond bottom
                    scrollTop = Math.min(scrollTop,
                            getRowTop(getRowCount()) - sectionHeight);
                    // if padding is set we want to overshoot or undershoot,
                    // otherwise make sure the top of the row or spacer is
                    // in view
                    if (padding == 0) {
                        if (ScrollType.SPACER.equals(scrollType)) {
                            scrollTop = Math.min(scrollTop,
                                    rowTop + getDefaultRowHeight());
                        } else {
                            scrollTop = Math.min(scrollTop, rowTop);
                        }
                    }
                } else {
                    // we are fine where we are
                    scrollTop = getScrollTop();
                }
                break;
            case END:
                if (ScrollType.ROW.equals(scrollType)
                        && rowTop + getDefaultRowHeight()
                                + padding != getScrollTop() + sectionHeight) {
                    // row should be at the bottom of the viewport
                    scrollTop = rowTop + getDefaultRowHeight() - sectionHeight
                            + padding;
                } else if (rowTop + getDefaultRowHeight() + spacerHeight
                        + padding != getScrollTop() + sectionHeight) {
                    // spacer should be at the bottom of the viewport
                    scrollTop = rowTop + getDefaultRowHeight() + spacerHeight
                            - sectionHeight + padding;
                } else {
                    // we are fine where we are
                    scrollTop = getScrollTop();
                }
                break;
            case MIDDLE:
                double center;
                if (ScrollType.ROW.equals(scrollType)) {
                    // center the row itself
                    center = rowTop + (getDefaultRowHeight() / 2.0);
                } else if (ScrollType.ROW_AND_SPACER.equals(scrollType)) {
                    // center both
                    center = rowTop
                            + ((getDefaultRowHeight() + spacerHeight) / 2.0);
                } else {
                    // center the spacer
                    center = rowTop + getDefaultRowHeight()
                            + (spacerHeight / 2.0);
                }
                scrollTop = center - Math.ceil(sectionHeight / 2.0);
                break;
            case START:
                if (!ScrollType.SPACER.equals(scrollType)
                        && Math.max(rowTop - padding, 0) != getScrollTop()) {
                    // row should be at the top of the viewport
                    scrollTop = Math.max(rowTop - padding, 0);
                } else if (ScrollType.SPACER.equals(scrollType)
                        && Math.max(rowTop + getDefaultRowHeight() - padding,
                                0) != getScrollTop()) {
                    // spacer should be at the top of the viewport
                    scrollTop = Math
                            .max(rowTop + getDefaultRowHeight() - padding, 0);
                } else {
                    scrollTop = getScrollTop();
                }
                break;
            default:
                scrollTop = getScrollTop();
            }
            // ensure that we don't overshoot beyond bottom
            scrollTop = Math.min(scrollTop,
                    getRowTop(getRowCount()) - sectionHeight);
            // ensure that we don't overshoot beyond top
            scrollTop = Math.max(0, scrollTop);

            if (scrollTop != getScrollTop()) {
                setScrollTop(scrollTop);
                setBodyScrollPosition(tBodyScrollLeft, scrollTop);
            }
        }

        @Override
        public void setNewRowCallback(
                Consumer<List<TableRowElement>> callback) {
            newEscalatorRowCallback = callback;
        }
    }

    private class ColumnConfigurationImpl implements ColumnConfiguration {
        public class Column {
            public static final double DEFAULT_COLUMN_WIDTH_PX = 100;

            private double definedWidth = -1;
            private double calculatedWidth = DEFAULT_COLUMN_WIDTH_PX;
            private boolean measuringRequested = false;

            public void setWidth(double px) {
                Profiler.enter(
                        "Escalator.ColumnConfigurationImpl.Column.setWidth");

                definedWidth = px;

                if (px < 0) {
                    if (isAttached()) {
                        calculateWidth();
                    } else {
                        /*
                         * the column's width is calculated at Escalator.onLoad
                         * via measureAndSetWidthIfNeeded!
                         */
                        measuringRequested = true;
                    }
                } else {
                    calculatedWidth = px;
                }

                Profiler.leave(
                        "Escalator.ColumnConfigurationImpl.Column.setWidth");
            }

            public double getDefinedWidth() {
                return definedWidth;
            }

            /**
             * Returns the actual width in the DOM.
             *
             * @return the width in pixels in the DOM. Returns -1 if the column
             *         needs measuring, but has not been yet measured
             */
            public double getCalculatedWidth() {
                /*
                 * This might return an untrue value (e.g. during init/onload),
                 * since we haven't had a proper chance to actually calculate
                 * widths yet.
                 *
                 * This is fixed during Escalator.onLoad, by the call to
                 * "measureAndSetWidthIfNeeded", which fixes "everything".
                 */
                if (!measuringRequested) {
                    return calculatedWidth;
                } else {
                    return -1;
                }
            }

            /**
             * Checks if the column needs measuring, and then measures it.
             * <p>
             * Called by {@link Escalator#onLoad()}.
             */
            public boolean measureAndSetWidthIfNeeded() {
                assert isAttached() : "Column.measureAndSetWidthIfNeeded() was called even though Escalator was not attached!";

                if (measuringRequested) {
                    measuringRequested = false;
                    setWidth(definedWidth);
                    return true;
                }
                return false;
            }

            private void calculateWidth() {
                calculatedWidth = getMaxCellWidth(columns.indexOf(this));
            }
        }

        private final List<Column> columns = new ArrayList<>();
        private int frozenColumns = 0;

        /*
         * TODO: this is a bit of a duplicate functionality with the
         * Column.calculatedWidth caching. Probably should use one or the other,
         * not both
         */
        /**
         * A cached array of all the calculated column widths.
         *
         * @see #getCalculatedColumnWidths()
         */
        private double[] widthsArray = null;

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there are no rows in the DOM when
         * this method is called.
         *
         * @see #hasSomethingInDom()
         */
        @Override
        public void removeColumns(final int index, final int numberOfColumns) {
            if (numberOfColumns == 0) {
                return;
            }

            // Validate
            assertArgumentsAreValidAndWithinRange(index, numberOfColumns);

            // Move the horizontal scrollbar to the left, if removed columns are
            // to the left of the viewport
            removeColumnsAdjustScrollbar(index, numberOfColumns);

            // Remove from DOM
            header.paintRemoveColumns(index, numberOfColumns);
            body.paintRemoveColumns(index, numberOfColumns);
            footer.paintRemoveColumns(index, numberOfColumns);

            // Remove from bookkeeping
            flyweightRow.removeCells(index, numberOfColumns);
            columns.subList(index, index + numberOfColumns).clear();

            // Adjust frozen columns
            if (index < getFrozenColumnCount()) {
                if (index + numberOfColumns < frozenColumns) {
                    /*
                     * Last removed column was frozen, meaning that all removed
                     * columns were frozen. Just decrement the number of frozen
                     * columns accordingly.
                     */
                    frozenColumns -= numberOfColumns;
                } else {
                    /*
                     * If last removed column was not frozen, we have removed
                     * columns beyond the frozen range, so all remaining frozen
                     * columns are to the left of the removed columns.
                     */
                    frozenColumns = index;
                }
            }

            scroller.recalculateScrollbarsForVirtualViewport();
            body.verifyEscalatorCount();

            if (getColumnConfiguration().getColumnCount() > 0) {
                reapplyRowWidths(header);
                reapplyRowWidths(body);
                reapplyRowWidths(footer);
            }

            /*
             * Colspans make any kind of automatic clever content re-rendering
             * impossible: As soon as anything has colspans, removing one might
             * reveal further colspans, modifying the DOM structure once again,
             * ending in a cascade of updates. Because we don't know how the
             * data is updated.
             *
             * So, instead, we don't do anything. The client code is responsible
             * for re-rendering the content (if so desired). Everything Just
             * Works (TM) if colspans aren't used.
             */
        }

        private void reapplyRowWidths(AbstractRowContainer container) {
            if (container.getRowCount() > 0) {
                container.reapplyRowWidths();
            }
        }

        private void removeColumnsAdjustScrollbar(int index,
                int numberOfColumns) {
            if (horizontalScrollbar.getOffsetSize() >= horizontalScrollbar
                    .getScrollSize()) {
                return;
            }

            double leftPosOfFirstColumnToRemove = getCalculatedColumnsWidth(
                    Range.between(0, index));
            double widthOfColumnsToRemove = getCalculatedColumnsWidth(
                    Range.withLength(index, numberOfColumns));

            double scrollLeft = horizontalScrollbar.getScrollPos();

            if (scrollLeft <= leftPosOfFirstColumnToRemove) {
                /*
                 * viewport is scrolled to the left of the first removed column,
                 * so there's no need to adjust anything
                 */
                return;
            }

            double adjustedScrollLeft = Math.max(leftPosOfFirstColumnToRemove,
                    scrollLeft - widthOfColumnsToRemove);
            horizontalScrollbar.setScrollPos(adjustedScrollLeft);
        }

        /**
         * Calculate the width of a row, as the sum of columns' widths.
         *
         * @return the width of a row, in pixels
         */
        public double calculateRowWidth() {
            return getCalculatedColumnsWidth(
                    Range.between(0, getColumnCount()));
        }

        private void assertArgumentsAreValidAndWithinRange(final int index,
                final int numberOfColumns) {
            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns can't be less than 1 (was "
                                + numberOfColumns + ")");
            }

            if (index < 0 || index + numberOfColumns > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "column range (" + index + ".."
                        + (index + numberOfColumns)
                        + ") was outside of the current "
                        + "number of columns (" + getColumnCount() + ")");
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * <em>Implementation detail:</em> This method does no DOM modifications
         * (i.e. is very cheap to call) if there is no data for rows when this
         * method is called.
         *
         * @see #hasColumnAndRowData()
         */
        @Override
        public void insertColumns(final int index, final int numberOfColumns) {
            if (numberOfColumns == 0) {
                return;
            }

            // Validate
            if (index < 0 || index > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given index(" + index
                        + ") was outside of the current number of columns (0.."
                        + getColumnCount() + ")");
            }

            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns must be 1 or greater (was "
                                + numberOfColumns);
            }

            // Add to bookkeeping
            flyweightRow.addCells(index, numberOfColumns);
            for (int i = 0; i < numberOfColumns; i++) {
                columns.add(index, new Column());
            }

            // Adjust frozen columns
            boolean frozen = index < frozenColumns;
            if (frozen) {
                frozenColumns += numberOfColumns;
            }

            // Add to DOM
            header.paintInsertColumns(index, numberOfColumns, frozen);
            body.paintInsertColumns(index, numberOfColumns, frozen);
            footer.paintInsertColumns(index, numberOfColumns, frozen);

            // this needs to be before the scrollbar adjustment.
            boolean scrollbarWasNeeded = horizontalScrollbar
                    .getOffsetSize() < horizontalScrollbar.getScrollSize();
            scroller.recalculateScrollbarsForVirtualViewport();
            boolean scrollbarIsNowNeeded = horizontalScrollbar
                    .getOffsetSize() < horizontalScrollbar.getScrollSize();
            if (!scrollbarWasNeeded && scrollbarIsNowNeeded) {
                // This might as a side effect move rows around (when scrolled
                // all the way down) and require the DOM to be up to date, i.e.
                // the column to be added
                body.verifyEscalatorCount();
            }

            // fix initial width
            if (header.getRowCount() > 0 || body.getRowCount() > 0
                    || footer.getRowCount() > 0) {

                Map<Integer, Double> colWidths = new HashMap<>();
                Double width = Double.valueOf(Column.DEFAULT_COLUMN_WIDTH_PX);
                for (int i = index; i < index + numberOfColumns; i++) {
                    Integer col = Integer.valueOf(i);
                    colWidths.put(col, width);
                }
                getColumnConfiguration().setColumnWidths(colWidths);
            }

            // Adjust scrollbar
            double pixelsToInsertedColumn = columnConfiguration
                    .getCalculatedColumnsWidth(Range.withLength(0, index));
            final boolean columnsWereAddedToTheLeftOfViewport = scroller.lastScrollLeft > pixelsToInsertedColumn;

            if (columnsWereAddedToTheLeftOfViewport) {
                double insertedColumnsWidth = columnConfiguration
                        .getCalculatedColumnsWidth(
                                Range.withLength(index, numberOfColumns));
                horizontalScrollbar.setScrollPos(
                        scroller.lastScrollLeft + insertedColumnsWidth);
            }

            /*
             * Colspans make any kind of automatic clever content re-rendering
             * impossible: As soon as anything has colspans, adding one might
             * affect surrounding colspans, modifying the DOM structure once
             * again, ending in a cascade of updates. Because we don't know how
             * the data is updated.
             *
             * So, instead, we don't do anything. The client code is responsible
             * for re-rendering the content (if so desired). Everything Just
             * Works (TM) if colspans aren't used.
             */
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public void setFrozenColumnCount(int count)
                throws IllegalArgumentException {
            if (count < 0 || count > getColumnCount()) {
                throw new IllegalArgumentException(
                        "count must be between 0 and the current number of columns ("
                                + getColumnCount() + ")");
            }
            int oldCount = frozenColumns;
            if (count == oldCount) {
                return;
            }

            frozenColumns = count;

            if (hasSomethingInDom()) {
                // Are we freezing or unfreezing?
                boolean frozen = count > oldCount;

                int firstAffectedCol;
                int firstUnaffectedCol;

                if (frozen) {
                    firstAffectedCol = oldCount;
                    firstUnaffectedCol = count;
                } else {
                    firstAffectedCol = count;
                    firstUnaffectedCol = oldCount;
                }

                if (oldCount > 0) {
                    header.setColumnLastFrozen(oldCount - 1, false);
                    body.setColumnLastFrozen(oldCount - 1, false);
                    footer.setColumnLastFrozen(oldCount - 1, false);
                }
                if (count > 0) {
                    header.setColumnLastFrozen(count - 1, true);
                    body.setColumnLastFrozen(count - 1, true);
                    footer.setColumnLastFrozen(count - 1, true);
                }

                for (int col = firstAffectedCol; col < firstUnaffectedCol; col++) {
                    header.setColumnFrozen(col, frozen);
                    body.setColumnFrozen(col, frozen);
                    footer.setColumnFrozen(col, frozen);
                }
            }

            scroller.recalculateScrollbarsForVirtualViewport();
        }

        @Override
        public int getFrozenColumnCount() {
            return frozenColumns;
        }

        @Override
        public void setColumnWidth(int index, double px)
                throws IllegalArgumentException {
            setColumnWidths(Collections.singletonMap(Integer.valueOf(index),
                    Double.valueOf(px)));
        }

        @Override
        public void setColumnWidths(Map<Integer, Double> indexWidthMap)
                throws IllegalArgumentException {

            if (indexWidthMap == null) {
                throw new IllegalArgumentException("indexWidthMap was null");
            }

            if (indexWidthMap.isEmpty()) {
                return;
            }

            Profiler.enter("Escalator.ColumnConfigurationImpl.setColumnWidths");
            try {

                for (Entry<Integer, Double> entry : indexWidthMap.entrySet()) {
                    int index = entry.getKey().intValue();
                    double width = entry.getValue().doubleValue();

                    checkValidColumnIndex(index);

                    // Not all browsers will accept any fractional size..
                    width = WidgetUtil.roundSizeDown(width);
                    columns.get(index).setWidth(width);

                }

                widthsArray = null;
                header.reapplyColumnWidths();
                body.reapplyColumnWidths();
                footer.reapplyColumnWidths();

                recalculateElementSizes();

            } finally {
                Profiler.leave(
                        "Escalator.ColumnConfigurationImpl.setColumnWidths");
            }
        }

        private void checkValidColumnIndex(int index)
                throws IllegalArgumentException {
            if (!Range.withLength(0, getColumnCount()).contains(index)) {
                throw new IllegalArgumentException("The given column index ("
                        + index + ") does not exist");
            }
        }

        @Override
        public double getColumnWidth(int index)
                throws IllegalArgumentException {
            checkValidColumnIndex(index);
            return columns.get(index).getDefinedWidth();
        }

        @Override
        public double getColumnWidthActual(int index) {
            return columns.get(index).getCalculatedWidth();
        }

        private double getMaxCellWidth(int colIndex)
                throws IllegalArgumentException {
            double headerWidth = header.measureMinCellWidth(colIndex, true);
            double bodyWidth = body.measureMinCellWidth(colIndex, true);
            double footerWidth = footer.measureMinCellWidth(colIndex, true);

            double maxWidth = Math.max(headerWidth,
                    Math.max(bodyWidth, footerWidth));
            if (maxWidth < 0 && header.getRowCount() == 0
                    && body.getRowCount() == 0 && footer.getRowCount() == 0) {
                maxWidth = 0;
            }
            assert maxWidth >= 0 : "Got a negative max width for a column, which should be impossible.";
            return maxWidth;
        }

        private double getMinCellWidth(int colIndex)
                throws IllegalArgumentException {
            double headerWidth = header.measureMinCellWidth(colIndex, false);
            double bodyWidth = body.measureMinCellWidth(colIndex, false);
            double footerWidth = footer.measureMinCellWidth(colIndex, false);

            double minWidth = Math.max(headerWidth,
                    Math.max(bodyWidth, footerWidth));
            if (minWidth < 0 && header.getRowCount() == 0
                    && body.getRowCount() == 0 && footer.getRowCount() == 0) {
                minWidth = 0;
            }
            assert minWidth >= 0 : "Got a negative min width for a column, which should be impossible.";
            return minWidth;
        }

        /**
         * Calculates the width of the columns in a given range.
         *
         * @param columns
         *            the columns to calculate
         * @return the total width of the columns in the given
         *         <code>columns</code>
         */
        double getCalculatedColumnsWidth(final Range columns) {
            /*
             * This is an assert instead of an exception, since this is an
             * internal method.
             */
            assert columns
                    .isSubsetOf(Range.between(0, getColumnCount())) : "Range "
                            + "was outside of current column range (i.e.: "
                            + Range.between(0, getColumnCount())
                            + ", but was given :" + columns;

            double sum = 0;
            for (int i = columns.getStart(); i < columns.getEnd(); i++) {
                double columnWidthActual = getColumnWidthActual(i);
                sum += columnWidthActual;
            }
            return sum;
        }

        double[] getCalculatedColumnWidths() {
            if (widthsArray == null || widthsArray.length != getColumnCount()) {
                widthsArray = new double[getColumnCount()];
                for (int i = 0; i < columns.size(); i++) {
                    widthsArray[i] = columns.get(i).getCalculatedWidth();
                }
            }
            return widthsArray;
        }

        @Override
        public void refreshColumns(int index, int numberOfColumns)
                throws IndexOutOfBoundsException, IllegalArgumentException {
            if (numberOfColumns < 1) {
                throw new IllegalArgumentException(
                        "Number of columns must be 1 or greater (was "
                                + numberOfColumns + ")");
            }

            if (index < 0 || index + numberOfColumns > getColumnCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "column range (" + index + ".."
                        + (index + numberOfColumns)
                        + ") was outside of the current number of columns ("
                        + getColumnCount() + ")");
            }

            header.refreshColumns(index, numberOfColumns);
            body.refreshColumns(index, numberOfColumns);
            footer.refreshColumns(index, numberOfColumns);
        }
    }

    /**
     * A decision on how to measure a spacer when it is partially within a
     * designated range.
     * <p>
     * The meaning of each value may differ depending on the context it is being
     * used in. Check that particular method's JavaDoc.
     */
    private enum SpacerInclusionStrategy {
        /** A representation of "the entire spacer". */
        COMPLETE,

        /** A representation of "a partial spacer". */
        PARTIAL,

        /** A representation of "no spacer at all". */
        NONE
    }

    private class SpacerContainer {

        /** This is used mainly for testing purposes */
        private static final String SPACER_LOGICAL_ROW_PROPERTY = "vLogicalRow";

        private final class SpacerImpl implements Spacer {
            private TableCellElement spacerElement;
            private TableRowElement root;
            private DivElement deco;
            private int rowIndex;
            private double height = -1;
            private boolean domHasBeenSetup = false;
            private double decoHeight;
            private double defaultCellBorderBottomSize = -1;

            public SpacerImpl(int rowIndex) {
                this.rowIndex = rowIndex;

                root = TableRowElement.as(DOM.createTR());
                spacerElement = TableCellElement.as(DOM.createTD());
                root.appendChild(spacerElement);
                root.setPropertyInt(SPACER_LOGICAL_ROW_PROPERTY, rowIndex);
                deco = DivElement.as(DOM.createDiv());
            }

            public void setPositionDiff(double x, double y) {
                setPosition(getLeft() + x, getTop() + y);
            }

            public void setupDom(double height) {
                assert !domHasBeenSetup : "DOM can't be set up twice.";
                assert RootPanel.get().getElement().isOrHasChild(
                        root) : "Root element should've been attached to the DOM by now.";
                domHasBeenSetup = true;

                getRootElement().getStyle().setWidth(getInnerWidth(), Unit.PX);
                setHeight(height);

                spacerElement
                        .setColSpan(getColumnConfiguration().getColumnCount());

                setStylePrimaryName(getStylePrimaryName());
            }

            public TableRowElement getRootElement() {
                return root;
            }

            @Override
            public Element getDecoElement() {
                return deco;
            }

            public void setPosition(double x, double y) {
                positions.set(getRootElement(), x, y);
                positions.set(getDecoElement(), 0,
                        y - getSpacerDecoTopOffset());
            }

            private double getSpacerDecoTopOffset() {
                return getBody().getDefaultRowHeight();
            }

            public void setStylePrimaryName(String style) {
                UIObject.setStylePrimaryName(root, style + "-spacer");
                UIObject.setStylePrimaryName(deco, style + "-spacer-deco");
            }

            /**
             * Clear spacer height without moving other contents.
             *
             * @see #setHeight(double)
             */
            private void clearHeight() {
                height = 0;
                root.getStyle().setHeight(0, Unit.PX);
                updateDecoratorGeometry(0);
            }

            public void setHeight(double height) {

                assert height >= 0 : "Height must be more >= 0 (was " + height
                        + ")";

                final double heightDiff = height - Math.max(0, this.height);
                final double oldHeight = this.height;

                this.height = height;

                // since the spacer might be rendered on top of the previous
                // rows border (done with css), need to increase height the
                // amount of the border thickness
                if (defaultCellBorderBottomSize < 0) {
                    defaultCellBorderBottomSize = WidgetUtil
                            .getBorderBottomThickness(body
                                    .getRowElement(
                                            getVisibleRowRange().getStart())
                                    .getFirstChildElement());
                }
                root.getStyle().setHeight(height + defaultCellBorderBottomSize,
                        Unit.PX);

                // move the visible spacers getRow row onwards.
                shiftSpacerPositionsAfterRow(getRow(), heightDiff);

                /*
                 * If we're growing, we'll adjust the scroll size first, then
                 * adjust scrolling. If we're shrinking, we do it after the
                 * second if-clause.
                 */
                boolean spacerIsGrowing = heightDiff > 0;
                if (spacerIsGrowing) {
                    verticalScrollbar.setScrollSize(
                            verticalScrollbar.getScrollSize() + heightDiff);
                }

                /*
                 * Don't modify the scrollbars if we're expanding the -1 spacer
                 * while we're scrolled to the top.
                 */
                boolean minusOneSpacerException = spacerIsGrowing
                        && getRow() == -1 && body.getTopRowLogicalIndex() == 0;

                boolean viewportNeedsScrolling = getRow() < body
                        .getTopRowLogicalIndex() && !minusOneSpacerException;
                if (viewportNeedsScrolling) {

                    /*
                     * We can't use adjustScrollPos here, probably because of a
                     * bookkeeping-related race condition.
                     *
                     * This particular situation is easier, however, since we
                     * know exactly how many pixels we need to move (heightDiff)
                     * and all elements below the spacer always need to move
                     * that pixel amount.
                     */

                    for (TableRowElement row : body.visualRowOrder) {
                        body.setRowPosition(row, 0,
                                body.getRowTop(row) + heightDiff);
                    }

                    double top = getTop();
                    double bottom = top + oldHeight;
                    double scrollTop = verticalScrollbar.getScrollPos();

                    boolean viewportTopIsAtMidSpacer = top < scrollTop
                            && scrollTop < bottom;

                    final double moveDiff;
                    if (viewportTopIsAtMidSpacer && !spacerIsGrowing) {

                        /*
                         * If the scroll top is in the middle of the modified
                         * spacer, we want to scroll the viewport up as usual,
                         * but we don't want to scroll past the top of it.
                         *
                         * Math.max ensures this (remember: the result is going
                         * to be negative).
                         */

                        moveDiff = Math.max(heightDiff, top - scrollTop);
                    } else {
                        moveDiff = heightDiff;
                    }
                    body.setBodyScrollPosition(tBodyScrollLeft,
                            tBodyScrollTop + moveDiff);
                    verticalScrollbar.setScrollPosByDelta(moveDiff);

                } else {
                    body.shiftRowPositions(getRow(), heightDiff);
                }

                if (!spacerIsGrowing) {
                    verticalScrollbar.setScrollSize(
                            verticalScrollbar.getScrollSize() + heightDiff);
                }

                updateDecoratorGeometry(height);
            }

            /** Resizes and places the decorator. */
            private void updateDecoratorGeometry(double detailsHeight) {
                Style style = deco.getStyle();
                decoHeight = detailsHeight + getBody().getDefaultRowHeight();
                style.setHeight(decoHeight, Unit.PX);
            }

            @Override
            public Element getElement() {
                return spacerElement;
            }

            @Override
            public int getRow() {
                return rowIndex;
            }

            public double getHeight() {
                assert height >= 0 : "Height was not previously set by setHeight.";
                return height;
            }

            public double getTop() {
                return positions.getTop(getRootElement());
            }

            public double getLeft() {
                return positions.getLeft(getRootElement());
            }

            /**
             * Sets a new row index for this spacer. Also updates the
             * bookkeeping at {@link SpacerContainer#rowIndexToSpacer}.
             */
            @SuppressWarnings("boxing")
            public void setRowIndex(int rowIndex) {
                SpacerImpl spacer = rowIndexToSpacer.remove(this.rowIndex);
                assert this == spacer : "trying to move an unexpected spacer.";
                int oldIndex = this.rowIndex;
                this.rowIndex = rowIndex;
                root.setPropertyInt(SPACER_LOGICAL_ROW_PROPERTY, rowIndex);
                rowIndexToSpacer.put(this.rowIndex, this);

                fireEvent(new SpacerIndexChangedEvent(oldIndex, this.rowIndex));
            }

            /**
             * Updates the spacer's visibility parameters, based on whether it
             * is being currently visible or not.
             *
             * @deprecated Escalator no longer uses this logic at initialisation
             *             as there can only be a limited number of spacers and
             *             hidden spacers within visual range interfere with
             *             position calculations.
             */
            @Deprecated
            public void updateVisibility() {
                if (isInViewport()) {
                    show();
                } else {
                    hide();
                }
            }

            private boolean isInViewport() {
                int top = (int) Math.ceil(getTop());
                int height = (int) Math.floor(getHeight());
                Range location = Range.withLength(top, height);
                return getViewportPixels().intersects(location);
            }

            public void show() {
                getRootElement().getStyle().clearDisplay();
                getDecoElement().getStyle().clearDisplay();
                fireEvent(new SpacerVisibilityChangedEvent(getRow(), true));
            }

            public void hide() {
                getRootElement().getStyle().setDisplay(Display.NONE);
                getDecoElement().getStyle().setDisplay(Display.NONE);
                fireEvent(new SpacerVisibilityChangedEvent(getRow(), false));
            }

            /**
             * Crop the decorator element so that it doesn't overlap the header
             * and footer sections.
             *
             * @param bodyTop
             *            the top cordinate of the escalator body
             * @param bodyBottom
             *            the bottom cordinate of the escalator body
             * @param decoWidth
             *            width of the deco
             */
            private void updateDecoClip(final double bodyTop,
                    final double bodyBottom, final double decoWidth) {
                final int top = deco.getAbsoluteTop();
                final int bottom = deco.getAbsoluteBottom();
                /*
                 * FIXME
                 *
                 * Height and its use is a workaround for the issue where
                 * coordinates of the deco are not calculated yet. This will
                 * prevent a deco from being displayed when it's added to DOM
                 */
                final int height = bottom - top;
                if (top < bodyTop || bottom > bodyBottom) {
                    final double topClip = Math.max(0.0D, bodyTop - top);
                    final double bottomClip = height
                            - Math.max(0.0D, bottom - bodyBottom);
                    // TODO [optimize] not sure how GWT compiles this
                    final String clip = new StringBuilder("rect(")
                            .append(topClip).append("px,").append(decoWidth)
                            .append("px,").append(bottomClip).append("px,0)")
                            .toString();
                    deco.getStyle().setProperty("clip", clip);
                } else {
                    deco.getStyle().setProperty("clip", "auto");
                }
            }
        }

        private final TreeMap<Integer, SpacerImpl> rowIndexToSpacer = new TreeMap<>();

        private SpacerUpdater spacerUpdater = SpacerUpdater.NULL;

        private final ScrollHandler spacerScroller = new ScrollHandler() {
            private double prevScrollX = 0;

            @Override
            public void onScroll(ScrollEvent event) {
                if (WidgetUtil.pixelValuesEqual(getScrollLeft(), prevScrollX)) {
                    return;
                }

                prevScrollX = getScrollLeft();
                for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                    spacer.setPosition(prevScrollX, spacer.getTop());
                }
            }
        };
        private HandlerRegistration spacerScrollerRegistration;

        /** Width of the spacers' decos. Calculated once then cached. */
        private double spacerDecoWidth = 0.0D;

        public void setSpacer(int rowIndex, double height)
                throws IllegalArgumentException {

            if (rowIndex < -1 || rowIndex >= getBody().getRowCount()) {
                throw new IllegalArgumentException("invalid row index: "
                        + rowIndex + ", while the body only has "
                        + getBody().getRowCount() + " rows.");
            }

            if (height >= 0) {
                if (!spacerExists(rowIndex)) {
                    insertNewSpacer(rowIndex, height);
                } else {
                    updateExistingSpacer(rowIndex, height);
                }
            } else if (spacerExists(rowIndex)) {
                removeSpacer(rowIndex);
            }

            updateSpacerDecosVisibility();
        }

        /** Checks if a given element is a spacer element */
        public boolean isSpacer(Element row) {

            /*
             * If this needs optimization, we could do a more heuristic check
             * based on stylenames and stuff, instead of iterating through the
             * map.
             */

            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                if (spacer.getRootElement().equals(row)) {
                    return true;
                }
            }

            return false;
        }

        @SuppressWarnings("boxing")
        void scrollToSpacer(int spacerIndex, ScrollDestination destination,
                int padding) {

            assert !destination.equals(ScrollDestination.MIDDLE)
                    || padding != 0 : "destination/padding check should be done before this method";

            body.scrollToRowSpacerOrBoth(spacerIndex, destination, padding,
                    ScrollType.SPACER);
        }

        public void reapplySpacerWidths() {
            // FIXME #16266 , spacers get couple pixels too much because borders
            final double width = getInnerWidth() - spacerDecoWidth;
            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                spacer.getRootElement().getStyle().setWidth(width, Unit.PX);
            }
        }

        /**
         * @deprecated This method is no longer used by Escalator and is likely
         *             to be removed soon.
         *
         * @param removedRowsRange
         */
        @Deprecated
        public void paintRemoveSpacers(Range removedRowsRange) {
            removeSpacers(removedRowsRange);
            shiftSpacersByRows(removedRowsRange.getStart(),
                    -removedRowsRange.length());
        }

        /**
         * Removes spacers of the given range without moving other contents.
         * <p>
         * NOTE: Changed functionality since 8.9. Previous incarnation of this
         * method updated the positions of all the contents below the first
         * removed spacer.
         *
         * @param removedRange
         *            logical range of spacers to remove
         */
        @SuppressWarnings("boxing")
        public void removeSpacers(Range removedRange) {

            Map<Integer, SpacerImpl> removedSpacers = rowIndexToSpacer.subMap(
                    removedRange.getStart(), true, removedRange.getEnd(),
                    false);

            if (removedSpacers.isEmpty()) {
                return;
            }

            double specialSpacerHeight = removedRange.contains(-1)
                    ? getSpacerHeight(-1)
                    : 0;

            for (Entry<Integer, SpacerImpl> entry : removedSpacers.entrySet()) {
                SpacerImpl spacer = entry.getValue();

                rowIndexToSpacer.remove(entry.getKey());
                destroySpacerContent(spacer);
                spacer.clearHeight();
                spacer.getRootElement().removeFromParent();
                spacer.getDecoElement().removeFromParent();
            }

            removedSpacers.clear();

            if (rowIndexToSpacer.isEmpty()) {
                assert spacerScrollerRegistration != null : "Spacer scroller registration was null";
                spacerScrollerRegistration.removeHandler();
                spacerScrollerRegistration = null;
            }

            // if a rowless spacer at the top got removed, all rows and spacers
            // need to be moved up accordingly
            if (!WidgetUtil.pixelValuesEqual(specialSpacerHeight, 0)) {
                double scrollDiff = Math.min(specialSpacerHeight,
                        getScrollTop());
                body.moveViewportAndContent(null, -specialSpacerHeight,
                        -specialSpacerHeight, -scrollDiff);
            }
        }

        public Map<Integer, SpacerImpl> getSpacers() {
            return new HashMap<>(rowIndexToSpacer);
        }

        /**
         * Calculates the sum of all spacers.
         *
         * @return sum of all spacers, or 0 if no spacers present
         */
        public double getSpacerHeightsSum() {
            return getHeights(rowIndexToSpacer.values());
        }

        /**
         * Calculates the sum of all spacers from one row index onwards.
         *
         * @param logicalRowIndex
         *            the spacer to include as the first calculated spacer
         * @return the sum of all spacers from {@code logicalRowIndex} and
         *         onwards, or 0 if no suitable spacers were found
         */
        @SuppressWarnings("boxing")
        public Collection<SpacerImpl> getSpacersForRowAndAfter(
                int logicalRowIndex) {
            return new ArrayList<>(
                    rowIndexToSpacer.tailMap(logicalRowIndex, true).values());
        }

        /**
         * Get all spacers from one pixel point onwards.
         * <p>
         *
         * In this method, the {@link SpacerInclusionStrategy} has the following
         * meaning when a spacer lies in the middle of either pixel argument:
         * <dl>
         * <dt>{@link SpacerInclusionStrategy#COMPLETE COMPLETE}
         * <dd>include the spacer
         * <dt>{@link SpacerInclusionStrategy#PARTIAL PARTIAL}
         * <dd>include the spacer
         * <dt>{@link SpacerInclusionStrategy#NONE NONE}
         * <dd>ignore the spacer
         * </dl>
         *
         * @param px
         *            the pixel point after which to return all spacers
         * @param strategy
         *            the inclusion strategy regarding the {@code px}
         * @return a collection of the spacers that exist after {@code px}
         */
        public Collection<SpacerImpl> getSpacersAfterPx(final double px,
                final SpacerInclusionStrategy strategy) {

            List<SpacerImpl> spacers = new ArrayList<>(
                    rowIndexToSpacer.values());

            for (int i = 0; i < spacers.size(); i++) {
                SpacerImpl spacer = spacers.get(i);

                double top = spacer.getTop();
                double bottom = top + spacer.getHeight();

                if (top > px) {
                    return spacers.subList(i, spacers.size());
                } else if (bottom > px) {
                    if (strategy == SpacerInclusionStrategy.NONE) {
                        return spacers.subList(i + 1, spacers.size());
                    } else {
                        return spacers.subList(i, spacers.size());
                    }
                }
            }

            return Collections.emptySet();
        }

        /**
         * Gets the spacers currently rendered in the DOM.
         *
         * @return an unmodifiable (but live) collection of the spacers
         *         currently in the DOM
         */
        public Collection<SpacerImpl> getSpacersInDom() {
            return Collections
                    .unmodifiableCollection(rowIndexToSpacer.values());
        }

        /**
         * Gets the amount of pixels occupied by spacers between two pixel
         * points.
         * <p>
         * In this method, the {@link SpacerInclusionStrategy} has the following
         * meaning when a spacer lies in the middle of either pixel argument:
         * <dl>
         * <dt>{@link SpacerInclusionStrategy#COMPLETE COMPLETE}
         * <dd>take the entire spacer into account
         * <dt>{@link SpacerInclusionStrategy#PARTIAL PARTIAL}
         * <dd>take only the visible area into account
         * <dt>{@link SpacerInclusionStrategy#NONE NONE}
         * <dd>ignore that spacer
         * </dl>
         *
         * @param rangeTop
         *            the top pixel point
         * @param topInclusion
         *            the inclusion strategy regarding {@code rangeTop}.
         * @param rangeBottom
         *            the bottom pixel point
         * @param bottomInclusion
         *            the inclusion strategy regarding {@code rangeBottom}.
         * @return the pixels occupied by spacers between {@code rangeTop} and
         *         {@code rangeBottom}
         */
        public double getSpacerHeightsSumBetweenPx(double rangeTop,
                SpacerInclusionStrategy topInclusion, double rangeBottom,
                SpacerInclusionStrategy bottomInclusion) {

            assert rangeTop <= rangeBottom : "rangeTop must be less than rangeBottom";

            double heights = 0;

            /*
             * TODO [[optimize]]: this might be somewhat inefficient (due to
             * iterator-based scanning, instead of using the treemap's search
             * functionalities). But it should be easy to write, read, verify
             * and maintain.
             */
            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                double top = spacer.getTop();
                double height = spacer.getHeight();
                double bottom = top + height;

                /*
                 * If we happen to implement a DoubleRange (in addition to the
                 * int-based Range) at some point, the following logic should
                 * probably be converted into using the
                 * Range.partitionWith-equivalent.
                 */

                boolean topIsAboveRange = top < rangeTop;
                boolean topIsInRange = rangeTop <= top && top <= rangeBottom;
                boolean topIsBelowRange = rangeBottom < top;

                boolean bottomIsAboveRange = bottom < rangeTop;
                boolean bottomIsInRange = rangeTop <= bottom
                        && bottom <= rangeBottom;
                boolean bottomIsBelowRange = rangeBottom < bottom;

                assert topIsAboveRange ^ topIsBelowRange
                        ^ topIsInRange : "Bad top logic";
                assert bottomIsAboveRange ^ bottomIsBelowRange
                        ^ bottomIsInRange : "Bad bottom logic";

                if (bottomIsAboveRange) {
                    continue;
                } else if (topIsBelowRange) {
                    return heights;
                } else if (topIsAboveRange && bottomIsInRange) {
                    switch (topInclusion) {
                    case PARTIAL:
                        heights += bottom - rangeTop;
                        break;
                    case COMPLETE:
                        heights += height;
                        break;
                    default:
                        break;
                    }
                } else if (topIsAboveRange && bottomIsBelowRange) {

                    /*
                     * Here we arbitrarily decide that the top inclusion will
                     * have the honor of overriding the bottom inclusion if
                     * happens to be a conflict of interests.
                     */
                    switch (topInclusion) {
                    case NONE:
                        return 0;
                    case COMPLETE:
                        return height;
                    case PARTIAL:
                        return rangeBottom - rangeTop;
                    default:
                        throw new IllegalArgumentException(
                                "Unexpected inclusion state :" + topInclusion);
                    }

                } else if (topIsInRange && bottomIsInRange) {
                    heights += height;
                } else if (topIsInRange && bottomIsBelowRange) {
                    switch (bottomInclusion) {
                    case PARTIAL:
                        heights += rangeBottom - top;
                        break;
                    case COMPLETE:
                        heights += height;
                        break;
                    default:
                        break;
                    }

                    return heights;
                } else {
                    assert false : "Unnaccounted-for situation";
                }
            }

            return heights;
        }

        /**
         * Gets the amount of pixels occupied by spacers from the top until a
         * certain spot from the top of the body.
         *
         * @param px
         *            pixels counted from the top
         * @return the pixels occupied by spacers up until {@code px}
         */
        public double getSpacerHeightsSumUntilPx(double px) {
            return getSpacerHeightsSumBetweenPx(0,
                    SpacerInclusionStrategy.PARTIAL, px,
                    SpacerInclusionStrategy.PARTIAL);
        }

        /**
         * Gets the amount of pixels occupied by spacers until a logical row
         * index. The spacer of the row corresponding with the given index isn't
         * included.
         *
         * @param logicalIndex
         *            a logical row index
         * @return the pixels occupied by spacers up until {@code logicalIndex}
         */
        @SuppressWarnings("boxing")
        public double getSpacerHeightsSumUntilIndex(int logicalIndex) {
            return getHeights(
                    rowIndexToSpacer.headMap(logicalIndex, false).values());
        }

        private double getHeights(Collection<SpacerImpl> spacers) {
            double heights = 0;
            for (SpacerImpl spacer : spacers) {
                heights += spacer.getHeight();
            }
            return heights;
        }

        /**
         * Gets the height of the spacer for a row index.
         *
         * @param rowIndex
         *            the index of the row where the spacer should be
         * @return the height of the spacer at index {@code rowIndex}, or 0 if
         *         there is no spacer there
         */
        public double getSpacerHeight(int rowIndex) {
            SpacerImpl spacer = getSpacer(rowIndex);
            if (spacer != null) {
                return spacer.getHeight();
            } else {
                return 0;
            }
        }

        private boolean spacerExists(int rowIndex) {
            return rowIndexToSpacer.containsKey(Integer.valueOf(rowIndex));
        }

        @SuppressWarnings("boxing")
        private void insertNewSpacer(int rowIndex, double height) {

            if (spacerScrollerRegistration == null) {
                spacerScrollerRegistration = addScrollHandler(spacerScroller);
            }

            final SpacerImpl spacer = new SpacerImpl(rowIndex);

            rowIndexToSpacer.put(rowIndex, spacer);
            // set the position before adding it to DOM
            positions.set(spacer.getRootElement(), getScrollLeft(),
                    calculateSpacerTop(rowIndex));

            TableRowElement spacerRoot = spacer.getRootElement();
            spacerRoot.getStyle()
                    .setWidth(columnConfiguration.calculateRowWidth(), Unit.PX);
            body.getElement().appendChild(spacerRoot);
            spacer.setupDom(height);
            // set the deco position, requires that spacer is in the DOM
            positions.set(spacer.getDecoElement(), 0,
                    spacer.getTop() - spacer.getSpacerDecoTopOffset());

            spacerDecoContainer.appendChild(spacer.getDecoElement());
            if (spacerDecoContainer.getParentElement() == null) {
                getElement().appendChild(spacerDecoContainer);
                // calculate the spacer deco width, it won't change
                spacerDecoWidth = getBoundingWidth(spacer.getDecoElement());
            }

            initSpacerContent(spacer);

            // schedule updating of the physical indexes
            body.domSorter.reschedule();
        }

        private void updateExistingSpacer(int rowIndex, double newHeight) {
            getSpacer(rowIndex).setHeight(newHeight);
        }

        public SpacerImpl getSpacer(int rowIndex) {
            return rowIndexToSpacer.get(Integer.valueOf(rowIndex));
        }

        private void removeSpacer(int rowIndex) {
            removeSpacers(Range.withOnly(rowIndex));
        }

        public void setStylePrimaryName(String style) {
            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                spacer.setStylePrimaryName(style);
            }
        }

        public void setSpacerUpdater(SpacerUpdater spacerUpdater)
                throws IllegalArgumentException {
            if (spacerUpdater == null) {
                throw new IllegalArgumentException(
                        "spacer updater cannot be null");
            }

            destroySpacerContent(rowIndexToSpacer.values());
            this.spacerUpdater = spacerUpdater;
            initSpacerContent(rowIndexToSpacer.values());
        }

        public SpacerUpdater getSpacerUpdater() {
            return spacerUpdater;
        }

        private void destroySpacerContent(Iterable<SpacerImpl> spacers) {
            for (SpacerImpl spacer : spacers) {
                destroySpacerContent(spacer);
            }
        }

        private void destroySpacerContent(SpacerImpl spacer) {
            assert getElement().isOrHasChild(spacer
                    .getRootElement()) : "Spacer's root element somehow got detached from Escalator before detaching";
            assert getElement().isOrHasChild(spacer
                    .getElement()) : "Spacer element somehow got detached from Escalator before detaching";
            spacerUpdater.destroy(spacer);
            assert getElement().isOrHasChild(spacer
                    .getRootElement()) : "Spacer's root element somehow got detached from Escalator before detaching";
            assert getElement().isOrHasChild(spacer
                    .getElement()) : "Spacer element somehow got detached from Escalator before detaching";
        }

        private void initSpacerContent(Iterable<SpacerImpl> spacers) {
            for (SpacerImpl spacer : spacers) {
                initSpacerContent(spacer);
            }
        }

        private void initSpacerContent(SpacerImpl spacer) {
            assert getElement().isOrHasChild(spacer
                    .getRootElement()) : "Spacer's root element somehow got detached from Escalator before attaching";
            assert getElement().isOrHasChild(spacer
                    .getElement()) : "Spacer element somehow got detached from Escalator before attaching";
            spacerUpdater.init(spacer);
            assert getElement().isOrHasChild(spacer
                    .getRootElement()) : "Spacer's root element somehow got detached from Escalator during attaching";
            assert getElement().isOrHasChild(spacer
                    .getElement()) : "Spacer element somehow got detached from Escalator during attaching";

            spacer.show();
        }

        public String getSubPartName(Element subElement) {
            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                if (spacer.getRootElement().isOrHasChild(subElement)) {
                    return "spacer[" + spacer.getRow() + "]";
                }
            }
            return null;
        }

        public Element getSubPartElement(int index) {
            SpacerImpl spacer = rowIndexToSpacer.get(Integer.valueOf(index));
            if (spacer != null) {
                return spacer.getElement();
            } else {
                return null;
            }
        }

        private double calculateSpacerTop(int logicalIndex) {
            return body.getRowTop(logicalIndex) + body.getDefaultRowHeight();
        }

        @SuppressWarnings("boxing")
        private void shiftSpacerPositionsAfterRow(int changedRowIndex,
                double diffPx) {
            for (SpacerImpl spacer : rowIndexToSpacer
                    .tailMap(changedRowIndex, false).values()) {
                spacer.setPositionDiff(0, diffPx);
            }
        }

        /**
         * Shifts spacers at and after a specific row by an amount of rows that
         * don't contain spacers of their own.
         * <p>
         * This moves both their associated logical row index and also their
         * visual placement.
         * <p>
         * <em>Note:</em> This method does not check for the validity of any
         * arguments.
         *
         * @param index
         *            the index of first row to move
         * @param numberOfRows
         *            the number of rows to shift the spacers with. A positive
         *            value is downwards, a negative value is upwards.
         */
        public void shiftSpacersByRows(int index, int numberOfRows) {
            final double pxDiff = numberOfRows * body.getDefaultRowHeight();
            List<SpacerContainer.SpacerImpl> spacers = new ArrayList<>(
                    getSpacersForRowAndAfter(index));
            if (numberOfRows < 0) {
                for (SpacerContainer.SpacerImpl spacer : spacers) {
                    spacer.setPositionDiff(0, pxDiff);
                    spacer.setRowIndex(spacer.getRow() + numberOfRows);
                }
            } else {
                for (int i = spacers.size() - 1; i >= 0; --i) {
                    SpacerContainer.SpacerImpl spacer = spacers.get(i);
                    spacer.setPositionDiff(0, pxDiff);
                    spacer.setRowIndex(spacer.getRow() + numberOfRows);
                }
            }
        }

        /**
         * Update the associated logical row indexes for spacers without moving
         * their actual positions.
         * <p>
         * <em>Note:</em> This method does not check for the validity of any
         * arguments.
         *
         * @param startIndex
         *            the previous logical index of first row to update
         * @param endIndex
         *            the previous logical index of first row that doesn't need
         *            updating anymore
         * @param numberOfRows
         *            the number of rows to shift the associated logical index
         *            with. A positive value is downwards, a negative value is
         *            upwards.
         */
        private void updateSpacerIndexesForRowAndAfter(int startIndex,
                int endIndex, int numberOfRows) {
            List<SpacerContainer.SpacerImpl> spacers = new ArrayList<>(
                    getSpacersForRowAndAfter(startIndex));
            spacers.removeAll(getSpacersForRowAndAfter(endIndex));
            if (numberOfRows < 0) {
                for (SpacerContainer.SpacerImpl spacer : spacers) {
                    spacer.setRowIndex(spacer.getRow() + numberOfRows);
                }
            } else {
                for (int i = spacers.size() - 1; i >= 0; --i) {
                    SpacerContainer.SpacerImpl spacer = spacers.get(i);
                    spacer.setRowIndex(spacer.getRow() + numberOfRows);
                }
            }
        }

        private void updateSpacerDecosVisibility() {
            final Range visibleRowRange = getVisibleRowRange();
            Collection<SpacerImpl> visibleSpacers = rowIndexToSpacer
                    .subMap(visibleRowRange.getStart() - 1,
                            visibleRowRange.getEnd() + 1)
                    .values();
            if (!visibleSpacers.isEmpty()) {
                final double top = tableWrapper.getAbsoluteTop()
                        + header.getHeightOfSection();
                final double bottom = tableWrapper.getAbsoluteBottom()
                        - footer.getHeightOfSection();
                for (SpacerImpl spacer : visibleSpacers) {
                    spacer.updateDecoClip(top, bottom, spacerDecoWidth);
                }
            }
        }
    }

    private class ElementPositionBookkeeper {
        /**
         * A map containing cached values of an element's current top position.
         */
        private final Map<Element, Double> elementTopPositionMap = new HashMap<>();
        private final Map<Element, Double> elementLeftPositionMap = new HashMap<>();

        public void set(final Element e, final double x, final double y) {
            assert e != null : "Element was null";
            position.set(e, x, y);
            elementTopPositionMap.put(e, Double.valueOf(y));
            elementLeftPositionMap.put(e, Double.valueOf(x));
        }

        public double getTop(final Element e) {
            Double top = elementTopPositionMap.get(e);
            if (top == null) {
                throw new IllegalArgumentException("Element " + e
                        + " was not found in the position bookkeeping");
            }
            return top.doubleValue();
        }

        public double getLeft(final Element e) {
            Double left = elementLeftPositionMap.get(e);
            if (left == null) {
                throw new IllegalArgumentException("Element " + e
                        + " was not found in the position bookkeeping");
            }
            return left.doubleValue();
        }

        public void remove(Element e) {
            elementTopPositionMap.remove(e);
            elementLeftPositionMap.remove(e);
        }
    }

    /**
     * Utility class for parsing and storing SubPart request string attributes
     * for Grid and Escalator.
     *
     * @since 7.5.0
     */
    public static class SubPartArguments {
        private String type;
        private int[] indices;

        private SubPartArguments(String type, int[] indices) {
            /*
             * The constructor is private so that no third party would by
             * mistake start using this parsing scheme, since it's not official
             * by TestBench (yet?).
             */

            this.type = type;
            this.indices = indices;
        }

        public String getType() {
            return type;
        }

        public int getIndicesLength() {
            return indices.length;
        }

        public int getIndex(int i) {
            return indices[i];
        }

        public int[] getIndices() {
            return Arrays.copyOf(indices, indices.length);
        }

        static SubPartArguments create(String subPart) {
            String[] splitArgs = subPart.split("\\[");
            String type = splitArgs[0];
            int[] indices = new int[splitArgs.length - 1];
            for (int i = 0; i < indices.length; ++i) {
                String tmp = splitArgs[i + 1];
                indices[i] = Integer
                        .parseInt(tmp.substring(0, tmp.indexOf("]", 1)));
            }
            return new SubPartArguments(type, indices);
        }
    }

    enum ScrollType {
        ROW, SPACER, ROW_AND_SPACER
    }

    // abs(atan(y/x))*(180/PI) = n deg, x = 1, solve y
    /**
     * The solution to
     * <code>|tan<sup>-1</sup>(<i>x</i>)|&times;(180/&pi;)&nbsp;=&nbsp;30</code>
     * .
     * <p>
     * This constant is placed in the Escalator class, instead of an inner
     * class, since even mathematical expressions aren't allowed in non-static
     * inner classes for constants.
     */
    private static final double RATIO_OF_30_DEGREES = 1 / Math.sqrt(3);
    /**
     * The solution to
     * <code>|tan<sup>-1</sup>(<i>x</i>)|&times;(180/&pi;)&nbsp;=&nbsp;40</code>
     * .
     * <p>
     * This constant is placed in the Escalator class, instead of an inner
     * class, since even mathematical expressions aren't allowed in non-static
     * inner classes for constants.
     */
    private static final double RATIO_OF_40_DEGREES = Math.tan(2 * Math.PI / 9);

    private static final String DEFAULT_WIDTH = "500.0px";
    private static final String DEFAULT_HEIGHT = "400.0px";

    private FlyweightRow flyweightRow = new FlyweightRow();

    /** The {@code <thead/>} tag. */
    private final TableSectionElement headElem = TableSectionElement
            .as(DOM.createTHead());
    /** The {@code <tbody/>} tag. */
    private final TableSectionElement bodyElem = TableSectionElement
            .as(DOM.createTBody());
    /** The {@code <tfoot/>} tag. */
    private final TableSectionElement footElem = TableSectionElement
            .as(DOM.createTFoot());

    /**
     * TODO: investigate whether this field is now unnecessary, as
     * {@link ScrollbarBundle} now caches its values.
     *
     * @deprecated maybe...
     */
    @Deprecated
    private double tBodyScrollTop = 0;

    /**
     * TODO: investigate whether this field is now unnecessary, as
     * {@link ScrollbarBundle} now caches its values.
     *
     * @deprecated maybe...
     */
    @Deprecated
    private double tBodyScrollLeft = 0;

    private final VerticalScrollbarBundle verticalScrollbar = new VerticalScrollbarBundle();
    private final HorizontalScrollbarBundle horizontalScrollbar = new HorizontalScrollbarBundle();

    private final AriaGridHelper ariaGridHelper = new AriaGridHelper();

    private final HeaderRowContainer header = new HeaderRowContainer(headElem);
    private final BodyRowContainerImpl body = new BodyRowContainerImpl(
            bodyElem);
    private final FooterRowContainer footer = new FooterRowContainer(footElem);

    /**
     * Flag for keeping track of {@link RowHeightChangedEvent}s
     */
    private boolean rowHeightChangedEventFired = false;

    private final Scroller scroller = new Scroller();

    private final ColumnConfigurationImpl columnConfiguration = new ColumnConfigurationImpl();
    private final DivElement tableWrapper;
    private final Element table;

    private final DivElement horizontalScrollbarDeco = DivElement
            .as(DOM.createDiv());
    private final DivElement headerDeco = DivElement.as(DOM.createDiv());
    private final DivElement footerDeco = DivElement.as(DOM.createDiv());
    private final DivElement spacerDecoContainer = DivElement
            .as(DOM.createDiv());

    private PositionFunction position;

    /** The cached width of the escalator, in pixels. */
    private double widthOfEscalator = 0;
    /** The cached height of the escalator, in pixels. */
    private double heightOfEscalator = 0;

    /** The height of Escalator in terms of body rows. */
    private double heightByRows = 10.0d;

    /** The height of Escalator, as defined by {@link #setHeight(String)} */
    private String heightByCss = "";

    private HeightMode heightMode = HeightMode.CSS;

    private double delayToCancelTouchScroll = -1;

    private boolean layoutIsScheduled = false;
    private ScheduledCommand layoutCommand = () -> {
        // ensure that row heights have been set or auto-detected if
        // auto-detection is already possible, because visibility changes might
        // not trigger the default check that happens in onLoad()
        header.autodetectRowHeightLater();
        body.autodetectRowHeightLater();
        footer.autodetectRowHeightLater();

        recalculateElementSizes();
        layoutIsScheduled = false;
    };

    private final ElementPositionBookkeeper positions = new ElementPositionBookkeeper();

    /**
     * Creates a new Escalator widget instance.
     */
    public Escalator() {

        detectAndApplyPositionFunction();
        getLogger().info("Using " + position.getClass().getSimpleName()
                + " for position");

        final Element root = DOM.createDiv();
        setElement(root);

        setupScrollbars(root);

        tableWrapper = DivElement.as(DOM.createDiv());

        Event.sinkEvents(tableWrapper, Event.ONSCROLL | Event.KEYEVENTS);

        Event.setEventListener(tableWrapper, event -> {
            if (event.getKeyCode() != KeyCodes.KEY_TAB) {
                return;
            }

            boolean browserScroll = tableWrapper.getScrollLeft() != 0
                    || tableWrapper.getScrollTop() != 0;
            boolean keyEvent = event.getType().startsWith("key");

            if (browserScroll || keyEvent) {

                // Browser is scrolling our div automatically, reset
                tableWrapper.setScrollLeft(0);
                tableWrapper.setScrollTop(0);

                Element focused = WidgetUtil.getFocusedElement();
                Stream.of(header, body, footer).forEach(container -> {
                    Cell cell = container.getCell(focused);
                    if (cell == null) {
                        return;
                    }

                    scrollToColumn(cell.getColumn(), ScrollDestination.ANY, 0);
                    if (container == body) {
                        scrollToRow(cell.getRow(), ScrollDestination.ANY, 0);
                    }
                });
            }
        });

        root.appendChild(tableWrapper);

        table = DOM.createTable();
        tableWrapper.appendChild(table);

        table.appendChild(headElem);
        table.appendChild(bodyElem);
        table.appendChild(footElem);

        Style hCornerStyle = headerDeco.getStyle();
        hCornerStyle.setWidth(verticalScrollbar.getScrollbarThickness(),
                Unit.PX);
        hCornerStyle.setDisplay(Display.NONE);
        root.appendChild(headerDeco);

        Style fCornerStyle = footerDeco.getStyle();
        fCornerStyle.setWidth(verticalScrollbar.getScrollbarThickness(),
                Unit.PX);
        fCornerStyle.setDisplay(Display.NONE);
        root.appendChild(footerDeco);

        Style hWrapperStyle = horizontalScrollbarDeco.getStyle();
        hWrapperStyle.setDisplay(Display.NONE);
        hWrapperStyle.setHeight(horizontalScrollbar.getScrollbarThickness(),
                Unit.PX);
        root.appendChild(horizontalScrollbarDeco);

        setStylePrimaryName("v-escalator");

        spacerDecoContainer.setAttribute("aria-hidden", "true");

        // init default dimensions
        setHeight(null);
        setWidth(null);

        publishJSHelpers(root);
    }

    private double getBoundingWidth(Element element) {
        // Gets the current width, including border and padding, for the element
        // while ignoring any transforms applied to the element (e.g. scale)
        return new ComputedStyle(element).getWidthIncludingBorderPadding();
    }

    private double getBoundingHeight(Element element) {
        // Gets the current height, including border and padding, for the
        // element while ignoring any transforms applied to the element (e.g.
        // scale)
        return new ComputedStyle(element).getHeightIncludingBorderPadding();
    }

    private int getBodyRowCount() {
        return getBody().getRowCount();
    }

    private native void publishJSHelpers(Element root)
    /*-{
        var self = this;
        root.getBodyRowCount = $entry(function () {
           return self.@Escalator::getBodyRowCount()();
        });
    }-*/;

    private void setupScrollbars(final Element root) {

        ScrollHandler scrollHandler = event -> {
            scroller.onScroll();
            fireEvent(new ScrollEvent());
        };

        int scrollbarThickness = WidgetUtil.getNativeScrollbarSize();
        if (BrowserInfo.get().isIE()) {
            /*
             * IE refuses to scroll properly if the DIV isn't at least one pixel
             * larger than the scrollbar controls themselves.
             */
            scrollbarThickness += 1;
        }

        root.appendChild(verticalScrollbar.getElement());
        verticalScrollbar.addScrollHandler(scrollHandler);
        verticalScrollbar.setScrollbarThickness(scrollbarThickness);
        verticalScrollbar
                .addVisibilityHandler(new ScrollbarBundle.VisibilityHandler() {

                    private boolean queued = false;

                    @Override
                    public void visibilityChanged(
                            ScrollbarBundle.VisibilityChangeEvent event) {
                        if (queued) {
                            return;
                        }
                        queued = true;

                        /*
                         * We either lost or gained a scrollbar. In either case,
                         * we may need to update the column widths.
                         */
                        Scheduler.get().scheduleFinally(() -> {
                            fireVerticalScrollbarVisibilityChangeEvent();
                            queued = false;
                        });
                    }
                });

        root.appendChild(horizontalScrollbar.getElement());
        horizontalScrollbar.addScrollHandler(scrollHandler);
        horizontalScrollbar.setScrollbarThickness(scrollbarThickness);
        horizontalScrollbar
                .addVisibilityHandler(new ScrollbarBundle.VisibilityHandler() {

                    private boolean queued = false;

                    @Override
                    public void visibilityChanged(
                            ScrollbarBundle.VisibilityChangeEvent event) {
                        if (queued) {
                            return;
                        }
                        queued = true;

                        /*
                         * We either lost or gained a scrollbar. In any case, we
                         * need to change the height, if it's defined by rows.
                         */
                        Scheduler.get().scheduleFinally(() -> {
                            applyHeightByRows();
                            queued = false;
                        });
                    }
                });

        /*
         * Because of all the IE hacks we've done above, we now have scrollbars
         * hiding underneath a lot of DOM elements.
         *
         * This leads to problems with OSX (and many touch-only devices) when
         * scrollbars are only shown when scrolling, as the scrollbar elements
         * are hidden underneath everything. We trust that the scrollbars behave
         * properly in these situations and simply pop them out with a bit of
         * z-indexing.
         */
        if (WidgetUtil.getNativeScrollbarSize() == 0) {
            verticalScrollbar.getElement().getStyle().setZIndex(90);
            horizontalScrollbar.getElement().getStyle().setZIndex(90);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        // ensure that row heights have been set or auto-detected if
        // auto-detection is already possible, if not the check will be
        // performed again in layoutCommand
        header.autodetectRowHeightLater();
        body.autodetectRowHeightLater();
        footer.autodetectRowHeightLater();

        header.paintInsertRows(0, header.getRowCount());
        footer.paintInsertRows(0, footer.getRowCount());

        boolean columnsChanged = false;
        for (ColumnConfigurationImpl.Column column : columnConfiguration.columns) {
            boolean columnChanged = column.measureAndSetWidthIfNeeded();
            if (columnChanged) {
                columnsChanged = true;
            }
        }
        if (columnsChanged) {
            header.reapplyColumnWidths();
            body.reapplyColumnWidths();
            footer.reapplyColumnWidths();
        }

        verticalScrollbar.onLoad();
        horizontalScrollbar.onLoad();

        scroller.attachScrollListener(verticalScrollbar.getElement());
        scroller.attachScrollListener(horizontalScrollbar.getElement());
        scroller.attachMousewheelListener(getElement());

        if (isCurrentBrowserIE11OrEdge()) {
            // Touch listeners doesn't work for IE11 and Edge (#18737)
            scroller.attachPointerEventListeners(getElement());
        } else {
            scroller.attachTouchListeners(getElement());
        }

        /*
         * Note: There's no need to explicitly insert rows into the body.
         *
         * recalculateElementSizes will recalculate the height of the body. This
         * has the side-effect that as the body's size grows bigger (i.e. from 0
         * to its actual height), more escalator rows are populated. Those
         * escalator rows are then immediately rendered. This, in effect, is the
         * same thing as inserting those rows.
         *
         * In fact, having an extra paintInsertRows here would lead to duplicate
         * rows.
         */
        recalculateElementSizes();
    }

    @Override
    protected void onUnload() {

        scroller.detachScrollListener(verticalScrollbar.getElement());
        scroller.detachScrollListener(horizontalScrollbar.getElement());
        scroller.detachMousewheelListener(getElement());

        if (isCurrentBrowserIE11OrEdge()) {
            // Touch listeners doesn't work for IE11 and Edge (#18737)
            scroller.detachPointerEventListeners(getElement());
        } else {
            scroller.detachTouchListeners(getElement());
        }

        /*
         * We can call paintRemoveRows here, because static ranges are simple to
         * remove.
         */
        header.paintRemoveRows(0, header.getRowCount());
        footer.paintRemoveRows(0, footer.getRowCount());

        /*
         * We can't call body.paintRemoveRows since it relies on rowCount to be
         * updated correctly. Since it isn't, we'll simply and brutally rip out
         * the DOM elements (in an elegant way, of course).
         */
        int rowsToRemove = body.getDomRowCount();
        for (int i = 0; i < rowsToRemove; i++) {
            int index = rowsToRemove - i - 1;
            TableRowElement tr = bodyElem.getRows().getItem(index);
            body.paintRemoveRow(tr, index);
            positions.remove(tr);
        }
        body.visualRowOrder.clear();
        body.setTopRowLogicalIndex(0);

        super.onUnload();
    }

    private void detectAndApplyPositionFunction() {
        final Style docStyle = Document.get().getBody().getStyle();
        if (hasProperty(docStyle, "transform")) {
            if (hasProperty(docStyle, "transformStyle")) {
                position = new Translate3DPosition();
            } else {
                position = new TranslatePosition();
            }
        } else if (hasProperty(docStyle, "webkitTransform")) {
            position = new WebkitTranslate3DPosition();
        }
    }

    private Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    private static native boolean hasProperty(Style style, String name)
    /*-{
        return style[name] !== undefined;
    }-*/;

    /**
     * Check whether there are both columns and any row data (for either
     * headers, body or footer).
     *
     * @return <code>true</code> if header, body or footer has rows and there
     *         are columns
     */
    private boolean hasColumnAndRowData() {
        return (header.getRowCount() > 0 || body.getRowCount() > 0
                || footer.getRowCount() > 0)
                && columnConfiguration.getColumnCount() > 0;
    }

    /**
     * Check whether there are any cells in the DOM.
     *
     * @return <code>true</code> if header, body or footer has any child
     *         elements
     */
    private boolean hasSomethingInDom() {
        return headElem.hasChildNodes() || bodyElem.hasChildNodes()
                || footElem.hasChildNodes();
    }

    /**
     * Returns the row container for the header in this Escalator.
     *
     * @return the header. Never <code>null</code>
     */
    public RowContainer getHeader() {
        return header;
    }

    /**
     * Returns the row container for the body in this Escalator.
     *
     * @return the body. Never <code>null</code>
     */
    public BodyRowContainer getBody() {
        return body;
    }

    /**
     * Returns the row container for the footer in this Escalator.
     *
     * @return the footer. Never <code>null</code>
     */
    public RowContainer getFooter() {
        return footer;
    }

    /**
     * Returns the configuration object for the columns in this Escalator.
     *
     * @return the configuration object for the columns in this Escalator. Never
     *         <code>null</code>
     */
    public ColumnConfiguration getColumnConfiguration() {
        return columnConfiguration;
    }

    @Override
    public void setWidth(final String width) {
        String oldWidth = getElement().getStyle().getProperty("width");
        if (width != null && !width.isEmpty()) {
            super.setWidth(width);
            if (!width.equals(oldWidth)) {
                fireEscalatorSizeChangeEvent();
            }
        } else {
            super.setWidth(DEFAULT_WIDTH);
            if (!DEFAULT_WIDTH.equals(oldWidth)) {
                fireEscalatorSizeChangeEvent();
            }
        }

        recalculateElementSizes();
    }

    /**
     * {@inheritDoc}
     * <p>
     * If Escalator is currently not in {@link HeightMode#CSS}, the given value
     * is remembered, and applied once the mode is applied.
     *
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(String height) {
        /*
         * TODO remove method once RequiresResize and the Vaadin layoutmanager
         * listening mechanisms are implemented
         */

        if (height != null && !height.isEmpty()) {
            heightByCss = height;
        } else {
            if (getHeightMode() == HeightMode.UNDEFINED) {
                heightByRows = body.getRowCount();
                applyHeightByRows();
                return;
            } else {
                heightByCss = DEFAULT_HEIGHT;
            }
        }

        if (getHeightMode() == HeightMode.CSS) {
            setHeightInternal(height);
        }
    }

    private void setHeightInternal(final String height) {
        final int escalatorRowsBefore = body.visualRowOrder.size();

        if (height != null && !height.isEmpty()) {
            String oldHeight = getElement().getStyle().getProperty("height");
            super.setHeight(height);
            if (!height.equals(oldHeight)) {
                fireEscalatorSizeChangeEvent();
            }
        } else {
            if (getHeightMode() == HeightMode.UNDEFINED) {
                int newHeightByRows = body.getRowCount();
                if (heightByRows != newHeightByRows) {
                    heightByRows = newHeightByRows;
                    applyHeightByRows();
                }
                return;
            } else {
                String oldHeight = getElement().getStyle()
                        .getProperty("height");
                super.setHeight(DEFAULT_HEIGHT);
                if (!DEFAULT_HEIGHT.equals(oldHeight)) {
                    fireEscalatorSizeChangeEvent();
                }
            }
        }

        recalculateElementSizes();

        if (escalatorRowsBefore != body.visualRowOrder.size()) {
            fireRowVisibilityChangeEvent();
        }
    }

    /**
     * Returns the vertical scroll offset. Note that this is not necessarily the
     * same as the {@code scrollTop} attribute in the DOM.
     *
     * @return the logical vertical scroll offset
     */
    public double getScrollTop() {
        return verticalScrollbar.getScrollPos();
    }

    /**
     * Sets the vertical scroll offset. Note that this will not necessarily
     * become the same as the {@code scrollTop} attribute in the DOM.
     *
     * @param scrollTop
     *            the number of pixels to scroll vertically
     */
    public void setScrollTop(final double scrollTop) {
        verticalScrollbar.setScrollPos(scrollTop);
    }

    /**
     * Returns the logical horizontal scroll offset. Note that this is not
     * necessarily the same as the {@code scrollLeft} attribute in the DOM.
     *
     * @return the logical horizontal scroll offset
     */
    public double getScrollLeft() {
        return horizontalScrollbar.getScrollPos();
    }

    /**
     * Sets the logical horizontal scroll offset. Note that will not necessarily
     * become the same as the {@code scrollLeft} attribute in the DOM.
     *
     * @param scrollLeft
     *            the number of pixels to scroll horizontally
     */
    public void setScrollLeft(final double scrollLeft) {
        horizontalScrollbar.setScrollPos(scrollLeft);
    }

    /**
     * Returns the scroll width for the escalator. Note that this is not
     * necessary the same as {@code Element.scrollWidth} in the DOM.
     *
     * @since 7.5.0
     * @return the scroll width in pixels
     */
    public double getScrollWidth() {
        return horizontalScrollbar.getScrollSize();
    }

    /**
     * Returns the scroll height for the escalator. Note that this is not
     * necessary the same as {@code Element.scrollHeight} in the DOM.
     *
     * @since 7.5.0
     * @return the scroll height in pixels
     */
    public double getScrollHeight() {
        return verticalScrollbar.getScrollSize();
    }

    /**
     * Scrolls the body horizontally so that the column at the given index is
     * visible and there is at least {@code padding} pixels in the direction of
     * the given scroll destination.
     *
     * @param columnIndex
     *            the index of the column to scroll to
     * @param destination
     *            where the column should be aligned visually after scrolling
     * @param padding
     *            the number pixels to place between the scrolled-to column and
     *            the viewport edge.
     * @throws IndexOutOfBoundsException
     *             if {@code columnIndex} is not a valid index for an existing
     *             column
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE}
     *             and padding is nonzero; or if the indicated column is frozen;
     *             or if {@code destination == null}
     */
    public void scrollToColumn(final int columnIndex,
            final ScrollDestination destination, final int padding)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        validateScrollDestination(destination, padding);
        verifyValidColumnIndex(columnIndex);

        if (columnIndex < columnConfiguration.frozenColumns) {
            throw new IllegalArgumentException(
                    "The given column index " + columnIndex + " is frozen.");
        }

        scroller.scrollToColumn(columnIndex, destination, padding);
    }

    private void verifyValidColumnIndex(final int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0
                || columnIndex >= columnConfiguration.getColumnCount()) {
            throw new IndexOutOfBoundsException("The given column index "
                    + columnIndex + " does not exist.");
        }
    }

    /**
     * Scrolls the body vertically so that the row at the given index is visible
     * and there is at least {@literal padding} pixels to the given scroll
     * destination.
     *
     * @param rowIndex
     *            the index of the logical row to scroll to
     * @param destination
     *            where the row should be aligned visually after scrolling
     * @param padding
     *            the number pixels to place between the scrolled-to row and the
     *            viewport edge.
     * @throws IndexOutOfBoundsException
     *             if {@code rowIndex} is not a valid index for an existing row
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE}
     *             and padding is nonzero; or if {@code destination == null}
     * @see #scrollToRowAndSpacer(int, ScrollDestination, int)
     * @see #scrollToSpacer(int, ScrollDestination, int)
     */
    public void scrollToRow(final int rowIndex,
            final ScrollDestination destination, final int padding)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        verifyValidRowIndex(rowIndex);
        body.scrollToRowSpacerOrBoth(rowIndex, destination, padding,
                ScrollType.ROW);
    }

    private void verifyValidRowIndex(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= body.getRowCount()) {
            throw new IndexOutOfBoundsException(
                    "The given row index " + rowIndex + " does not exist.");
        }
    }

    /**
     * Scrolls the body vertically so that the spacer at the given row index is
     * visible and there is at least {@literal padding} pixels to the given
     * scroll destination.
     *
     * @since 7.5.0
     * @param spacerIndex
     *            the row index of the spacer to scroll to
     * @param destination
     *            where the spacer should be aligned visually after scrolling
     * @param padding
     *            the number of pixels to place between the scrolled-to spacer
     *            and the viewport edge
     * @throws IllegalArgumentException
     *             if {@code spacerIndex} is not an opened spacer; or if
     *             {@code destination} is {@link ScrollDestination#MIDDLE} and
     *             padding is nonzero; or if {@code destination == null}
     * @see #scrollToRow(int, ScrollDestination, int)
     * @see #scrollToRowAndSpacer(int, ScrollDestination, int)
     */
    public void scrollToSpacer(final int spacerIndex,
            ScrollDestination destination, final int padding)
            throws IllegalArgumentException {
        body.scrollToRowSpacerOrBoth(spacerIndex, destination, padding,
                ScrollType.SPACER);
    }

    /**
     * Scrolls vertically to a row and the spacer below it.
     * <p>
     * If a spacer is not open at that index, this method behaves like
     * {@link #scrollToRow(int, ScrollDestination, int)}
     *
     * @since 7.5.0
     * @param rowIndex
     *            the index of the logical row to scroll to. -1 takes the
     *            topmost spacer into account as well.
     * @param destination
     *            where the row should be aligned visually after scrolling
     * @param padding
     *            the number pixels to place between the scrolled-to row and the
     *            viewport edge.
     * @see #scrollToRow(int, ScrollDestination, int)
     * @see #scrollToSpacer(int, ScrollDestination, int)
     * @throws IllegalArgumentException
     *             if {@code destination} is {@link ScrollDestination#MIDDLE}
     *             and {@code padding} is not zero; or if {@code rowIndex} is
     *             not a valid row index, or -1; or if
     *             {@code destination == null}; or if {@code rowIndex == -1} and
     *             there is no spacer open at that index.
     */
    public void scrollToRowAndSpacer(final int rowIndex,
            final ScrollDestination destination, final int padding)
            throws IllegalArgumentException {
        if (rowIndex != -1) {
            verifyValidRowIndex(rowIndex);
        }
        body.scrollToRowSpacerOrBoth(rowIndex, destination, padding,
                ScrollType.ROW_AND_SPACER);
    }

    private static void validateScrollDestination(
            final ScrollDestination destination, final int padding) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        if (destination == ScrollDestination.MIDDLE && padding != 0) {
            throw new IllegalArgumentException(
                    "You cannot have a padding with a MIDDLE destination");
        }
    }

    /**
     * Recalculates the dimensions for all elements that require manual
     * calculations. Also updates the dimension caches.
     * <p>
     * <em>Note:</em> This method has the <strong>side-effect</strong>
     * automatically makes sure that an appropriate amount of escalator rows are
     * present. So, if the body area grows, more <strong>escalator rows might be
     * inserted</strong>. Conversely, if the body area shrinks,
     * <strong>escalator rows might be removed</strong>.
     */
    private void recalculateElementSizes() {
        if (!isAttached()) {
            return;
        }

        Profiler.enter("Escalator.recalculateElementSizes");
        widthOfEscalator = Math.max(0, getBoundingWidth(getElement()));
        heightOfEscalator = Math.max(0, getBoundingHeight(getElement()));

        header.recalculateSectionHeight();
        body.recalculateSectionHeight();
        footer.recalculateSectionHeight();

        scroller.recalculateScrollbarsForVirtualViewport();
        body.verifyEscalatorCount();
        body.reapplySpacerWidths();
        Profiler.leave("Escalator.recalculateElementSizes");
    }

    /**
     * Snap deltas of x and y to the major four axes (up, down, left, right)
     * with a threshold of a number of degrees from those axes.
     *
     * @param deltaX
     *            the delta in the x axis
     * @param deltaY
     *            the delta in the y axis
     * @param thresholdRatio
     *            the threshold in ratio (0..1) between x and y for when to snap
     * @return a two-element array: <code>[snappedX, snappedY]</code>
     */
    private static double[] snapDeltas(final double deltaX, final double deltaY,
            final double thresholdRatio) {

        final double[] array = new double[2];
        if (deltaX != 0 && deltaY != 0) {
            final double aDeltaX = Math.abs(deltaX);
            final double aDeltaY = Math.abs(deltaY);
            final double yRatio = aDeltaY / aDeltaX;
            final double xRatio = aDeltaX / aDeltaY;

            array[0] = (xRatio < thresholdRatio) ? 0 : deltaX;
            array[1] = (yRatio < thresholdRatio) ? 0 : deltaY;
        } else {
            array[0] = deltaX;
            array[1] = deltaY;
        }

        return array;
    }

    /**
     * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
     * <p>
     * Adds an event handler that gets notified when the visibility of the
     * vertical scrollbar changes.
     *
     * @param verticalScrollbarVisibilityChangeHandler
     *            the event handler
     * @return a handler registration for the added handler
     */
    public HandlerRegistration addVerticalScrollbarVisibilityChangeHandler(
            VerticalScrollbarVisibilityChangeHandler verticalScrollbarVisibilityChangeHandler) {
        return addHandler(verticalScrollbarVisibilityChangeHandler,
                VerticalScrollbarVisibilityChangeEvent.TYPE);
    }

    private void fireVerticalScrollbarVisibilityChangeEvent() {
        fireEvent(new VerticalScrollbarVisibilityChangeEvent());
    }

    /**
     * FOR INTERNAL USE ONLY, MAY GET REMOVED OR MODIFIED AT ANY TIME!
     * <p>
     * Adds an event handler that gets notified when the Escalator size changes.
     *
     * @param escalatorSizeChangeHandler
     *            the event handler
     * @return a handler registration for the added handler
     */
    public HandlerRegistration addEscalatorSizeChangeHandler(
            EscalatorSizeChangeHandler escalatorSizeChangeHandler) {
        return addHandler(escalatorSizeChangeHandler,
                EscalatorSizeChangeEvent.TYPE);
    }

    private void fireEscalatorSizeChangeEvent() {
        fireEvent(new EscalatorSizeChangeEvent());
    }

    /**
     * Adds an event handler that gets notified when the range of visible rows
     * changes e.g. because of scrolling, row resizing or spacers
     * appearing/disappearing.
     *
     * @param rowVisibilityChangeHandler
     *            the event handler
     * @return a handler registration for the added handler
     */
    public HandlerRegistration addRowVisibilityChangeHandler(
            RowVisibilityChangeHandler rowVisibilityChangeHandler) {
        return addHandler(rowVisibilityChangeHandler,
                RowVisibilityChangeEvent.TYPE);
    }

    private void fireRowVisibilityChangeEvent() {
        if (!body.visualRowOrder.isEmpty()) {
            int visibleRangeStart = body.getTopRowLogicalIndex();
            int visibleRowCount = body.visualRowOrder.size();
            fireEvent(new RowVisibilityChangeEvent(visibleRangeStart,
                    visibleRowCount));
        } else {
            fireEvent(new RowVisibilityChangeEvent(0, 0));
        }
    }

    /**
     * Gets the logical index range of currently visible rows.
     *
     * @return logical index range of visible rows
     */
    public Range getVisibleRowRange() {
        if (!body.visualRowOrder.isEmpty()) {
            return Range.withLength(body.getTopRowLogicalIndex(),
                    body.visualRowOrder.size());
        } else {
            return Range.withLength(0, 0);
        }
    }

    /**
     * Returns the widget from a cell node or <code>null</code> if there is no
     * widget in the cell
     *
     * @param cellNode
     *            The cell node
     */
    static Widget getWidgetFromCell(Node cellNode) {
        Node possibleWidgetNode = cellNode.getFirstChild();
        if (possibleWidgetNode != null
                && possibleWidgetNode.getNodeType() == Node.ELEMENT_NODE) {
            @SuppressWarnings("deprecation")
            com.google.gwt.user.client.Element castElement = (com.google.gwt.user.client.Element) possibleWidgetNode
                    .cast();
            Widget w = WidgetUtil.findWidget(castElement);

            // Ensure findWidget did not traverse past the cell element in the
            // DOM hierarchy
            if (cellNode.isOrHasChild(w.getElement())) {
                return w;
            }
        }
        return null;
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);

        verticalScrollbar.setStylePrimaryName(style);
        horizontalScrollbar.setStylePrimaryName(style);

        UIObject.setStylePrimaryName(tableWrapper, style + "-tablewrapper");
        UIObject.setStylePrimaryName(headerDeco, style + "-header-deco");
        UIObject.setStylePrimaryName(footerDeco, style + "-footer-deco");
        UIObject.setStylePrimaryName(horizontalScrollbarDeco,
                style + "-horizontal-scrollbar-deco");
        UIObject.setStylePrimaryName(spacerDecoContainer,
                style + "-spacer-deco-container");

        header.setStylePrimaryName(style);
        body.setStylePrimaryName(style);
        footer.setStylePrimaryName(style);
    }

    /**
     * Sets the number of rows that should be visible in Escalator's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Escalator is currently not in {@link HeightMode#ROW}, the given value
     * is remembered, and applied once the mode is applied.
     *
     * @param rows
     *            the number of rows that should be visible in Escalator's body
     * @throws IllegalArgumentException
     *             if {@code rows} is &leq; 0, {@link Double#isInfinite(double)
     *             infinite} or {@link Double#isNaN(double) NaN}.
     * @see #setHeightMode(HeightMode)
     */
    public void setHeightByRows(double rows) throws IllegalArgumentException {
        if (heightMode == HeightMode.UNDEFINED && body.insertingOrRemoving) {
            // this will be called again once the operation is finished, ignore
            // for now
            return;
        }
        if (rows < 0) {
            throw new IllegalArgumentException(
                    "The number of rows must be a positive number.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "The number of rows must be finite.");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("The number must not be NaN.");
        }

        heightByRows = rows;
        applyHeightByRows();
    }

    /**
     * Gets the amount of rows in Escalator's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * By default, it is 10.
     *
     * @return the amount of rows that are being shown in Escalator's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return heightByRows;
    }

    /**
     * Reapplies the row-based height of the Grid, if Grid currently should
     * define its height that way.
     */
    private void applyHeightByRows() {
        if (heightMode != HeightMode.ROW
                && heightMode != HeightMode.UNDEFINED) {
            return;
        }

        double headerHeight = header.getHeightOfSection();
        double footerHeight = footer.getHeightOfSection();
        double bodyHeight = body.getDefaultRowHeight() * heightByRows;
        double scrollbar = horizontalScrollbar.showsScrollHandle()
                ? horizontalScrollbar.getScrollbarThickness()
                : 0;
        double spacerHeight = 0; // ignored if HeightMode.ROW
        if (heightMode == HeightMode.UNDEFINED) {
            spacerHeight = body.spacerContainer.getSpacerHeightsSum();
        }

        double totalHeight = headerHeight + bodyHeight + spacerHeight
                + scrollbar + footerHeight;
        setHeightInternal(totalHeight + "px");
    }

    /**
     * Defines the mode in which the Escalator widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Escalator will respect the values
     * given via {@link #setHeight(String)}, and behave as a traditional Widget.
     * <p>
     * If {@link HeightMode#ROW} is given, Escalator will make sure that the
     * {@link #getBody() body} will display as many rows as
     * {@link #getHeightByRows()} defines. <em>Note:</em> If headers/footers are
     * inserted or removed, the widget will resize itself to still display the
     * required amount of rows in its body. It also takes the horizontal
     * scrollbar into account.
     *
     * @param heightMode
     *            the mode in to which Escalator should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         *
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        if (heightMode != this.heightMode) {
            this.heightMode = heightMode;

            switch (this.heightMode) {
            case CSS:
                setHeight(heightByCss);
                break;
            case ROW:
                setHeightByRows(heightByRows);
                break;
            case UNDEFINED:
                setHeightByRows(body.getRowCount());
                break;
            default:
                throw new IllegalStateException("Unimplemented feature "
                        + "- unknown HeightMode: " + this.heightMode);
            }
        }
    }

    /**
     * Returns the current {@link HeightMode} the Escalator is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     *
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return heightMode;
    }

    /**
     * Returns the {@link RowContainer} which contains the element.
     *
     * @param element
     *            the element to check for
     * @return the container the element is in or <code>null</code> if element
     *         is not present in any container.
     */
    public RowContainer findRowContainer(Element element) {
        if (getHeader().getElement() != element
                && getHeader().getElement().isOrHasChild(element)) {
            return getHeader();
        } else if (getBody().getElement() != element
                && getBody().getElement().isOrHasChild(element)) {
            return getBody();
        } else if (getFooter().getElement() != element
                && getFooter().getElement().isOrHasChild(element)) {
            return getFooter();
        }
        return null;
    }

    /**
     * Sets whether a scroll direction is locked or not.
     * <p>
     * If a direction is locked, the escalator will refuse to scroll in that
     * direction.
     *
     * @param direction
     *            the orientation of the scroll to set the lock status
     * @param locked
     *            <code>true</code> to lock, <code>false</code> to unlock
     */
    public void setScrollLocked(ScrollbarBundle.Direction direction,
            boolean locked) {
        switch (direction) {
        case HORIZONTAL:
            horizontalScrollbar.setLocked(locked);
            break;
        case VERTICAL:
            verticalScrollbar.setLocked(locked);
            break;
        default:
            throw new UnsupportedOperationException(
                    "Unexpected value: " + direction);
        }
    }

    /**
     * Checks whether or not an direction is locked for scrolling.
     *
     * @param direction
     *            the direction of the scroll of which to check the lock status
     * @return <code>true</code> if the direction is locked
     */
    public boolean isScrollLocked(ScrollbarBundle.Direction direction) {
        switch (direction) {
        case HORIZONTAL:
            return horizontalScrollbar.isLocked();
        case VERTICAL:
            return verticalScrollbar.isLocked();
        default:
            throw new UnsupportedOperationException(
                    "Unexpected value: " + direction);
        }
    }

    /**
     * Adds a scroll handler to this escalator.
     *
     * @param handler
     *            the scroll handler to add
     * @return a handler registration for the registered scroll handler
     */
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addHandler(handler, ScrollEvent.TYPE);
    }

    /**
     * Returns true if the Escalator is currently scrolling by touch, or has not
     * made the decision yet whether to accept touch actions as scrolling or
     * not.
     *
     * @see #setDelayToCancelTouchScroll(double)
     *
     * @return true when the component is touch scrolling at the moment
     * @since 8.1
     */
    public boolean isTouchScrolling() {
        return scroller.touchHandlerBundle.touching;
    }

    /**
     * Returns the time after which to not consider a touch event a scroll event
     * if the user has not moved the touch. This can be used to differentiate
     * between quick touch move (scrolling) and long tap (e.g. context menu or
     * drag and drop operation).
     *
     * @return delay in milliseconds after which to cancel touch scrolling if
     *         there is no movement, -1 means scrolling is always allowed
     * @since 8.1
     */
    public double getDelayToCancelTouchScroll() {
        return delayToCancelTouchScroll;
    }

    /**
     * Sets the time after which to not consider a touch event a scroll event if
     * the user has not moved the touch. This can be used to differentiate
     * between quick touch move (scrolling) and long tap (e.g. context menu or
     * drag and drop operation).
     *
     * @param delayToCancelTouchScroll
     *            delay in milliseconds after which to cancel touch scrolling if
     *            there is no movement, -1 to always allow scrolling
     * @since 8.1
     */
    public void setDelayToCancelTouchScroll(double delayToCancelTouchScroll) {
        this.delayToCancelTouchScroll = delayToCancelTouchScroll;
    }

    @Override
    public boolean isWorkPending() {
        return body.domSorter.waiting || verticalScrollbar.isWorkPending()
                || horizontalScrollbar.isWorkPending() || layoutIsScheduled;
    }

    @Override
    public void onResize() {
        if (isAttached() && !layoutIsScheduled) {
            layoutIsScheduled = true;
            Scheduler.get().scheduleFinally(layoutCommand);
        }
    }

    /**
     * Gets the maximum number of body rows that can be visible on the screen at
     * once.
     *
     * @return the maximum capacity
     */
    public int getMaxVisibleRowCount() {
        return body.getMaxVisibleRowCount();
    }

    /**
     * Gets the escalator's inner width. This is the entire width in pixels,
     * without the vertical scrollbar.
     *
     * @return escalator's inner width
     */
    public double getInnerWidth() {
        return getBoundingWidth(tableWrapper);
    }

    /**
     * Resets all cached pixel sizes and reads new values from the DOM. This
     * methods should be used e.g. when styles affecting the dimensions of
     * elements in this escalator have been changed.
     */
    public void resetSizesFromDom() {
        header.autodetectRowHeightNow();
        body.autodetectRowHeightNow();
        footer.autodetectRowHeightNow();

        for (int i = 0; i < columnConfiguration.getColumnCount(); i++) {
            columnConfiguration.setColumnWidth(i,
                    columnConfiguration.getColumnWidth(i));
        }
    }

    private Range getViewportPixels() {
        int from = (int) Math.floor(verticalScrollbar.getScrollPos());
        int to = (int) body.getHeightOfSection();
        return Range.withLength(from, to);
    }

    @Override
    @SuppressWarnings("deprecation")
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        SubPartArguments args = SubPartArguments.create(subPart);

        Element tableStructureElement = getSubPartElementTableStructure(args);
        if (tableStructureElement != null) {
            return DOM.asOld(tableStructureElement);
        }

        Element spacerElement = getSubPartElementSpacer(args);
        if (spacerElement != null) {
            return DOM.asOld(spacerElement);
        }

        return null;
    }

    /**
     * Returns the {@code <div class="{primary-stylename}-tablewrapper" />}
     * element which has the table inside it. {primary-stylename} is .e.g
     * {@code v-grid}.
     * <p>
     * <em>NOTE: you should not do any modifications to the returned element.
     * This API is only available for querying data from the element.</em>
     *
     * @return the table wrapper element
     * @since 8.1
     */
    public Element getTableWrapper() {
        return tableWrapper;
    }

    /**
     * Returns the <code>&lt;table&gt;</code> element of the grid.
     *
     * @return the table element
     * @since 8.2
     */
    public Element getTable() {
        return table;
    }

    private Element getSubPartElementTableStructure(SubPartArguments args) {

        String type = args.getType();
        int[] indices = args.getIndices();

        // Get correct RowContainer for type from Escalator
        RowContainer container = null;
        if (type.equalsIgnoreCase("header")) {
            container = getHeader();
        } else if (type.equalsIgnoreCase("cell")) {
            if (indices.length > 0) {
                // If wanted row is not visible, we need to scroll there.
                // Scrolling might be a no-op if row is already in the viewport.
                scrollToRow(indices[0], ScrollDestination.ANY, 0);
            }
            container = getBody();
        } else if (type.equalsIgnoreCase("footer")) {
            container = getFooter();
        }

        if (null != container) {
            if (indices.length == 0) {
                // No indexing. Just return the wanted container element
                return container.getElement();
            } else {
                try {
                    return getSubPart(container, indices);
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
            }
        }
        return null;
    }

    private Element getSubPart(RowContainer container, int[] indices) {
        Element targetElement = container.getRowElement(indices[0]);

        // Scroll wanted column to view if able
        if (indices.length > 1 && targetElement != null) {
            if (getColumnConfiguration().getFrozenColumnCount() <= indices[1]) {
                scrollToColumn(indices[1], ScrollDestination.ANY, 0);
            }

            targetElement = getCellFromRow(TableRowElement.as(targetElement),
                    indices[1]);

            for (int i = 2; i < indices.length && targetElement != null; ++i) {
                targetElement = (Element) targetElement.getChild(indices[i]);
            }
        }

        return targetElement;
    }

    private static Element getCellFromRow(TableRowElement rowElement,
            int index) {
        int childCount = rowElement.getCells().getLength();
        if (index < 0 || index >= childCount) {
            return null;
        }

        TableCellElement currentCell = null;
        boolean indexInColspan = false;
        int i = 0;

        while (!indexInColspan) {
            currentCell = rowElement.getCells().getItem(i);

            // Calculate if this is the cell we are looking for
            int colSpan = currentCell.getColSpan();
            indexInColspan = index < colSpan + i;

            // Increment by colspan to skip over hidden cells
            i += colSpan;
        }
        return currentCell;
    }

    private Element getSubPartElementSpacer(SubPartArguments args) {
        if ("spacer".equals(args.getType()) && args.getIndicesLength() == 1) {
            // If spacer's row is not visible, we need to scroll there.
            // Scrolling might be a no-op if row is already in the viewport.
            scrollToSpacer(args.getIndex(0), ScrollDestination.ANY, 0);

            return body.spacerContainer.getSubPartElement(args.getIndex(0));
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {

        /*
         * The spacer check needs to be before table structure check, because
         * (for now) the table structure will take spacer elements into account
         * as well, when it shouldn't.
         */

        String spacer = getSubPartNameSpacer(subElement);
        if (spacer != null) {
            return spacer;
        }

        String tableStructure = getSubPartNameTableStructure(subElement);
        if (tableStructure != null) {
            return tableStructure;
        }

        return null;
    }

    private String getSubPartNameTableStructure(Element subElement) {

        List<RowContainer> containers = Arrays.asList(getHeader(), getBody(),
                getFooter());
        List<String> containerType = Arrays.asList("header", "cell", "footer");

        for (int i = 0; i < containers.size(); ++i) {
            RowContainer container = containers.get(i);
            boolean containerRow = (subElement.getTagName()
                    .equalsIgnoreCase("tr")
                    && subElement.getParentElement() == container.getElement());
            if (containerRow) {
                /*
                 * Wanted SubPart is row that is a child of containers root to
                 * get indices, we use a cell that is a child of this row
                 */
                subElement = subElement.getFirstChildElement();
            }

            Cell cell = container.getCell(subElement);
            if (cell != null) {
                // Skip the column index if subElement was a child of root
                return containerType.get(i) + "[" + cell.getRow()
                        + (containerRow ? "]" : "][" + cell.getColumn() + "]");
            }
        }
        return null;
    }

    private String getSubPartNameSpacer(Element subElement) {
        return body.spacerContainer.getSubPartName(subElement);
    }

    private void logWarning(String message) {
        getLogger().warning(message);
    }

    /**
     * This is an internal method for calculating minimum width for Column
     * resize.
     *
     * @return minimum width for column
     */
    double getMinCellWidth(int colIndex) {
        return columnConfiguration.getMinCellWidth(colIndex);
    }

    /**
     * Internal method for checking whether the browser is IE11 or Edge
     *
     * @return true only if the current browser is IE11, or Edge
     */
    private static boolean isCurrentBrowserIE11OrEdge() {
        return BrowserInfo.get().isIE11() || BrowserInfo.get().isEdge();
    }
}
