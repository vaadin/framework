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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.client.Duration;
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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
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
import com.vaadin.client.widget.escalator.PositionFunction.AbsolutePosition;
import com.vaadin.client.widget.escalator.PositionFunction.Translate3DPosition;
import com.vaadin.client.widget.escalator.PositionFunction.TranslatePosition;
import com.vaadin.client.widget.escalator.PositionFunction.WebkitTranslate3DPosition;
import com.vaadin.client.widget.escalator.Row;
import com.vaadin.client.widget.escalator.RowContainer;
import com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer;
import com.vaadin.client.widget.escalator.RowVisibilityChangeEvent;
import com.vaadin.client.widget.escalator.RowVisibilityChangeHandler;
import com.vaadin.client.widget.escalator.ScrollbarBundle;
import com.vaadin.client.widget.escalator.ScrollbarBundle.HorizontalScrollbarBundle;
import com.vaadin.client.widget.escalator.ScrollbarBundle.VerticalScrollbarBundle;
import com.vaadin.client.widget.escalator.Spacer;
import com.vaadin.client.widget.escalator.SpacerUpdater;
import com.vaadin.client.widget.grid.events.ScrollEvent;
import com.vaadin.client.widget.grid.events.ScrollHandler;
import com.vaadin.client.widgets.Escalator.JsniUtil.TouchHandlerBundle;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.Range;
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
     * @see Escalator#onScroll()
     * @see Escalator.Scroller#onScroll()
     */
    protected final JavaScriptObject scrollListenerFunction;

    /**
     * A JavaScript function that handles the mousewheel DOM event, and passes
     * it on to Java code.
     * 
     * @see #createMousewheelListenerFunction(Escalator)
     * @see Escalator#onScroll()
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
        mousewheelListenerFunction = createMousewheelListenerFunction(escalator);

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
     * @see Escalator#onScroll()
     */
    protected abstract JavaScriptObject createScrollListenerFunction(
            Escalator esc);

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #mousewheelListenerFunction}.
     * 
     * @param esc
     *            a reference to the current instance of {@link Escalator}
     * @see Escalator#onScroll()
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
public class Escalator extends Widget implements RequiresResize,
        DeferredWorker, SubPartAware {

    // todo comments legend
    /*
     * [[optimize]]: There's an opportunity to rewrite the code in such a way
     * that it _might_ perform better (rememeber to measure, implement,
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

            /**
             * A <a href=
             * "http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html"
             * >JavaScriptObject overlay</a> for the <a
             * href="http://www.w3.org/TR/touch-events/">JavaScript
             * TouchEvent</a> object.
             * <p>
             * This needs to be used in the touch event handlers, since GWT's
             * {@link com.google.gwt.event.dom.client.TouchEvent TouchEvent}
             * can't be cast from the JSNI call, and the
             * {@link com.google.gwt.dom.client.NativeEvent NativeEvent} isn't
             * properly populated with the correct values.
             */
            private final static class CustomTouchEvent extends
                    JavaScriptObject {
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
            final double MIN_VEL = 0.6, MAX_VEL = 4, F_VEL = 1500, F_ACC = 0.7,
                    F_AXIS = 1;

            // The object to deal with one direction scrolling
            private class Movement {
                final List<Double> speeds = new ArrayList<Double>();
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
                        if (speeds.size() > 0 && !validSpeed(speeds.get(0))) {
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
                if (event.getNativeEvent().getTouches().length() == 1) {
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
                    touching = true;
                } else {
                    touching = false;
                    animation.cancel();
                    acceleration = 1;
                }
            }

            public void touchMove(final CustomTouchEvent event) {
                if (touching) {
                    xMov.moveTouch(event);
                    yMov.moveTouch(event);
                    xMov.validate(yMov);
                    yMov.validate(xMov);
                    event.getNativeEvent().preventDefault();
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

            if (!Double.isNaN(deltaX)) {
                escalator.horizontalScrollbar.setScrollPosByDelta(deltaX);
            }

            if (!Double.isNaN(deltaY)) {
                escalator.verticalScrollbar.setScrollPosByDelta(deltaY);
            }

            /*
             * TODO: only prevent if not scrolled to end/bottom. Or no? UX team
             * needs to decide.
             */
            final boolean warrantedYScroll = deltaY != 0
                    && escalator.verticalScrollbar.showsScrollHandle();
            final boolean warrantedXScroll = deltaX != 0
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
                var target = e.target || e.srcElement; // IE8 uses e.scrElement

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
                if(e.deltaMode === 1) {
                    var brc = esc.@com.vaadin.client.widgets.Escalator::body;
                    deltaY *= brc.@com.vaadin.client.widgets.Escalator.AbstractRowContainer::getDefaultRowHeight()();
                }

                // Other delta modes aren't supported
                if((e.deltaMode !== undefined) && (e.deltaMode >= 2 || e.deltaMode < 0)) {
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
                    + WidgetUtil.PIXEL_EPSILON
                    - header.getHeightOfSection()
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
            double vScrollbarHeight = Math.max(0, tableWrapperHeight
                    - footerHeight - headerHeight);
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
            horizontalScrollbar.getElement().getStyle()
                    .setLeft(frozenPixels, Unit.PX);
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

                /*
                 * TODO [[optimize]]: cache this value in case the instanceof
                 * check has undesirable overhead. This could also be a
                 * candidate for some deferred binding magic so that e.g.
                 * AbsolutePosition is not even considered in permutations that
                 * we know support something better. That would let the compiler
                 * completely remove the entire condition since it knows that
                 * the if will never be true.
                 */
                if (position instanceof AbsolutePosition) {
                    /*
                     * we don't want to put "top: 0" on the footer, since it'll
                     * render wrong, as we already have
                     * "bottom: $footer-height".
                     */
                    footElem.getStyle().setLeft(-scrollLeft, Unit.PX);
                } else {
                    position.set(footElem, -scrollLeft, 0);
                }

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
         * Attaching events with JSNI instead of the GWT event mechanism because
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
            if (element.addEventListener) {
                // firefox likes "wheel", while others use "mousewheel"
                var eventName = 'onmousewheel' in element ? 'mousewheel' : 'wheel';
                element.addEventListener(eventName, this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
            } else {
                // IE8
                element.attachEvent("onmousewheel", this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
            }
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
            if (element.addEventListener) {
                // firefox likes "wheel", while others use "mousewheel"
                var eventName = element.onwheel===undefined?"mousewheel":"wheel";
                element.removeEventListener(eventName, this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
            } else {
                // IE8
                element.detachEvent("onmousewheel", this.@com.vaadin.client.widgets.JsniWorkaround::mousewheelListenerFunction);
            }
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
            if (element.addEventListener) {
                element.addEventListener("touchstart", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
                element.addEventListener("touchmove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
                element.addEventListener("touchend", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
                element.addEventListener("touchcancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            } else {
                // this would be IE8, but we don't support it with touch
            }
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
            if (element.removeEventListener) {
                element.removeEventListener("touchstart", this.@com.vaadin.client.widgets.JsniWorkaround::touchStartFunction);
                element.removeEventListener("touchmove", this.@com.vaadin.client.widgets.JsniWorkaround::touchMoveFunction);
                element.removeEventListener("touchend", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
                element.removeEventListener("touchcancel", this.@com.vaadin.client.widgets.JsniWorkaround::touchEndFunction);
            } else {
                // this would be IE8, but we don't support it with touch
            }
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
                    + WidgetUtil
                            .getRequiredWidthBoundingClientRectDouble(getElement())
                    - frozenPixels;
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

            final double targetStartPx = (body.getDefaultRowHeight() * rowIndex)
                    + body.spacerContainer
                            .getSpacerHeightsSumUntilIndex(rowIndex);
            final double targetEndPx = targetStartPx
                    + body.getDefaultRowHeight();

            final double viewportStartPx = getScrollTop();
            final double viewportEndPx = viewportStartPx
                    + body.getHeightOfSection();

            final double scrollTop = getScrollPos(destination, targetStartPx,
                    targetEndPx, viewportStartPx, viewportEndPx, padding);

            /*
             * note that it doesn't matter if the scroll would go beyond the
             * content, since the browser will adjust for that, and everything
             * falls into line accordingly.
             */
            setScrollTop(scrollTop);
        }
    }

    protected abstract class AbstractRowContainer implements RowContainer {
        private EscalatorUpdater updater = EscalatorUpdater.NULL;

        private int rows;

        /**
         * The table section element ({@code <thead>}, {@code <tbody>} or
         * {@code <tfoot>}) the rows (i.e. {@code <tr>} tags) are contained in.
         */
        protected final TableSectionElement root;

        /**
         * The primary style name of the escalator. Most commonly provided by
         * Escalator as "v-escalator".
         */
        private String primaryStyleName = null;

        private boolean defaultRowHeightShouldBeAutodetected = true;

        private double defaultRowHeight = INITIAL_DEFAULT_ROW_HEIGHT;

        public AbstractRowContainer(
                final TableSectionElement rowContainerElement) {
            root = rowContainerElement;
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
         * {@link #createCellElement(int, int)} instead.
         * 
         * @return the tag name for the element to represent cells as
         * @see #createCellElement(int, int)
         */
        protected abstract String getCellElementTagName();

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
        public void setEscalatorUpdater(final EscalatorUpdater escalatorUpdater) {
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
         * The implementation must call {@link #paintRemoveRow(Element, int)}
         * for each row that is removed from the DOM.
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
                final int numberOfRows) throws IllegalArgumentException,
                IndexOutOfBoundsException {
            if (numberOfRows < 1) {
                throw new IllegalArgumentException(
                        "Number of rows must be 1 or greater (was "
                                + numberOfRows + ")");
            }

            if (index < 0 || index + numberOfRows > getRowCount()) {
                throw new IndexOutOfBoundsException("The given "
                        + "row range (" + index + ".." + (index + numberOfRows)
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

            /*
             * only add items in the DOM if the widget itself is attached to the
             * DOM. We can't calculate sizes otherwise.
             */
            if (isAttached()) {
                paintInsertRows(index, numberOfRows);

                if (rows == numberOfRows) {
                    /*
                     * We are inserting the first rows in this container. We
                     * potentially need to set the widths for the cells for the
                     * first time.
                     */
                    Map<Integer, Double> colWidths = new HashMap<Integer, Double>();
                    for (int i = 0; i < getColumnConfiguration()
                            .getColumnCount(); i++) {
                        Double width = Double.valueOf(getColumnConfiguration()
                                .getColumnWidth(i));
                        Integer col = Integer.valueOf(i);
                        colWidths.put(col, width);
                    }
                    getColumnConfiguration().setColumnWidths(colWidths);
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
         * @return a list of the added row elements
         */
        protected abstract void paintInsertRows(final int visualIndex,
                final int numberOfRows);

        protected List<TableRowElement> paintInsertStaticRows(
                final int visualIndex, final int numberOfRows) {
            assert isAttached() : "Can't paint rows if Escalator is not attached";

            final List<TableRowElement> addedRows = new ArrayList<TableRowElement>();

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

            for (int row = visualIndex; row < visualIndex + numberOfRows; row++) {
                final TableRowElement tr = TableRowElement.as(DOM.createTR());
                addedRows.add(tr);
                tr.addClassName(getStylePrimaryName() + "-row");

                for (int col = 0; col < columnConfiguration.getColumnCount(); col++) {
                    final double colWidth = columnConfiguration
                            .getColumnWidthActual(col);
                    final TableCellElement cellElem = createCellElement(colWidth);
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

        abstract protected void recalculateSectionHeight();

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
            Range colRange = Range.withLength(0, getColumnConfiguration()
                    .getColumnCount());
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
            Iterable<FlyweightCell> cellsToUpdate = flyweightRow.getCells(
                    colRange.getStart(), colRange.length());
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
            final TableCellElement cellElem = TableCellElement.as(DOM
                    .createElement(getCellElementTagName()));

            final double height = getDefaultRowHeight();
            assert height >= 0 : "defaultRowHeight was negative. There's a setter leak somewhere.";
            cellElem.getStyle().setHeight(height, Unit.PX);

            if (width >= 0) {
                cellElem.getStyle().setWidth(width, Unit.PX);
            }
            cellElem.addClassName(getStylePrimaryName() + "-cell");
            return cellElem;
        }

        @Override
        public TableRowElement getRowElement(int index) {
            return getTrByVisualIndex(index);
        }

        /**
         * Gets the child element that is visually at a certain index
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

                Iterable<FlyweightCell> attachedCells = flyweightRow.getCells(
                        offset, numberOfColumns);
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
                int logicalRowIndex, final int offset, final int numberOfCells) {

            assert root.isOrHasChild(tr) : "The row must be attached to the document";

            flyweightRow.setup(tr, logicalRowIndex,
                    columnConfiguration.getCalculatedColumnWidths());

            Iterable<FlyweightCell> cells = flyweightRow.getUnattachedCells(
                    offset, numberOfCells);

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
         *            the row element to check for if it is or has elements that
         *            can be frozen
         * @return <code>true</code> iff this the given element, or any of its
         *         descendants, can be frozen
         */
        abstract protected boolean rowCanBeFrozen(TableRowElement tr);

        /**
         * Iterates through all the cells in a column and returns the width of
         * the widest element in this RowContainer.
         * 
         * @param index
         *            the index of the column to inspect
         * @return the pixel width of the widest element in the indicated column
         */
        public double calculateMaxColWidth(int index) {
            TableRowElement row = TableRowElement.as(root
                    .getFirstChildElement());
            double maxWidth = 0;
            while (row != null) {
                final TableCellElement cell = row.getCells().getItem(index);
                final boolean isVisible = !cell.getStyle().getDisplay()
                        .equals(Display.NONE.getCssName());
                if (isVisible) {
                    maxWidth = Math.max(maxWidth, WidgetUtil
                            .getRequiredWidthBoundingClientRectDouble(cell));
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
            if (spannedColumns.getEnd() > columnConfiguration.getColumnCount()) {
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
         * method only modifies the width of the {@code <tr>} element, not the
         * cells within.
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
                    UIObject.setStylePrimaryName(cell, primaryStyleName
                            + "-cell");
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
                throw new IllegalArgumentException("Height must be positive. "
                        + px + " was given.");
            }

            defaultRowHeightShouldBeAutodetected = false;
            defaultRowHeight = px;
            reapplyDefaultRowHeights();
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

        public void autodetectRowHeightLater() {
            Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    if (defaultRowHeightShouldBeAutodetected && isAttached()) {
                        autodetectRowHeightNow();
                        defaultRowHeightShouldBeAutodetected = false;
                    }
                }
            });
        }

        public void autodetectRowHeightNow() {
            if (!isAttached()) {
                // Run again when attached
                defaultRowHeightShouldBeAutodetected = true;
                return;
            }

            final Element detectionTr = DOM.createTR();
            detectionTr.setClassName(getStylePrimaryName() + "-row");

            final Element cellElem = DOM.createElement(getCellElementTagName());
            cellElem.setClassName(getStylePrimaryName() + "-cell");
            cellElem.setInnerText("Ij");

            detectionTr.appendChild(cellElem);
            root.appendChild(detectionTr);
            double boundingHeight = WidgetUtil
                    .getRequiredHeightBoundingClientRectDouble(cellElem);
            defaultRowHeight = Math.max(1.0d, boundingHeight);
            root.removeChild(detectionTr);

            if (root.hasChildNodes()) {
                reapplyDefaultRowHeights();
                applyHeightByRows();
            }
        }

        @Override
        public Cell getCell(final Element element) {
            if (element == null) {
                throw new IllegalArgumentException("Element cannot be null");
            }

            /*
             * Ensure that element is not root nor the direct descendant of root
             * (a row) and ensure the element is inside the dom hierarchy of the
             * root element. If not, return.
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
            while (cellElementCandidate.getParentElement().getParentElement() != root) {
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

            TableCellElement cellClone = TableCellElement.as((Element) cell
                    .cloneNode(withContent));
            cellClone.getStyle().clearHeight();
            cellClone.getStyle().clearWidth();

            cell.getParentElement().insertBefore(cellClone, cell);
            double requiredWidth = WidgetUtil
                    .getRequiredWidthBoundingClientRectDouble(cellClone);
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
            boolean cellIsHidden = Display.NONE.getCssName().equals(
                    cell.getStyle().getDisplay());
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

        protected int getLogicalRowIndex(final TableRowElement tr) {
            return tr.getSectionRowIndex();
        };

    }

    private abstract class AbstractStaticRowContainer extends
            AbstractRowContainer {

        /** The height of the combined rows in the DOM. Never negative. */
        private double heightOfSection = 0;

        public AbstractStaticRowContainer(final TableSectionElement headElement) {
            super(headElement);
        }

        @Override
        public int getDomRowCount() {
            return root.getChildCount();
        }

        @Override
        protected void paintRemoveRows(final int index, final int numberOfRows) {
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
                throw new IndexOutOfBoundsException("No such visual index: "
                        + index);
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

            Profiler.enter("Escalator.AbstractStaticRowContainer.reapplyDefaultRowHeights");

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

            Profiler.leave("Escalator.AbstractStaticRowContainer.reapplyDefaultRowHeights");
        }

        @Override
        protected void recalculateSectionHeight() {
            Profiler.enter("Escalator.AbstractStaticRowContainer.recalculateSectionHeight");

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
                verticalScrollbar.setOffsetSize(heightOfEscalator
                        - header.getHeightOfSection()
                        - footer.getHeightOfSection());

                body.verifyEscalatorCount();
                body.spacerContainer.updateSpacerDecosVisibility();
            }

            Profiler.leave("Escalator.AbstractStaticRowContainer.recalculateSectionHeight");
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
            Profiler.enter("Escalator.AbstractStaticRowContainer.refreshRows");

            assertArgumentsAreValidAndWithinRange(logicalRowRange.getStart(),
                    logicalRowRange.length());

            if (!isAttached()) {
                return;
            }

            if (hasColumnAndRowData()) {
                for (int row = logicalRowRange.getStart(); row < logicalRowRange
                        .getEnd(); row++) {
                    final TableRowElement tr = getTrByVisualIndex(row);
                    refreshRow(tr, row, colRange);
                }
            }

            Profiler.leave("Escalator.AbstractStaticRowContainer.refreshRows");
        }

        @Override
        protected void paintInsertRows(int visualIndex, int numberOfRows) {
            paintInsertStaticRows(visualIndex, numberOfRows);
        }

        @Override
        protected boolean rowCanBeFrozen(TableRowElement tr) {
            assert root.isOrHasChild(tr) : "Row does not belong to this table section";
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
            verticalScrollbar.getElement().getStyle()
                    .setTop(heightOfSection, Unit.PX);
            headerDeco.getStyle().setHeight(heightOfSection, Unit.PX);
        }

        @Override
        protected String getCellElementTagName() {
            return "th";
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
            int vscrollHeight = (int) Math.floor(heightOfEscalator
                    - headerHeight - footerHeight);

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

    private class BodyRowContainerImpl extends AbstractRowContainer implements
            BodyRowContainer {
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
        private final LinkedList<TableRowElement> visualRowOrder = new LinkedList<TableRowElement>();

        /**
         * The logical index of the topmost row.
         * 
         * @deprecated Use the accessors {@link #setTopRowLogicalIndex(int)},
         *             {@link #updateTopRowLogicalIndex(int)} and
         *             {@link #getTopRowLogicalIndex()} instead
         */
        @Deprecated
        private int topRowLogicalIndex = 0;

        private void setTopRowLogicalIndex(int topRowLogicalIndex) {
            if (LogConfiguration.loggingIsEnabled(Level.INFO)) {
                Logger.getLogger("Escalator.BodyRowContainer").fine(
                        "topRowLogicalIndex: " + this.topRowLogicalIndex
                                + " -> " + topRowLogicalIndex);
            }
            assert topRowLogicalIndex >= 0 : "topRowLogicalIndex became negative (top left cell contents: "
                    + visualRowOrder.getFirst().getCells().getItem(0)
                            .getInnerText() + ") ";
            /*
             * if there's a smart way of evaluating and asserting the max index,
             * this would be a nice place to put it. I haven't found out an
             * effective and generic solution.
             */

            this.topRowLogicalIndex = topRowLogicalIndex;
        }

        public int getTopRowLogicalIndex() {
            return topRowLogicalIndex;
        }

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
                boolean enoughTimeHasPassed = (Duration.currentTimeMillis() - startTime) >= SORT_DELAY_MILLIS;
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
            final double viewportOffset = topElementPosition - scrollTop;

            /*
             * TODO [[optimize]] this if-else can most probably be refactored
             * into a neater block of code
             */

            if (viewportOffset > 0) {
                // there's empty room on top

                double rowPx = getRowHeightsSumBetweenPx(scrollTop,
                        topElementPosition);
                int originalRowsToMove = (int) Math.ceil(rowPx
                        / getDefaultRowHeight());
                int rowsToMove = Math.min(originalRowsToMove,
                        visualRowOrder.size());

                final int end = visualRowOrder.size();
                final int start = end - rowsToMove;
                final int logicalRowIndex = getLogicalRowIndex(scrollTop);

                moveAndUpdateEscalatorRows(Range.between(start, end), 0,
                        logicalRowIndex);

                setTopRowLogicalIndex(logicalRowIndex);

                rowsWereMoved = true;
            }

            else if (viewportOffset + nextRowBottomOffset <= 0) {
                /*
                 * the viewport has been scrolled more than the topmost visual
                 * row.
                 */

                double rowPx = getRowHeightsSumBetweenPx(topElementPosition,
                        scrollTop);

                int originalRowsToMove = (int) (rowPx / getDefaultRowHeight());
                int rowsToMove = Math.min(originalRowsToMove,
                        visualRowOrder.size());

                int logicalRowIndex;
                if (rowsToMove < visualRowOrder.size()) {
                    /*
                     * We scroll so little that we can just keep adding the rows
                     * below the current escalator
                     */
                    logicalRowIndex = getLogicalRowIndex(visualRowOrder
                            .getLast()) + 1;
                } else {
                    /*
                     * Since we're moving all escalator rows, we need to
                     * calculate the first logical row index from the scroll
                     * position.
                     */
                    logicalRowIndex = getLogicalRowIndex(scrollTop);
                }

                /*
                 * Since we're moving the viewport downwards, the visual index
                 * is always at the bottom. Note: Due to how
                 * moveAndUpdateEscalatorRows works, this will work out even if
                 * we move all the rows, and try to place them "at the end".
                 */
                final int targetVisualIndex = visualRowOrder.size();

                // make sure that we don't move rows over the data boundary
                boolean aRowWasLeftBehind = false;
                if (logicalRowIndex + rowsToMove > getRowCount()) {
                    /*
                     * TODO [[spacer]]: with constant row heights, there's
                     * always exactly one row that will be moved beyond the data
                     * source, when viewport is scrolled to the end. This,
                     * however, isn't guaranteed anymore once row heights start
                     * varying.
                     */
                    rowsToMove--;
                    aRowWasLeftBehind = true;
                }

                /*
                 * Make sure we don't scroll beyond the row content. This can
                 * happen if we have spacers for the last rows.
                 */
                rowsToMove = Math.max(0,
                        Math.min(rowsToMove, getRowCount() - logicalRowIndex));

                moveAndUpdateEscalatorRows(Range.between(0, rowsToMove),
                        targetVisualIndex, logicalRowIndex);

                if (aRowWasLeftBehind) {
                    /*
                     * To keep visualRowOrder as a spatially contiguous block of
                     * rows, let's make sure that the one row we didn't move
                     * visually still stays with the pack.
                     */
                    final Range strayRow = Range.withOnly(0);

                    /*
                     * We cannot trust getLogicalRowIndex, because it hasn't yet
                     * been updated. But since we're leaving rows behind, it
                     * means we've scrolled to the bottom. So, instead, we
                     * simply count backwards from the end.
                     */
                    final int topLogicalIndex = getRowCount()
                            - visualRowOrder.size();
                    moveAndUpdateEscalatorRows(strayRow, 0, topLogicalIndex);
                }

                final int naiveNewLogicalIndex = getTopRowLogicalIndex()
                        + originalRowsToMove;
                final int maxLogicalIndex = getRowCount()
                        - visualRowOrder.size();
                setTopRowLogicalIndex(Math.min(naiveNewLogicalIndex,
                        maxLogicalIndex));

                rowsWereMoved = true;
            }

            if (rowsWereMoved) {
                fireRowVisibilityChangeEvent();
                domSorter.reschedule();
            }
        }

        private double getRowHeightsSumBetweenPx(double y1, double y2) {
            assert y1 < y2 : "y1 must be smaller than y2";

            double viewportPx = y2 - y1;
            double spacerPx = spacerContainer.getSpacerHeightsSumBetweenPx(y1,
                    SpacerInclusionStrategy.PARTIAL, y2,
                    SpacerInclusionStrategy.PARTIAL);

            return viewportPx - spacerPx;
        }

        private int getLogicalRowIndex(final double px) {
            double rowPx = px - spacerContainer.getSpacerHeightsSumUntilPx(px);
            return (int) (rowPx / getDefaultRowHeight());
        }

        @Override
        protected void paintInsertRows(final int index, final int numberOfRows) {
            if (numberOfRows == 0) {
                return;
            }

            spacerContainer.shiftSpacersByRows(index, numberOfRows);

            /*
             * TODO: this method should probably only add physical rows, and not
             * populate them - let everything be populated as appropriate by the
             * logic that follows.
             * 
             * This also would lead to the fact that paintInsertRows wouldn't
             * need to return anything.
             */
            final List<TableRowElement> addedRows = fillAndPopulateEscalatorRowsIfNeeded(
                    index, numberOfRows);

            /*
             * insertRows will always change the number of rows - update the
             * scrollbar sizes.
             */
            scroller.recalculateScrollbarsForVirtualViewport();

            final boolean addedRowsAboveCurrentViewport = index
                    * getDefaultRowHeight() < getScrollTop();
            final boolean addedRowsBelowCurrentViewport = index
                    * getDefaultRowHeight() > getScrollTop()
                    + getHeightOfSection();

            if (addedRowsAboveCurrentViewport) {
                /*
                 * We need to tweak the virtual viewport (scroll handle
                 * positions, table "scroll position" and row locations), but
                 * without re-evaluating any rows.
                 */

                final double yDelta = numberOfRows * getDefaultRowHeight();
                moveViewportAndContent(yDelta);
                updateTopRowLogicalIndex(numberOfRows);
            }

            else if (addedRowsBelowCurrentViewport) {
                // NOOP, we already recalculated scrollbars.
            }

            else { // some rows were added inside the current viewport

                final int unupdatedLogicalStart = index + addedRows.size();
                final int visualOffset = getLogicalRowIndex(visualRowOrder
                        .getFirst());

                /*
                 * At this point, we have added new escalator rows, if so
                 * needed.
                 * 
                 * If more rows were added than the new escalator rows can
                 * account for, we need to start to spin the escalator to update
                 * the remaining rows aswell.
                 */
                final int rowsStillNeeded = numberOfRows - addedRows.size();

                if (rowsStillNeeded > 0) {
                    final Range unupdatedVisual = convertToVisual(Range
                            .withLength(unupdatedLogicalStart, rowsStillNeeded));
                    final int end = getDomRowCount();
                    final int start = end - unupdatedVisual.length();
                    final int visualTargetIndex = unupdatedLogicalStart
                            - visualOffset;
                    moveAndUpdateEscalatorRows(Range.between(start, end),
                            visualTargetIndex, unupdatedLogicalStart);

                    // move the surrounding rows to their correct places.
                    double rowTop = (unupdatedLogicalStart + (end - start))
                            * getDefaultRowHeight();

                    // TODO: Get rid of this try/catch block by fixing the
                    // underlying issue. The reason for this erroneous behavior
                    // might be that Escalator actually works 'by mistake', and
                    // the order of operations is, in fact, wrong.
                    try {
                        final ListIterator<TableRowElement> i = visualRowOrder
                                .listIterator(visualTargetIndex + (end - start));

                        int logicalRowIndexCursor = unupdatedLogicalStart;
                        while (i.hasNext()) {
                            rowTop += spacerContainer
                                    .getSpacerHeight(logicalRowIndexCursor++);

                            final TableRowElement tr = i.next();
                            setRowPosition(tr, 0, rowTop);
                            rowTop += getDefaultRowHeight();
                        }
                    } catch (Exception e) {
                        Logger logger = getLogger();
                        logger.warning("Ignored out-of-bounds row element access");
                        logger.warning("Escalator state: start=" + start
                                + ", end=" + end + ", visualTargetIndex="
                                + visualTargetIndex
                                + ", visualRowOrder.size()="
                                + visualRowOrder.size());
                        logger.warning(e.toString());
                    }
                }

                fireRowVisibilityChangeEvent();
                sortDomElements();
            }
        }

        /**
         * Move escalator rows around, and make sure everything gets
         * appropriately repositioned and repainted.
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

            assert visualSourceRange.getStart() >= 0 : "Visual source start "
                    + "must be 0 or greater (was "
                    + visualSourceRange.getStart() + ")";

            assert logicalTargetIndex >= 0 : "Logical target must be 0 or "
                    + "greater (was " + logicalTargetIndex + ")";

            assert visualTargetIndex >= 0 : "Visual target must be 0 or greater (was "
                    + visualTargetIndex + ")";

            assert visualTargetIndex <= getDomRowCount() : "Visual target "
                    + "must not be greater than the number of escalator rows (was "
                    + visualTargetIndex + ", escalator rows "
                    + getDomRowCount() + ")";

            assert logicalTargetIndex + visualSourceRange.length() <= getRowCount() : "Logical "
                    + "target leads to rows outside of the data range ("
                    + Range.withLength(logicalTargetIndex,
                            visualSourceRange.length())
                    + " goes beyond "
                    + Range.withLength(0, getRowCount()) + ")";

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
                        - visualSourceRange.length();
            } else {
                adjustedVisualTargetIndex = visualTargetIndex;
            }

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

                final List<TableRowElement> removedRows = new ArrayList<TableRowElement>(
                        visualSourceRange.length());
                for (int i = 0; i < visualSourceRange.length(); i++) {
                    final TableRowElement tr = visualRowOrder
                            .remove(visualSourceRange.getStart());
                    removedRows.add(tr);
                }
                visualRowOrder.addAll(adjustedVisualTargetIndex, removedRows);
            }

            { // Refresh the contents of the affected rows
                final ListIterator<TableRowElement> iter = visualRowOrder
                        .listIterator(adjustedVisualTargetIndex);
                for (int logicalIndex = logicalTargetIndex; logicalIndex < logicalTargetIndex
                        + visualSourceRange.length(); logicalIndex++) {
                    final TableRowElement tr = iter.next();
                    refreshRow(tr, logicalIndex);
                }
            }

            { // Reposition the rows that were moved
                double newRowTop = getRowTop(logicalTargetIndex);

                final ListIterator<TableRowElement> iter = visualRowOrder
                        .listIterator(adjustedVisualTargetIndex);
                for (int i = 0; i < visualSourceRange.length(); i++) {
                    final TableRowElement tr = iter.next();
                    setRowPosition(tr, 0, newRowTop);

                    newRowTop += getDefaultRowHeight();
                    newRowTop += spacerContainer
                            .getSpacerHeight(logicalTargetIndex + i);
                }
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
         * @param yDelta
         *            the delta of pixels by which to move the viewport and
         *            content. A positive value moves everything downwards,
         *            while a negative value moves everything upwards
         */
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
         * Adds new physical escalator rows to the DOM at the given index if
         * there's still a need for more escalator rows.
         * <p>
         * If Escalator already is at (or beyond) max capacity, this method does
         * nothing to the DOM.
         * 
         * @param index
         *            the index at which to add new escalator rows.
         *            <em>Note:</em>It is assumed that the index is both the
         *            visual index and the logical index.
         * @param numberOfRows
         *            the number of rows to add at <code>index</code>
         * @return a list of the added rows
         */
        private List<TableRowElement> fillAndPopulateEscalatorRowsIfNeeded(
                final int index, final int numberOfRows) {

            final int escalatorRowsStillFit = getMaxEscalatorRowCapacity()
                    - getDomRowCount();
            final int escalatorRowsNeeded = Math.min(numberOfRows,
                    escalatorRowsStillFit);

            if (escalatorRowsNeeded > 0) {

                final List<TableRowElement> addedRows = paintInsertStaticRows(
                        index, escalatorRowsNeeded);
                visualRowOrder.addAll(index, addedRows);

                double y = index * getDefaultRowHeight()
                        + spacerContainer.getSpacerHeightsSumUntilIndex(index);
                for (int i = index; i < visualRowOrder.size(); i++) {

                    final TableRowElement tr;
                    if (i - index < addedRows.size()) {
                        tr = addedRows.get(i - index);
                    } else {
                        tr = visualRowOrder.get(i);
                    }

                    setRowPosition(tr, 0, y);
                    y += getDefaultRowHeight();
                    y += spacerContainer.getSpacerHeight(i);
                }

                return addedRows;
            } else {
                return Collections.emptyList();
            }
        }

        private int getMaxEscalatorRowCapacity() {
            final int maxEscalatorRowCapacity = (int) Math
                    .ceil(getHeightOfSection() / getDefaultRowHeight()) + 1;

            /*
             * maxEscalatorRowCapacity can become negative if the headers and
             * footers start to overlap. This is a crazy situation, but Vaadin
             * blinks the components a lot, so it's feasible.
             */
            return Math.max(0, maxEscalatorRowCapacity);
        }

        @Override
        protected void paintRemoveRows(final int index, final int numberOfRows) {
            if (numberOfRows == 0) {
                return;
            }

            final Range viewportRange = getVisibleRowRange();
            final Range removedRowsRange = Range
                    .withLength(index, numberOfRows);

            /*
             * Removing spacers as the very first step will correct the
             * scrollbars and row offsets right away.
             * 
             * TODO: actually, it kinda sounds like a Grid feature that a spacer
             * would be associated with a particular row. Maybe it would be
             * better to have a spacer separate from rows, and simply collapse
             * them if they happen to end up on top of each other. This would
             * probably make supporting the -1 row pretty easy, too.
             */
            spacerContainer.paintRemoveSpacers(removedRowsRange);

            final Range[] partitions = removedRowsRange
                    .partitionWith(viewportRange);
            final Range removedAbove = partitions[0];
            final Range removedLogicalInside = partitions[1];
            final Range removedVisualInside = convertToVisual(removedLogicalInside);

            /*
             * TODO: extract the following if-block to a separate method. I'll
             * leave this be inlined for now, to make linediff-based code
             * reviewing easier. Probably will be moved in the following patch
             * set.
             */

            /*
             * Adjust scroll position in one of two scenarios:
             * 
             * 1) Rows were removed above. Then we just need to adjust the
             * scrollbar by the height of the removed rows.
             * 
             * 2) There are no logical rows above, and at least the first (if
             * not more) visual row is removed. Then we need to snap the scroll
             * position to the first visible row (i.e. reset scroll position to
             * absolute 0)
             * 
             * The logic is optimized in such a way that the
             * moveViewportAndContent is called only once, to avoid extra
             * reflows, and thus the code might seem a bit obscure.
             */
            final boolean firstVisualRowIsRemoved = !removedVisualInside
                    .isEmpty() && removedVisualInside.getStart() == 0;

            if (!removedAbove.isEmpty() || firstVisualRowIsRemoved) {
                final double yDelta = removedAbove.length()
                        * getDefaultRowHeight();
                final double firstLogicalRowHeight = getDefaultRowHeight();
                final boolean removalScrollsToShowFirstLogicalRow = verticalScrollbar
                        .getScrollPos() - yDelta < firstLogicalRowHeight;

                if (removedVisualInside.isEmpty()
                        && (!removalScrollsToShowFirstLogicalRow || !firstVisualRowIsRemoved)) {
                    /*
                     * rows were removed from above the viewport, so all we need
                     * to do is to adjust the scroll position to account for the
                     * removed rows
                     */
                    moveViewportAndContent(-yDelta);
                } else if (removalScrollsToShowFirstLogicalRow) {
                    /*
                     * It seems like we've removed all rows from above, and also
                     * into the current viewport. This means we'll need to even
                     * out the scroll position to exactly 0 (i.e. adjust by the
                     * current negative scrolltop, presto!), so that it isn't
                     * aligned funnily
                     */
                    moveViewportAndContent(-verticalScrollbar.getScrollPos());
                }
            }

            // ranges evaluated, let's do things.
            if (!removedVisualInside.isEmpty()) {
                int escalatorRowCount = body.getDomRowCount();

                /*
                 * remember: the rows have already been subtracted from the row
                 * count at this point
                 */
                int rowsLeft = getRowCount();
                if (rowsLeft < escalatorRowCount) {
                    int escalatorRowsToRemove = escalatorRowCount - rowsLeft;
                    for (int i = 0; i < escalatorRowsToRemove; i++) {
                        final TableRowElement tr = visualRowOrder
                                .remove(removedVisualInside.getStart());

                        paintRemoveRow(tr, index);
                        removeRowPosition(tr);
                    }
                    escalatorRowCount -= escalatorRowsToRemove;

                    /*
                     * Because we're removing escalator rows, we don't have
                     * anything to scroll by. Let's make sure the viewport is
                     * scrolled to top, to render any rows possibly left above.
                     */
                    body.setBodyScrollPosition(tBodyScrollLeft, 0);

                    /*
                     * We might have removed some rows from the middle, so let's
                     * make sure we're not left with any holes. Also remember:
                     * visualIndex == logicalIndex applies now.
                     */
                    final int dirtyRowsStart = removedLogicalInside.getStart();
                    double y = getRowTop(dirtyRowsStart);
                    for (int i = dirtyRowsStart; i < escalatorRowCount; i++) {
                        final TableRowElement tr = visualRowOrder.get(i);
                        setRowPosition(tr, 0, y);
                        y += getDefaultRowHeight();
                        y += spacerContainer.getSpacerHeight(i);
                    }

                    /*
                     * this is how many rows appeared into the viewport from
                     * below
                     */
                    final int rowsToUpdateDataOn = numberOfRows
                            - escalatorRowsToRemove;
                    final int start = Math.max(0, escalatorRowCount
                            - rowsToUpdateDataOn);
                    final int end = escalatorRowCount;
                    for (int i = start; i < end; i++) {
                        final TableRowElement tr = visualRowOrder.get(i);
                        refreshRow(tr, i);
                    }
                }

                else {
                    // No escalator rows need to be removed.

                    /*
                     * Two things (or a combination thereof) can happen:
                     * 
                     * 1) We're scrolled to the bottom, the last rows are
                     * removed. SOLUTION: moveAndUpdateEscalatorRows the
                     * bottommost rows, and place them at the top to be
                     * refreshed.
                     * 
                     * 2) We're scrolled somewhere in the middle, arbitrary rows
                     * are removed. SOLUTION: moveAndUpdateEscalatorRows the
                     * removed rows, and place them at the bottom to be
                     * refreshed.
                     * 
                     * Since a combination can also happen, we need to handle
                     * this in a smart way, all while avoiding
                     * double-refreshing.
                     */

                    final double contentBottom = getRowCount()
                            * getDefaultRowHeight();
                    final double viewportBottom = tBodyScrollTop
                            + getHeightOfSection();
                    if (viewportBottom <= contentBottom) {
                        /*
                         * We're in the middle of the row container, everything
                         * is added to the bottom
                         */
                        paintRemoveRowsAtMiddle(removedLogicalInside,
                                removedVisualInside, 0);
                    }

                    else if (removedVisualInside.contains(0)
                            && numberOfRows >= visualRowOrder.size()) {
                        /*
                         * We're removing so many rows that the viewport is
                         * pushed up more than a screenful. This means we can
                         * simply scroll up and everything will work without a
                         * sweat.
                         */

                        double left = horizontalScrollbar.getScrollPos();
                        double top = contentBottom - visualRowOrder.size()
                                * getDefaultRowHeight();
                        setBodyScrollPosition(left, top);

                        Range allEscalatorRows = Range.withLength(0,
                                visualRowOrder.size());
                        int logicalTargetIndex = getRowCount()
                                - allEscalatorRows.length();
                        moveAndUpdateEscalatorRows(allEscalatorRows, 0,
                                logicalTargetIndex);

                        /*
                         * moveAndUpdateEscalatorRows recalculates the rows, but
                         * logical top row index bookkeeping is handled in this
                         * method.
                         * 
                         * TODO: Redesign how to keep it easy to track this.
                         */
                        updateTopRowLogicalIndex(-removedLogicalInside.length());

                        /*
                         * Scrolling the body to the correct location will be
                         * fixed automatically. Because the amount of rows is
                         * decreased, the viewport is pushed up as the scrollbar
                         * shrinks. So no need to do anything there.
                         * 
                         * TODO [[optimize]]: This might lead to a double body
                         * refresh. Needs investigation.
                         */
                    }

                    else if (contentBottom
                            + (numberOfRows * getDefaultRowHeight())
                            - viewportBottom < getDefaultRowHeight()) {
                        /*
                         * We're at the end of the row container, everything is
                         * added to the top.
                         */

                        /*
                         * FIXME [[spacer]]: above if-clause is coded to only
                         * work with default row heights - will not work with
                         * variable row heights
                         */

                        paintRemoveRowsAtBottom(removedLogicalInside,
                                removedVisualInside);
                        updateTopRowLogicalIndex(-removedLogicalInside.length());
                    }

                    else {
                        /*
                         * We're in a combination, where we need to both scroll
                         * up AND show new rows at the bottom.
                         * 
                         * Example: Scrolled down to show the second to last
                         * row. Remove two. Viewport scrolls up, revealing the
                         * row above row. The last element collapses up and into
                         * view.
                         * 
                         * Reminder: this use case handles only the case when
                         * there are enough escalator rows to still render a
                         * full view. I.e. all escalator rows will _always_ be
                         * populated
                         */
                        /*-
                         *  1       1      |1| <- newly rendered
                         * |2|     |2|     |2|
                         * |3| ==> |*| ==> |5| <- newly rendered
                         * |4|     |*|
                         *  5       5
                         *  
                         *  1       1      |1| <- newly rendered
                         * |2|     |*|     |4|
                         * |3| ==> |*| ==> |5| <- newly rendered
                         * |4|     |4|
                         *  5       5
                         */

                        /*
                         * STEP 1:
                         * 
                         * reorganize deprecated escalator rows to bottom, but
                         * don't re-render anything yet
                         */
                        /*-
                         *  1       1       1
                         * |2|     |*|     |4|
                         * |3| ==> |*| ==> |*|
                         * |4|     |4|     |*|
                         *  5       5       5
                         */
                        double newTop = getRowTop(visualRowOrder
                                .get(removedVisualInside.getStart()));
                        for (int i = 0; i < removedVisualInside.length(); i++) {
                            final TableRowElement tr = visualRowOrder
                                    .remove(removedVisualInside.getStart());
                            visualRowOrder.addLast(tr);
                        }

                        for (int i = removedVisualInside.getStart(); i < escalatorRowCount; i++) {
                            final TableRowElement tr = visualRowOrder.get(i);
                            setRowPosition(tr, 0, (int) newTop);
                            newTop += getDefaultRowHeight();
                            newTop += spacerContainer.getSpacerHeight(i
                                    + removedLogicalInside.getStart());
                        }

                        /*
                         * STEP 2:
                         * 
                         * manually scroll
                         */
                        /*-
                         *  1      |1| <-- newly rendered (by scrolling)
                         * |4|     |4|
                         * |*| ==> |*|
                         * |*|       
                         *  5       5
                         */
                        final double newScrollTop = contentBottom
                                - getHeightOfSection();
                        setScrollTop(newScrollTop);
                        /*
                         * Manually call the scroll handler, so we get immediate
                         * effects in the escalator.
                         */
                        scroller.onScroll();

                        /*
                         * Move the bottommost (n+1:th) escalator row to top,
                         * because scrolling up doesn't handle that for us
                         * automatically
                         */
                        moveAndUpdateEscalatorRows(
                                Range.withOnly(escalatorRowCount - 1),
                                0,
                                getLogicalRowIndex(visualRowOrder.getFirst()) - 1);
                        updateTopRowLogicalIndex(-1);

                        /*
                         * STEP 3:
                         * 
                         * update remaining escalator rows
                         */
                        /*-
                         * |1|     |1|
                         * |4| ==> |4|
                         * |*|     |5| <-- newly rendered
                         *           
                         *  5
                         */

                        final int rowsScrolled = (int) (Math
                                .ceil((viewportBottom - contentBottom)
                                        / getDefaultRowHeight()));
                        final int start = escalatorRowCount
                                - (removedVisualInside.length() - rowsScrolled);
                        final Range visualRefreshRange = Range.between(start,
                                escalatorRowCount);
                        final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                                .getFirst()) + start;
                        // in-place move simply re-renders the rows.
                        moveAndUpdateEscalatorRows(visualRefreshRange, start,
                                logicalTargetIndex);
                    }
                }

                fireRowVisibilityChangeEvent();
                sortDomElements();
            }

            updateTopRowLogicalIndex(-removedAbove.length());

            /*
             * this needs to be done after the escalator has been shrunk down,
             * or it won't work correctly (due to setScrollTop invocation)
             */
            scroller.recalculateScrollbarsForVirtualViewport();
        }

        private void paintRemoveRowsAtMiddle(final Range removedLogicalInside,
                final Range removedVisualInside, final int logicalOffset) {
            /*-
             *  :       :       :
             * |2|     |2|     |2|
             * |3| ==> |*| ==> |4|
             * |4|     |4|     |6| <- newly rendered
             *  :       :       :
             */

            final int escalatorRowCount = visualRowOrder.size();

            final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                    .getLast())
                    - (removedVisualInside.length() - 1)
                    + logicalOffset;
            moveAndUpdateEscalatorRows(removedVisualInside, escalatorRowCount,
                    logicalTargetIndex);

            // move the surrounding rows to their correct places.
            final ListIterator<TableRowElement> iterator = visualRowOrder
                    .listIterator(removedVisualInside.getStart());

            double rowTop = getRowTop(removedLogicalInside.getStart()
                    + logicalOffset);
            for (int i = removedVisualInside.getStart(); i < escalatorRowCount
                    - removedVisualInside.length(); i++) {
                final TableRowElement tr = iterator.next();
                setRowPosition(tr, 0, rowTop);
                rowTop += getDefaultRowHeight();
                rowTop += spacerContainer.getSpacerHeight(i
                        + removedLogicalInside.getStart());
            }
        }

        private void paintRemoveRowsAtBottom(final Range removedLogicalInside,
                final Range removedVisualInside) {
            /*-
             *                  :
             *  :       :      |4| <- newly rendered
             * |5|     |5|     |5|
             * |6| ==> |*| ==> |7|
             * |7|     |7|     
             */

            final int logicalTargetIndex = getLogicalRowIndex(visualRowOrder
                    .getFirst()) - removedVisualInside.length();
            moveAndUpdateEscalatorRows(removedVisualInside, 0,
                    logicalTargetIndex);

            // move the surrounding rows to their correct places.
            int firstUpdatedIndex = removedVisualInside.getEnd();
            final ListIterator<TableRowElement> iterator = visualRowOrder
                    .listIterator(firstUpdatedIndex);

            double rowTop = getRowTop(removedLogicalInside.getStart());
            int i = 0;
            while (iterator.hasNext()) {
                final TableRowElement tr = iterator.next();
                setRowPosition(tr, 0, rowTop);
                rowTop += getDefaultRowHeight();
                rowTop += spacerContainer.getSpacerHeight(firstUpdatedIndex
                        + i++);
            }
        }

        @Override
        protected int getLogicalRowIndex(final TableRowElement tr) {
            assert tr.getParentNode() == root : "The given element isn't a row element in the body";
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

            /*
             * TODO [[spacer]]: these assumptions will be totally broken with
             * spacers.
             */
            final int maxEscalatorRows = getMaxEscalatorRowCapacity();
            final int currentTopRowIndex = getLogicalRowIndex(visualRowOrder
                    .getFirst());

            final Range[] partitions = logicalRange.partitionWith(Range
                    .withLength(currentTopRowIndex, maxEscalatorRows));
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
                final int firstLogicalRowIndex = getLogicalRowIndex(visualRowOrder
                        .getFirst());
                for (int rowNumber = visualRange.getStart(); rowNumber < visualRange
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
                throw new IndexOutOfBoundsException("No such visual index: "
                        + index);
            }
        }

        @Override
        public TableRowElement getRowElement(int index) {
            if (index < 0 || index >= getRowCount()) {
                throw new IndexOutOfBoundsException("No such logical index: "
                        + index);
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
             * Unfortunately, those the code can't trivially be shared, since
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

            final int maxEscalatorRows = getMaxEscalatorRowCapacity();
            final int neededEscalatorRows = Math.min(maxEscalatorRows,
                    body.getRowCount());
            final int neededEscalatorRowsDiff = neededEscalatorRows
                    - visualRowOrder.size();

            if (neededEscalatorRowsDiff > 0) {
                // needs more

                /*
                 * This is a workaround for the issue where we might be scrolled
                 * to the bottom, and the widget expands beyond the content
                 * range
                 */

                final int index = visualRowOrder.size();
                final int nextLastLogicalIndex;
                if (!visualRowOrder.isEmpty()) {
                    nextLastLogicalIndex = getLogicalRowIndex(visualRowOrder
                            .getLast()) + 1;
                } else {
                    nextLastLogicalIndex = 0;
                }

                final boolean contentWillFit = nextLastLogicalIndex < getRowCount()
                        - neededEscalatorRowsDiff;
                if (contentWillFit) {
                    final List<TableRowElement> addedRows = fillAndPopulateEscalatorRowsIfNeeded(
                            index, neededEscalatorRowsDiff);

                    /*
                     * Since fillAndPopulateEscalatorRowsIfNeeded operates on
                     * the assumption that index == visual index == logical
                     * index, we thank for the added escalator rows, but since
                     * they're painted in the wrong CSS position, we need to
                     * move them to their actual locations.
                     * 
                     * Note: this is the second (see body.paintInsertRows)
                     * occasion where fillAndPopulateEscalatorRowsIfNeeded would
                     * behave "more correctly" if it only would add escalator
                     * rows to the DOM and appropriate bookkeping, and not
                     * actually populate them :/
                     */
                    moveAndUpdateEscalatorRows(
                            Range.withLength(index, addedRows.size()), index,
                            nextLastLogicalIndex);
                } else {
                    /*
                     * TODO [[optimize]]
                     * 
                     * We're scrolled so far down that all rows can't be simply
                     * appended at the end, since we might start displaying
                     * escalator rows that don't exist. To avoid the mess that
                     * is body.paintRemoveRows, this is a dirty hack that dumbs
                     * the problem down to a more basic and already-solved
                     * problem:
                     * 
                     * 1) scroll all the way up 2) add the missing escalator
                     * rows 3) scroll back to the original position.
                     * 
                     * Letting the browser scroll back to our original position
                     * will automatically solve any possible overflow problems,
                     * since the browser will not allow us to scroll beyond the
                     * actual content.
                     */

                    final double oldScrollTop = getScrollTop();
                    setScrollTop(0);
                    scroller.onScroll();
                    fillAndPopulateEscalatorRowsIfNeeded(index,
                            neededEscalatorRowsDiff);
                    setScrollTop(oldScrollTop);
                    scroller.onScroll();
                }
            }

            else if (neededEscalatorRowsDiff < 0) {
                // needs less

                final ListIterator<TableRowElement> iter = visualRowOrder
                        .listIterator(visualRowOrder.size());
                for (int i = 0; i < -neededEscalatorRowsDiff; i++) {
                    final Element last = iter.previous();
                    last.removeFromParent();
                    iter.remove();
                }

                /*
                 * If we were scrolled to the bottom so that we didn't have an
                 * extra escalator row at the bottom, we'll probably end up with
                 * blank space at the bottom of the escalator, and one extra row
                 * above the header.
                 * 
                 * Experimentation idea #1: calculate "scrollbottom" vs content
                 * bottom and remove one row from top, rest from bottom. This
                 * FAILED, since setHeight has already happened, thus we never
                 * will detect ourselves having been scrolled all the way to the
                 * bottom.
                 */

                if (!visualRowOrder.isEmpty()) {
                    final double firstRowTop = getRowTop(visualRowOrder
                            .getFirst());
                    final double firstRowMinTop = tBodyScrollTop
                            - getDefaultRowHeight();
                    if (firstRowTop < firstRowMinTop) {
                        final int newLogicalIndex = getLogicalRowIndex(visualRowOrder
                                .getLast()) + 1;
                        moveAndUpdateEscalatorRows(Range.withOnly(0),
                                visualRowOrder.size(), newLogicalIndex);
                    }
                }
            }

            if (neededEscalatorRowsDiff != 0) {
                fireRowVisibilityChangeEvent();
            }

            Profiler.leave("Escalator.BodyRowContainer.verifyEscalatorCount");
        }

        @Override
        protected void reapplyDefaultRowHeights() {
            if (visualRowOrder.isEmpty()) {
                return;
            }

            Profiler.enter("Escalator.BodyRowContainer.reapplyDefaultRowHeights");

            /* step 1: resize and reposition rows */
            for (int i = 0; i < visualRowOrder.size(); i++) {
                TableRowElement tr = visualRowOrder.get(i);
                reapplyRowHeight(tr, getDefaultRowHeight());

                final int logicalIndex = getTopRowLogicalIndex() + i;
                setRowPosition(tr, 0, logicalIndex * getDefaultRowHeight());
            }

            /*
             * step 2: move scrollbar so that it corresponds to its previous
             * place
             */

            /*
             * This ratio needs to be calculated with the scrollsize (not max
             * scroll position) in order to align the top row with the new
             * scroll position.
             */
            double scrollRatio = verticalScrollbar.getScrollPos()
                    / verticalScrollbar.getScrollSize();
            scroller.recalculateScrollbarsForVirtualViewport();
            verticalScrollbar.setScrollPos((int) (getDefaultRowHeight()
                    * getRowCount() * scrollRatio));
            setBodyScrollPosition(horizontalScrollbar.getScrollPos(),
                    verticalScrollbar.getScrollPos());
            scroller.onScroll();

            /* step 3: make sure we have the correct amount of escalator rows. */
            verifyEscalatorCount();

            int logicalLogical = (int) (getRowTop(visualRowOrder.getFirst()) / getDefaultRowHeight());
            setTopRowLogicalIndex(logicalLogical);

            Profiler.leave("Escalator.BodyRowContainer.reapplyDefaultRowHeights");
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
                assert focusedRow.getParentElement() == root : "Trying to sort around a row that doesn't exist in body";
                assert visualRowOrder.contains(focusedRow)
                        || body.spacerContainer.isSpacer(focusedRow) : "Trying to sort around a row that doesn't exist in visualRowOrder or is not a spacer.";
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

            List<TableRowElement> orderedBodyRows = new ArrayList<TableRowElement>(
                    visualRowOrder);
            Map<Integer, SpacerContainer.SpacerImpl> spacers = body.spacerContainer
                    .getSpacers();

            /*
             * Start at -1 to include a spacer that is rendered above the
             * viewport, but its parent row is still not shown
             */
            for (int i = -1; i < visualRowOrder.size(); i++) {
                SpacerContainer.SpacerImpl spacer = spacers.remove(Integer
                        .valueOf(getTopRowLogicalIndex() + i));

                if (spacer != null) {
                    orderedBodyRows.add(i + 1, spacer.getRootElement());
                    spacer.show();
                }
            }
            /*
             * At this point, invisible spacers aren't reordered, so their
             * position in the DOM will remain undefined.
             */

            // If a spacer was not reordered, it means that it's out of view.
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

        @Override
        public Cell getCell(Element element) {
            Cell cell = super.getCell(element);
            if (cell == null) {
                return null;
            }

            // Convert DOM coordinates to logical coordinates for rows
            TableRowElement rowElement = (TableRowElement) cell.getElement()
                    .getParentElement();
            return new Cell(getLogicalRowIndex(rowElement), cell.getColumn(),
                    cell.getElement());
        }

        @Override
        public void setSpacer(int rowIndex, double height)
                throws IllegalArgumentException {
            spacerContainer.setSpacer(rowIndex, height);
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
         * <em>Calculates</em> the correct top position of a row at a logical
         * index, regardless if there is one there or not.
         * <p>
         * A correct result requires that both {@link #getDefaultRowHeight()} is
         * consistent, and the placement and height of all spacers above the
         * given logical index are consistent.
         * 
         * @param logicalIndex
         *            the logical index of the row for which to calculate the
         *            top position
         * @return the position at which to place a row in {@code logicalIndex}
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
                int fromIndex = (logicalRow - visibleRowLogicalRange.getStart()) + 1;
                int toIndex = visibleRowLogicalRange.length();
                List<TableRowElement> sublist = visualRowOrder.subList(
                        fromIndex, toIndex);
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

        void scrollToSpacer(int spacerIndex, ScrollDestination destination,
                int padding) {
            spacerContainer.scrollToSpacer(spacerIndex, destination, padding);
        }
    }

    private class ColumnConfigurationImpl implements ColumnConfiguration {
        public class Column {
            public static final double DEFAULT_COLUMN_WIDTH_PX = 100;

            private double definedWidth = -1;
            private double calculatedWidth = DEFAULT_COLUMN_WIDTH_PX;
            private boolean measuringRequested = false;

            public void setWidth(double px) {
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

        private final List<Column> columns = new ArrayList<Column>();
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

        private void removeColumnsAdjustScrollbar(int index, int numberOfColumns) {
            if (horizontalScrollbar.getOffsetSize() >= horizontalScrollbar
                    .getScrollSize()) {
                return;
            }

            double leftPosOfFirstColumnToRemove = getCalculatedColumnsWidth(Range
                    .between(0, index));
            double widthOfColumnsToRemove = getCalculatedColumnsWidth(Range
                    .withLength(index, numberOfColumns));

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
            return getCalculatedColumnsWidth(Range.between(0, getColumnCount()));
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

            // this needs to be before the scrollbar adjustment.
            boolean scrollbarWasNeeded = horizontalScrollbar.getOffsetSize() < horizontalScrollbar
                    .getScrollSize();
            scroller.recalculateScrollbarsForVirtualViewport();
            boolean scrollbarIsNowNeeded = horizontalScrollbar.getOffsetSize() < horizontalScrollbar
                    .getScrollSize();
            if (!scrollbarWasNeeded && scrollbarIsNowNeeded) {
                body.verifyEscalatorCount();
            }

            // Add to DOM
            header.paintInsertColumns(index, numberOfColumns, frozen);
            body.paintInsertColumns(index, numberOfColumns, frozen);
            footer.paintInsertColumns(index, numberOfColumns, frozen);

            // fix initial width
            if (header.getRowCount() > 0 || body.getRowCount() > 0
                    || footer.getRowCount() > 0) {

                Map<Integer, Double> colWidths = new HashMap<Integer, Double>();
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
                        .getCalculatedColumnsWidth(Range.withLength(index,
                                numberOfColumns));
                horizontalScrollbar.setScrollPos(scroller.lastScrollLeft
                        + insertedColumnsWidth);
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
        }

        private void checkValidColumnIndex(int index)
                throws IllegalArgumentException {
            if (!Range.withLength(0, getColumnCount()).contains(index)) {
                throw new IllegalArgumentException("The given column index ("
                        + index + ") does not exist");
            }
        }

        @Override
        public double getColumnWidth(int index) throws IllegalArgumentException {
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
            assert minWidth >= 0 : "Got a negative max width for a column, which should be impossible.";
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
            assert columns.isSubsetOf(Range.between(0, getColumnCount())) : "Range "
                    + "was outside of current column range (i.e.: "
                    + Range.between(0, getColumnCount())
                    + ", but was given :"
                    + columns;

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
                assert RootPanel.get().getElement().isOrHasChild(root) : "Root element should've been attached to the DOM by now.";
                domHasBeenSetup = true;

                getRootElement().getStyle().setWidth(getInnerWidth(), Unit.PX);
                setHeight(height);

                spacerElement.setColSpan(getColumnConfiguration()
                        .getColumnCount());

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
                positions
                        .set(getDecoElement(), 0, y - getSpacerDecoTopOffset());
            }

            private double getSpacerDecoTopOffset() {
                return getBody().getDefaultRowHeight();
            }

            public void setStylePrimaryName(String style) {
                UIObject.setStylePrimaryName(root, style + "-spacer");
                UIObject.setStylePrimaryName(deco, style + "-spacer-deco");
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
                            .getBorderBottomThickness(body.getRowElement(
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
                    verticalScrollbar.setScrollSize(verticalScrollbar
                            .getScrollSize() + heightDiff);
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
                        body.setRowPosition(row, 0, body.getRowTop(row)
                                + heightDiff);
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
                    body.setBodyScrollPosition(tBodyScrollLeft, tBodyScrollTop
                            + moveDiff);
                    verticalScrollbar.setScrollPosByDelta(moveDiff);

                } else {
                    body.shiftRowPositions(getRow(), heightDiff);
                }

                if (!spacerIsGrowing) {
                    verticalScrollbar.setScrollSize(verticalScrollbar
                            .getScrollSize() + heightDiff);
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
             * Sets a new row index for this spacer. Also updates the bookeeping
             * at {@link SpacerContainer#rowIndexToSpacer}.
             */
            @SuppressWarnings("boxing")
            public void setRowIndex(int rowIndex) {
                SpacerImpl spacer = rowIndexToSpacer.remove(this.rowIndex);
                assert this == spacer : "trying to move an unexpected spacer.";
                this.rowIndex = rowIndex;
                root.setPropertyInt(SPACER_LOGICAL_ROW_PROPERTY, rowIndex);
                rowIndexToSpacer.put(this.rowIndex, this);
            }

            /**
             * Updates the spacer's visibility parameters, based on whether it
             * is being currently visible or not.
             */
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
            }

            public void hide() {
                getRootElement().getStyle().setDisplay(Display.NONE);
                getDecoElement().getStyle().setDisplay(Display.NONE);
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

        private final TreeMap<Integer, SpacerImpl> rowIndexToSpacer = new TreeMap<Integer, SpacerImpl>();

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

            if (!rowIndexToSpacer.containsKey(spacerIndex)) {
                throw new IllegalArgumentException("No spacer open at index "
                        + spacerIndex);
            }

            SpacerImpl spacer = rowIndexToSpacer.get(spacerIndex);
            double targetStartPx = spacer.getTop();
            double targetEndPx = targetStartPx + spacer.getHeight();

            Range viewportPixels = getViewportPixels();
            double viewportStartPx = viewportPixels.getStart();
            double viewportEndPx = viewportPixels.getEnd();

            double scrollTop = getScrollPos(destination, targetStartPx,
                    targetEndPx, viewportStartPx, viewportEndPx, padding);

            setScrollTop(scrollTop);
        }

        public void reapplySpacerWidths() {
            // FIXME #16266 , spacers get couple pixels too much because borders
            final double width = getInnerWidth() - spacerDecoWidth;
            for (SpacerImpl spacer : rowIndexToSpacer.values()) {
                spacer.getRootElement().getStyle().setWidth(width, Unit.PX);
            }
        }

        public void paintRemoveSpacers(Range removedRowsRange) {
            removeSpacers(removedRowsRange);
            shiftSpacersByRows(removedRowsRange.getStart(),
                    -removedRowsRange.length());
        }

        @SuppressWarnings("boxing")
        public void removeSpacers(Range removedRange) {

            Map<Integer, SpacerImpl> removedSpacers = rowIndexToSpacer
                    .subMap(removedRange.getStart(), true,
                            removedRange.getEnd(), false);

            if (removedSpacers.isEmpty()) {
                return;
            }

            for (SpacerImpl spacer : removedSpacers.values()) {
                /*
                 * [[optimization]] TODO: Each invocation of the setHeight
                 * method has a cascading effect in the DOM. if this proves to
                 * be slow, the DOM offset could be updated as a batch.
                 */

                destroySpacerContent(spacer);
                spacer.setHeight(0); // resets row offsets
                spacer.getRootElement().removeFromParent();
                spacer.getDecoElement().removeFromParent();
            }

            removedSpacers.clear();

            if (rowIndexToSpacer.isEmpty()) {
                assert spacerScrollerRegistration != null : "Spacer scroller registration was null";
                spacerScrollerRegistration.removeHandler();
                spacerScrollerRegistration = null;
            }
        }

        public Map<Integer, SpacerImpl> getSpacers() {
            return new HashMap<Integer, SpacerImpl>(rowIndexToSpacer);
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
            return new ArrayList<SpacerImpl>(rowIndexToSpacer.tailMap(
                    logicalRowIndex, true).values());
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

            ArrayList<SpacerImpl> spacers = new ArrayList<SpacerImpl>(
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

                assert topIsAboveRange ^ topIsBelowRange ^ topIsInRange : "Bad top logic";
                assert bottomIsAboveRange ^ bottomIsBelowRange
                        ^ bottomIsInRange : "Bad bottom logic";

                if (bottomIsAboveRange) {
                    continue;
                } else if (topIsBelowRange) {
                    return heights;
                }

                else if (topIsAboveRange && bottomIsInRange) {
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
                }

                else if (topIsAboveRange && bottomIsBelowRange) {

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
                }

                else if (topIsInRange && bottomIsBelowRange) {
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
                }

                else {
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
         * index.
         * 
         * @param logicalIndex
         *            a logical row index
         * @return the pixels occupied by spacers up until {@code logicalIndex}
         */
        @SuppressWarnings("boxing")
        public double getSpacerHeightsSumUntilIndex(int logicalIndex) {
            return getHeights(rowIndexToSpacer.headMap(logicalIndex, false)
                    .values());
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
            spacerRoot.getStyle().setWidth(
                    columnConfiguration.calculateRowWidth(), Unit.PX);
            body.getElement().appendChild(spacerRoot);
            spacer.setupDom(height);
            // set the deco position, requires that spacer is in the DOM
            positions.set(spacer.getDecoElement(), 0,
                    spacer.getTop() - spacer.getSpacerDecoTopOffset());

            spacerDecoContainer.appendChild(spacer.getDecoElement());
            if (spacerDecoContainer.getParentElement() == null) {
                getElement().appendChild(spacerDecoContainer);
                // calculate the spacer deco width, it won't change
                spacerDecoWidth = WidgetUtil
                        .getRequiredWidthBoundingClientRectDouble(spacer
                                .getDecoElement());
            }

            initSpacerContent(spacer);

            body.sortDomElements();
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
            assert getElement().isOrHasChild(spacer.getRootElement()) : "Spacer's root element somehow got detached from Escalator before detaching";
            assert getElement().isOrHasChild(spacer.getElement()) : "Spacer element somehow got detached from Escalator before detaching";
            spacerUpdater.destroy(spacer);
            assert getElement().isOrHasChild(spacer.getRootElement()) : "Spacer's root element somehow got detached from Escalator before detaching";
            assert getElement().isOrHasChild(spacer.getElement()) : "Spacer element somehow got detached from Escalator before detaching";
        }

        private void initSpacerContent(Iterable<SpacerImpl> spacers) {
            for (SpacerImpl spacer : spacers) {
                initSpacerContent(spacer);
            }
        }

        private void initSpacerContent(SpacerImpl spacer) {
            assert getElement().isOrHasChild(spacer.getRootElement()) : "Spacer's root element somehow got detached from Escalator before attaching";
            assert getElement().isOrHasChild(spacer.getElement()) : "Spacer element somehow got detached from Escalator before attaching";
            spacerUpdater.init(spacer);
            assert getElement().isOrHasChild(spacer.getRootElement()) : "Spacer's root element somehow got detached from Escalator during attaching";
            assert getElement().isOrHasChild(spacer.getElement()) : "Spacer element somehow got detached from Escalator during attaching";

            spacer.updateVisibility();
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
            for (SpacerImpl spacer : rowIndexToSpacer.tailMap(changedRowIndex,
                    false).values()) {
                spacer.setPositionDiff(0, diffPx);
            }
        }

        /**
         * Shifts spacers at and after a specific row by an amount of rows.
         * <p>
         * This moves both their associated row index and also their visual
         * placement.
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
            for (SpacerContainer.SpacerImpl spacer : getSpacersForRowAndAfter(index)) {
                spacer.setPositionDiff(0, pxDiff);
                spacer.setRowIndex(spacer.getRow() + numberOfRows);
            }
        }

        private void updateSpacerDecosVisibility() {
            final Range visibleRowRange = getVisibleRowRange();
            Collection<SpacerImpl> visibleSpacers = rowIndexToSpacer.subMap(
                    visibleRowRange.getStart() - 1,
                    visibleRowRange.getEnd() + 1).values();
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
        private final Map<Element, Double> elementTopPositionMap = new HashMap<Element, Double>();
        private final Map<Element, Double> elementLeftPositionMap = new HashMap<Element, Double>();

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
                indices[i] = Integer.parseInt(tmp.substring(0,
                        tmp.indexOf("]", 1)));
            }
            return new SubPartArguments(type, indices);
        }
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
    private final TableSectionElement headElem = TableSectionElement.as(DOM
            .createTHead());
    /** The {@code <tbody/>} tag. */
    private final TableSectionElement bodyElem = TableSectionElement.as(DOM
            .createTBody());
    /** The {@code <tfoot/>} tag. */
    private final TableSectionElement footElem = TableSectionElement.as(DOM
            .createTFoot());

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

    private final HeaderRowContainer header = new HeaderRowContainer(headElem);
    private final BodyRowContainerImpl body = new BodyRowContainerImpl(bodyElem);
    private final FooterRowContainer footer = new FooterRowContainer(footElem);

    private final Scroller scroller = new Scroller();

    private final ColumnConfigurationImpl columnConfiguration = new ColumnConfigurationImpl();
    private final DivElement tableWrapper;

    private final DivElement horizontalScrollbarDeco = DivElement.as(DOM
            .createDiv());
    private final DivElement headerDeco = DivElement.as(DOM.createDiv());
    private final DivElement footerDeco = DivElement.as(DOM.createDiv());
    private final DivElement spacerDecoContainer = DivElement.as(DOM
            .createDiv());

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

    private boolean layoutIsScheduled = false;
    private ScheduledCommand layoutCommand = new ScheduledCommand() {
        @Override
        public void execute() {
            recalculateElementSizes();
            layoutIsScheduled = false;
        }
    };

    private final ElementPositionBookkeeper positions = new ElementPositionBookkeeper();

    /**
     * Creates a new Escalator widget instance.
     */
    public Escalator() {

        detectAndApplyPositionFunction();
        getLogger().info(
                "Using " + position.getClass().getSimpleName()
                        + " for position");

        final Element root = DOM.createDiv();
        setElement(root);

        setupScrollbars(root);

        tableWrapper = DivElement.as(DOM.createDiv());

        root.appendChild(tableWrapper);

        final Element table = DOM.createTable();
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
    }

    private void setupScrollbars(final Element root) {

        ScrollHandler scrollHandler = new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                scroller.onScroll();
                fireEvent(new ScrollEvent());
            }
        };

        int scrollbarThickness = WidgetUtil.getNativeScrollbarSize();
        if (BrowserInfo.get().isIE()) {
            /*
             * IE refuses to scroll properly if the DIV isn't at least one pixel
             * larger than the scrollbar controls themselves. But, probably
             * because of subpixel rendering, in Grid, one pixel isn't enough,
             * so we'll add two instead.
             */
            if (BrowserInfo.get().isIE9()) {
                scrollbarThickness += 2;
            } else {
                scrollbarThickness += 1;
            }
        }

        root.appendChild(verticalScrollbar.getElement());
        verticalScrollbar.addScrollHandler(scrollHandler);
        verticalScrollbar.setScrollbarThickness(scrollbarThickness);

        if (BrowserInfo.get().isIE8()) {
            /*
             * IE8 will have to compensate for a misalignment where it pops the
             * scrollbar outside of its box. See Bug 3 in
             * http://edskes.net/ie/ie8overflowandexpandingboxbugs.htm
             */
            Style vScrollStyle = verticalScrollbar.getElement().getStyle();
            vScrollStyle.setRight(
                    verticalScrollbar.getScrollbarThickness() - 1, Unit.PX);
        }

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
                        Scheduler.get().scheduleFinally(new ScheduledCommand() {

                            @Override
                            public void execute() {
                                applyHeightByRows();
                                queued = false;
                            }
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

        header.autodetectRowHeightLater();
        body.autodetectRowHeightLater();
        footer.autodetectRowHeightLater();

        header.paintInsertRows(0, header.getRowCount());
        footer.paintInsertRows(0, footer.getRowCount());

        // recalculateElementSizes();

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                /*
                 * Not a faintest idea why we have to defer this call, but
                 * unless it is deferred, the size of the escalator will be 0x0
                 * after it is first detached and then reattached to the DOM.
                 * This only applies to a bare Escalator; inside a Grid
                 * everything works fine either way.
                 * 
                 * The three autodetectRowHeightLater calls above seem obvious
                 * suspects at first. However, they don't seem to have anything
                 * to do with the issue, as they are no-ops in the
                 * detach-reattach case.
                 */
                recalculateElementSizes();
            }
        });

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
        scroller.attachTouchListeners(getElement());
    }

    @Override
    protected void onUnload() {

        scroller.detachScrollListener(verticalScrollbar.getElement());
        scroller.detachScrollListener(horizontalScrollbar.getElement());
        scroller.detachMousewheelListener(getElement());
        scroller.detachTouchListeners(getElement());

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
        /*
         * firefox has a bug in its translate operation, showing white space
         * when adjusting the scrollbar in BodyRowContainer.paintInsertRows
         */
        if (Window.Navigator.getUserAgent().contains("Firefox")) {
            position = new AbsolutePosition();
            return;
        }

        final Style docStyle = Document.get().getBody().getStyle();
        if (hasProperty(docStyle, "transform")) {
            if (hasProperty(docStyle, "transformStyle")) {
                position = new Translate3DPosition();
            } else {
                position = new TranslatePosition();
            }
        } else if (hasProperty(docStyle, "webkitTransform")) {
            position = new WebkitTranslate3DPosition();
        } else {
            position = new AbsolutePosition();
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
     * @return <code>true</code> iff header, body or footer has rows && there
     *         are columns
     */
    private boolean hasColumnAndRowData() {
        return (header.getRowCount() > 0 || body.getRowCount() > 0 || footer
                .getRowCount() > 0) && columnConfiguration.getColumnCount() > 0;
    }

    /**
     * Check whether there are any cells in the DOM.
     * 
     * @return <code>true</code> iff header, body or footer has any child
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
        if (width != null && !width.isEmpty()) {
            super.setWidth(width);
        } else {
            super.setWidth(DEFAULT_WIDTH);
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
            heightByCss = DEFAULT_HEIGHT;
        }

        if (getHeightMode() == HeightMode.CSS) {
            setHeightInternal(height);
        }
    }

    private void setHeightInternal(final String height) {
        final int escalatorRowsBefore = body.visualRowOrder.size();

        if (height != null && !height.isEmpty()) {
            super.setHeight(height);
        } else {
            super.setHeight(DEFAULT_HEIGHT);
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
            throw new IllegalArgumentException("The given column index "
                    + columnIndex + " is frozen.");
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
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                validateScrollDestination(destination, padding);
                verifyValidRowIndex(rowIndex);
                scroller.scrollToRow(rowIndex, destination, padding);
            }
        });
    }

    private void verifyValidRowIndex(final int rowIndex) {
        if (rowIndex < 0 || rowIndex >= body.getRowCount()) {
            throw new IndexOutOfBoundsException("The given row index "
                    + rowIndex + " does not exist.");
        }
    }

    /**
     * Scrolls the body vertically so that the spacer at the given row index is
     * visible and there is at least {@literal padding} pixesl to the given
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
        validateScrollDestination(destination, padding);
        body.scrollToSpacer(spacerIndex, destination, padding);
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
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                validateScrollDestination(destination, padding);
                if (rowIndex != -1) {
                    verifyValidRowIndex(rowIndex);
                }

                // row range
                final Range rowRange;
                if (rowIndex != -1) {
                    int rowTop = (int) Math.floor(body.getRowTop(rowIndex));
                    int rowHeight = (int) Math.ceil(body.getDefaultRowHeight());
                    rowRange = Range.withLength(rowTop, rowHeight);
                } else {
                    rowRange = Range.withLength(0, 0);
                }

                // get spacer
                final SpacerContainer.SpacerImpl spacer = body.spacerContainer
                        .getSpacer(rowIndex);

                if (rowIndex == -1 && spacer == null) {
                    throw new IllegalArgumentException(
                            "Cannot scroll to row index "
                                    + "-1, as there is no spacer open at that index.");
                }

                // make into target range
                final Range targetRange;
                if (spacer != null) {
                    final int spacerTop = (int) Math.floor(spacer.getTop());
                    final int spacerHeight = (int) Math.ceil(spacer.getHeight());
                    Range spacerRange = Range.withLength(spacerTop,
                            spacerHeight);

                    targetRange = rowRange.combineWith(spacerRange);
                } else {
                    targetRange = rowRange;
                }

                // get params
                int targetStart = targetRange.getStart();
                int targetEnd = targetRange.getEnd();
                double viewportStart = getScrollTop();
                double viewportEnd = viewportStart + body.getHeightOfSection();

                double scrollPos = getScrollPos(destination, targetStart,
                        targetEnd, viewportStart, viewportEnd, padding);

                setScrollTop(scrollPos);
            }
        });
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
        widthOfEscalator = Math.max(0, WidgetUtil
                .getRequiredWidthBoundingClientRectDouble(getElement()));
        heightOfEscalator = Math.max(0, WidgetUtil
                .getRequiredHeightBoundingClientRectDouble(getElement()));

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
    private static double[] snapDeltas(final double deltaX,
            final double deltaY, final double thresholdRatio) {

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
            int visibleRangeStart = body.getLogicalRowIndex(body.visualRowOrder
                    .getFirst());
            int visibleRangeEnd = body.getLogicalRowIndex(body.visualRowOrder
                    .getLast()) + 1;

            int visibleRowCount = visibleRangeEnd - visibleRangeStart;
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
            Widget w = WidgetUtil.findWidget(castElement, null);

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
        UIObject.setStylePrimaryName(horizontalScrollbarDeco, style
                + "-horizontal-scrollbar-deco");
        UIObject.setStylePrimaryName(spacerDecoContainer, style
                + "-spacer-deco-container");

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
     *             if {@code rows} is &leq; 0,
     *             {@link Double#isInifinite(double) infinite} or
     *             {@link Double#isNaN(double) NaN}.
     * @see #setHeightMode(HeightMode)
     */
    public void setHeightByRows(double rows) throws IllegalArgumentException {
        if (rows <= 0) {
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
        if (heightMode != HeightMode.ROW) {
            return;
        }

        double headerHeight = header.getHeightOfSection();
        double footerHeight = footer.getHeightOfSection();
        double bodyHeight = body.getDefaultRowHeight() * heightByRows;
        double scrollbar = horizontalScrollbar.showsScrollHandle() ? horizontalScrollbar
                .getScrollbarThickness() : 0;

        double totalHeight = headerHeight + bodyHeight + scrollbar
                + footerHeight;
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
            throw new UnsupportedOperationException("Unexpected value: "
                    + direction);
        }
    }

    /**
     * Checks whether or not an direction is locked for scrolling.
     * 
     * @param direction
     *            the direction of the scroll of which to check the lock status
     * @return <code>true</code> iff the direction is locked
     */
    public boolean isScrollLocked(ScrollbarBundle.Direction direction) {
        switch (direction) {
        case HORIZONTAL:
            return horizontalScrollbar.isLocked();
        case VERTICAL:
            return verticalScrollbar.isLocked();
        default:
            throw new UnsupportedOperationException("Unexpected value: "
                    + direction);
        }
    }

    /**
     * Adds a scroll handler to this escalator
     * 
     * @param handler
     *            the scroll handler to add
     * @return a handler registration for the registered scroll handler
     */
    public HandlerRegistration addScrollHandler(ScrollHandler handler) {
        return addHandler(handler, ScrollEvent.TYPE);
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
        return body.getMaxEscalatorRowCapacity();
    }

    /**
     * Gets the escalator's inner width. This is the entire width in pixels,
     * without the vertical scrollbar.
     * 
     * @return escalator's inner width
     */
    public double getInnerWidth() {
        return WidgetUtil
                .getRequiredWidthBoundingClientRectDouble(tableWrapper);
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
    public com.google.gwt.user.client.Element getSubPartElement(String subPart) {
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

    private Element getSubPartElementTableStructure(SubPartArguments args) {

        String type = args.getType();
        int[] indices = args.getIndices();

        // Get correct RowContainer for type from Escalator
        RowContainer container = null;
        if (type.equalsIgnoreCase("header")) {
            container = getHeader();
        } else if (type.equalsIgnoreCase("cell")) {
            // If wanted row is not visible, we need to scroll there.
            Range visibleRowRange = getVisibleRowRange();
            if (indices.length > 0 && !visibleRowRange.contains(indices[0])) {
                try {
                    scrollToRow(indices[0], ScrollDestination.ANY, 0);
                } catch (IllegalArgumentException e) {
                    getLogger().log(Level.SEVERE, e.getMessage());
                }
                // Scrolling causes a lazy loading event. No element can
                // currently be retrieved.
                return null;
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

    private static Element getCellFromRow(TableRowElement rowElement, int index) {
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
            return body.spacerContainer.getSubPartElement(args.getIndex(0));
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getSubPartName(com.google.gwt.user.client.Element subElement) {

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
            boolean containerRow = (subElement.getTagName().equalsIgnoreCase(
                    "tr") && subElement.getParentElement() == container
                    .getElement());
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
}
