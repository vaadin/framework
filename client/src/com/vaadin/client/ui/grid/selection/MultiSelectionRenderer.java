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
package com.vaadin.client.ui.grid.selection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.Util;
import com.vaadin.client.data.AbstractRemoteDataSource;
import com.vaadin.client.data.DataSource;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.DataAvailableEvent;
import com.vaadin.client.ui.grid.DataAvailableHandler;
import com.vaadin.client.ui.grid.FlyweightCell;
import com.vaadin.client.ui.grid.Grid;
import com.vaadin.client.ui.grid.events.BodyKeyDownHandler;
import com.vaadin.client.ui.grid.events.BodyKeyUpHandler;
import com.vaadin.client.ui.grid.events.GridKeyDownEvent;
import com.vaadin.client.ui.grid.events.GridKeyUpEvent;
import com.vaadin.client.ui.grid.renderers.ComplexRenderer;
import com.vaadin.client.ui.grid.selection.SelectionModel.Multi.Batched;
import com.vaadin.shared.ui.grid.ScrollDestination;

/* This class will probably not survive the final merge of all selection functionality. */
public class MultiSelectionRenderer<T> extends ComplexRenderer<Boolean> {

    /** The size of the autoscroll area, both top and bottom. */
    private static final int SCROLL_AREA_GRADIENT_PX = 100;

    /** The maximum number of pixels per second to autoscroll. */
    private static final int SCROLL_TOP_SPEED_PX_SEC = 500;

    /**
     * The minimum area where the grid doesn't scroll while the pointer is
     * pressed.
     */
    private static final int MIN_NO_AUTOSCROLL_AREA_PX = 50;

    /**
     * This class's main objective is to listen when to stop autoscrolling, and
     * make sure everything stops accordingly.
     */
    private class TouchEventHandler implements NativePreviewHandler {
        @Override
        public void onPreviewNativeEvent(final NativePreviewEvent event) {
            switch (event.getTypeInt()) {
            case Event.ONTOUCHSTART: {
                if (event.getNativeEvent().getTouches().length() == 1) {
                    /*
                     * Something has dropped a touchend/touchcancel and the
                     * scroller is most probably running amok. Let's cancel it
                     * and pretend that everything's going as expected
                     * 
                     * Because this is a preview, this code is run before the
                     * event handler in MultiSelectionRenderer.onBrowserEvent.
                     * Therefore, we can simply kill everything and let that
                     * method restart things as they should.
                     */
                    autoScrollHandler.stop();

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
                /*
                 * Remember: targetElement is always where touchstart started,
                 * not where the finger is pointing currently.
                 */
                final Element targetElement = Element.as(event.getNativeEvent()
                        .getEventTarget());
                if (isInFirstColumn(targetElement)) {
                    removeNativeHandler();
                    event.cancel();
                }
                break;
            }
        }

        private boolean isInFirstColumn(final Element element) {
            if (element == null) {
                return false;
            }
            final Element tbody = getTbodyElement();

            if (tbody == null || !tbody.isOrHasChild(element)) {
                return false;
            }

            /*
             * The null-parent in the while clause is in the case where element
             * is an immediate tr child in the tbody. Should never happen in
             * internal code, but hey...
             */
            Element cursor = element;
            while (cursor.getParentElement() != null
                    && cursor.getParentElement().getParentElement() != tbody) {
                cursor = cursor.getParentElement();
            }

            final Element tr = cursor.getParentElement();
            return tr.getFirstChildElement().equals(cursor);
        }
    }

    /**
     * This class's responsibility is to
     * <ul>
     * <li>scroll the table while a pointer is kept in a scrolling zone and
     * <li>select rows whenever a pointer is "activated" on a selection cell
     * </ul>
     * <p>
     * <em>Techical note:</em> This class is an AnimationCallback because we
     * need a timer: when the finger is kept in place while the grid scrolls, we
     * still need to be able to make new selections. So, instead of relying on
     * events (which won't be fired, since the pointer isn't necessarily
     * moving), we do this check on each frame while the pointer is "active"
     * (mouse is pressed, finger is on screen).
     */
    private class AutoScrollerAndSelector implements AnimationCallback {

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
         * The lowest y-coordinate on the {@link Event#getClientY() client} from
         * where we need to start scrolling towards the top.
         */
        private int topBound = -1;

        /**
         * The highest y-coordinate on the {@link Event#getClientY() client}
         * from where we need to scrolling towards the bottom.
         */
        private int bottomBound = -1;

        /**
         * <code>true</code> if the pointer is selecting, <code>false</code> if
         * the pointer is deselecting.
         */
        private final boolean selectionPaint;

        /**
         * The area where the selection acceleration takes place. If &lt;
         * {@link #GRADIENT_MIN_THRESHOLD_PX}, autoscrolling is disabled
         */
        private final int gradientArea;

        /**
         * The number of pixels per seconds we currently are scrolling (negative
         * is towards the top, positive is towards the bottom).
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

        /** The pointer's pageX coordinate. */
        private int pageX;

        /** The pointer's pageY coordinate. */
        private int pageY;

        /** The logical index of the row that was most recently modified. */
        private int logicalRow = -1;

        /** @see #doScrollAreaChecks(int) */
        private int finalTopBound;

        /** @see #doScrollAreaChecks(int) */
        private int finalBottomBound;

        private boolean scrollAreaShouldRebound = false;

        private AbstractRemoteDataSource<T> remoteDataSource = null;
        private Batched<T> batchedSelectionModel = null;

        public AutoScrollerAndSelector(final int topBound,
                final int bottomBound, final int gradientArea,
                final boolean selectionPaint) {
            finalTopBound = topBound;
            finalBottomBound = bottomBound;
            this.gradientArea = gradientArea;
            this.selectionPaint = selectionPaint;
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
                grid.setScrollTop(grid.getScrollTop() + intPixelsToScroll);
            }

            @SuppressWarnings("hiding")
            int logicalRow = getLogicalRowIndex(Util.getElementFromPoint(pageX,
                    pageY));
            if (logicalRow != -1 && logicalRow != this.logicalRow) {
                this.logicalRow = logicalRow;
                setSelected(logicalRow, selectionPaint);

                if (remoteDataSource != null && batchedSelectionModel != null) {
                    Collection<T> pinneds = batchedSelectionModel
                            .getSelectedRowsBatch();
                    pinneds.addAll(batchedSelectionModel
                            .getDeselectedRowsBatch());
                    remoteDataSource.transactionPin(pinneds);
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
            if (topBound < finalTopBound) {
                topBound += reboundPx;
                topBound = Math.min(topBound, finalTopBound);
                updateScrollSpeed(pageY);
            } else if (bottomBound > finalBottomBound) {
                bottomBound -= reboundPx;
                bottomBound = Math.max(bottomBound, finalBottomBound);
                updateScrollSpeed(pageY);
            }
        }

        private void updateScrollSpeed(final int pointerPageY) {

            final double ratio;
            if (pointerPageY < topBound) {
                final double distance = pointerPageY - topBound;
                ratio = Math.max(-1, distance / gradientArea);
            }

            else if (pointerPageY > bottomBound) {
                final double distance = pointerPageY - bottomBound;
                ratio = Math.min(1, distance / gradientArea);
            }

            else {
                ratio = 0;
            }

            scrollSpeed = ratio * SCROLL_TOP_SPEED_PX_SEC;
        }

        @SuppressWarnings("deprecation")
        public void start(int logicalRowIndex) {
            running = true;
            setSelected(logicalRowIndex, selectionPaint);
            logicalRow = logicalRowIndex;
            reschedule();

            DataSource<T> dataSource = grid.getDataSource();
            SelectionModel<T> selectionModel = grid.getSelectionModel();
            if (dataSource instanceof AbstractRemoteDataSource
                    && selectionModel instanceof Batched) {
                this.remoteDataSource = (AbstractRemoteDataSource<T>) dataSource;
                this.batchedSelectionModel = (Batched<T>) selectionModel;

                Collection<T> pinneds = batchedSelectionModel
                        .getSelectedRowsBatch();
                pinneds.addAll(batchedSelectionModel.getDeselectedRowsBatch());
                remoteDataSource.transactionPin(pinneds);
            }
        }

        @SuppressWarnings("deprecation")
        public void stop() {
            running = false;

            if (remoteDataSource != null) {
                // split into two lines because of Java generics not playing
                // nice.
                @SuppressWarnings("unchecked")
                Collection<T> emptySet = (Collection<T>) Collections.emptySet();
                remoteDataSource.transactionPin(emptySet);
                remoteDataSource = null;
                batchedSelectionModel = null;
            }

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

        @SuppressWarnings("hiding")
        public void updatePointerCoords(int pageX, int pageY) {
            doScrollAreaChecks(pageY);
            updateScrollSpeed(pageY);
            this.pageX = pageX;
            this.pageY = pageY;
        }

        /**
         * This method checks whether the first pointer event started in an area
         * that would start scrolling immediately, and does some actions
         * accordingly.
         * <p>
         * If it is, that scroll area will be offset "beyond" the pointer (above
         * if pointer is towards the top, otherwise below).
         * <p>
         * <span style="font-size:smaller">*) This behavior will change in
         * future patches (henrik paul 2.7.2014)</span>
         */
        private void doScrollAreaChecks(int pageY) {
            /*
             * The first run makes sure that neither scroll position is
             * underneath the finger, but offset to either direction from
             * underneath the pointer.
             */
            if (topBound == -1) {
                topBound = Math.min(finalTopBound, pageY);
                bottomBound = Math.max(finalBottomBound, pageY);
            }

            /*
             * Subsequent runs make sure that the scroll area grows (but doesn't
             * shrink) with the finger, but no further than the final bound.
             */
            else {
                int oldTopBound = topBound;
                if (topBound < finalTopBound) {
                    topBound = Math.max(topBound,
                            Math.min(finalTopBound, pageY));
                }

                int oldBottomBound = bottomBound;
                if (bottomBound > finalBottomBound) {
                    bottomBound = Math.min(bottomBound,
                            Math.max(finalBottomBound, pageY));
                }

                final boolean topDidNotMove = oldTopBound == topBound;
                final boolean bottomDidNotMove = oldBottomBound == bottomBound;
                final boolean wasVerticalMovement = pageY != this.pageY;
                scrollAreaShouldRebound = (topDidNotMove && bottomDidNotMove && wasVerticalMovement);
            }
        }
    }

    /**
     * This class makes sure that pointer movemenets are registered and
     * delegated to the autoscroller so that it can:
     * <ul>
     * <li>modify the speed in which we autoscroll.
     * <li>"paint" a new row with the selection.
     * </ul>
     * Essentially, when a pointer is pressed on the selection column, a native
     * preview handler is registered (so that selection gestures can happen
     * outside of the selection column). The handler itself makes sure that it's
     * detached when the pointer is "lifted".
     */
    private class AutoScrollHandler {
        private AutoScrollerAndSelector autoScroller;

        /** The registration info for {@link #scrollPreviewHandler} */
        private HandlerRegistration handlerRegistration;

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
                    pageY = Util.getTouchOrMouseClientY(nativeEvent);
                    pageX = Util.getTouchOrMouseClientX(nativeEvent);
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

        /**
         * The top bound, as calculated from the {@link Event#getClientY()
         * client} coordinates.
         */
        private int topBound = -1;

        /**
         * The bottom bound, as calculated from the {@link Event#getClientY()
         * client} coordinates.
         */
        private int bottomBound = -1;

        /** The size of the autoscroll acceleration area. */
        private int gradientArea;

        public void start(int logicalRowIndex) {

            SelectionModel<T> model = grid.getSelectionModel();
            if (model instanceof Batched) {
                Batched<?> batchedModel = (Batched<?>) model;
                batchedModel.startBatchSelect();
            }

            /*
             * bounds are updated whenever the autoscroll cycle starts, to make
             * sure that the widget hasn't changed in size, moved around, or
             * whatnot.
             */
            updateScrollBounds();

            assert handlerRegistration == null : "handlerRegistration was not null";
            assert autoScroller == null : "autoScroller was not null";
            handlerRegistration = Event
                    .addNativePreviewHandler(scrollPreviewHandler);

            autoScroller = new AutoScrollerAndSelector(topBound, bottomBound,
                    gradientArea, !isSelected(logicalRowIndex));
            autoScroller.start(logicalRowIndex);
        }

        private void updateScrollBounds() {
            final Element root = Element.as(grid.getElement());
            final Element tableWrapper = Element.as(root.getChild(2));
            final TableElement table = TableElement.as(tableWrapper
                    .getFirstChildElement());
            final Element thead = table.getTHead();
            final Element tfoot = table.getTFoot();

            /*
             * GWT _does_ have an "Element.getAbsoluteTop()" that takes both the
             * client top and scroll compensation into account, but they're
             * calculated wrong for our purposes, so this does something
             * similar, but only suitable for us.
             * 
             * Also, this should be a bit faster, since the scroll compensation
             * is calculated only once and used in two places.
             */

            final int topBorder = getClientTop(root) + thead.getOffsetHeight();
            final int bottomBorder = getClientTop(tfoot);

            final int scrollCompensation = getScrollCompensation();
            topBound = scrollCompensation + topBorder + SCROLL_AREA_GRADIENT_PX;
            bottomBound = scrollCompensation + bottomBorder
                    - SCROLL_AREA_GRADIENT_PX;
            gradientArea = SCROLL_AREA_GRADIENT_PX;

            // modify bounds if they're too tightly packed
            if (bottomBound - topBound < MIN_NO_AUTOSCROLL_AREA_PX) {
                int adjustment = MIN_NO_AUTOSCROLL_AREA_PX
                        - (bottomBound - topBound);
                topBound -= adjustment / 2;
                bottomBound += adjustment / 2;
                gradientArea -= adjustment / 2;
            }
        }

        /** Get the "top" of an element in relation to "client" coordinates. */
        private int getClientTop(final Element e) {
            Element cursor = e;
            int top = 0;
            while (cursor != null) {
                top += cursor.getOffsetTop();
                cursor = cursor.getOffsetParent();
            }
            return top;
        }

        private int getScrollCompensation() {
            Element cursor = grid.getElement();
            int scroll = 0;
            while (cursor != null) {
                scroll -= cursor.getScrollTop();
                cursor = cursor.getParentElement();
            }

            return scroll;
        }

        public void stop() {
            if (handlerRegistration != null) {
                handlerRegistration.removeHandler();
                handlerRegistration = null;
            }

            if (autoScroller != null) {
                autoScroller.stop();
                autoScroller = null;
            }

            SelectionModel<T> model = grid.getSelectionModel();
            if (model instanceof Batched) {
                Batched<?> batchedModel = (Batched<?>) model;
                batchedModel.commitBatchSelect();
            }

            removeNativeHandler();
        }
    }

    private class SpaceKeyDownSelectHandler implements BodyKeyDownHandler {

        private HandlerRegistration scrollHandler = null;
        private boolean spaceDown = false;

        @Override
        public void onKeyDown(GridKeyDownEvent event) {
            if (event.getNativeKeyCode() != KeyCodes.KEY_SPACE || spaceDown) {
                return;
            }

            spaceDown = true;
            Cell active = event.getActiveCell();
            final int rowIndex = active.getRow();

            if (scrollHandler != null) {
                scrollHandler.removeHandler();
                scrollHandler = null;
            }

            scrollHandler = grid
                    .addDataAvailableHandler(new DataAvailableHandler() {

                        @Override
                        public void onDataAvailable(DataAvailableEvent event) {
                            if (event.getAvailableRows().contains(rowIndex)) {
                                setSelected(rowIndex, !isSelected(rowIndex));
                                scrollHandler.removeHandler();
                                scrollHandler = null;
                            }
                        }
                    });
            grid.scrollToRow(rowIndex, ScrollDestination.ANY);
        }

    }

    private static final String LOGICAL_ROW_PROPERTY_INT = "vEscalatorLogicalRow";

    private final Grid<T> grid;
    private HandlerRegistration nativePreviewHandlerRegistration;
    private final SpaceKeyDownSelectHandler handler = new SpaceKeyDownSelectHandler();
    private HandlerRegistration spaceDown;
    private HandlerRegistration spaceUp;

    private final AutoScrollHandler autoScrollHandler = new AutoScrollHandler();

    public MultiSelectionRenderer(final Grid<T> grid) {
        this.grid = grid;
        spaceDown = grid.addBodyKeyDownHandler(handler);
        spaceUp = grid.addBodyKeyUpHandler(new BodyKeyUpHandler() {

            @Override
            public void onKeyUp(GridKeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
                    handler.spaceDown = false;
                }
            }
        });
    }

    @Override
    public void destroy() {
        spaceDown.removeHandler();
        spaceUp.removeHandler();
        if (nativePreviewHandlerRegistration != null) {
            removeNativeHandler();
        }
    }

    @Override
    public void init(FlyweightCell cell) {
        final InputElement checkbox = InputElement.as(DOM.createInputCheck());
        cell.getElement().removeAllChildren();
        cell.getElement().appendChild(checkbox);
    }

    @Override
    public void render(final FlyweightCell cell, final Boolean data) {
        InputElement checkbox = InputElement.as(cell.getElement()
                .getFirstChildElement());
        checkbox.setChecked(data.booleanValue());
        checkbox.setPropertyInt(LOGICAL_ROW_PROPERTY_INT, cell.getRow());
    }

    @Override
    public Collection<String> getConsumedEvents() {
        final HashSet<String> events = new HashSet<String>();

        /*
         * this column's first interest is only to attach a NativePreventHandler
         * that does all the magic. These events are the beginning of that
         * cycle.
         */
        events.add(BrowserEvents.MOUSEDOWN);
        events.add(BrowserEvents.TOUCHSTART);

        return events;
    }

    @Override
    public boolean onBrowserEvent(final Cell cell, final NativeEvent event) {
        if (BrowserEvents.TOUCHSTART.equals(event.getType())
                || BrowserEvents.MOUSEDOWN.equals(event.getType())) {
            injectNativeHandler();
            int logicalRowIndex = getLogicalRowIndex(Element.as(event
                    .getEventTarget()));
            autoScrollHandler.start(logicalRowIndex);
            event.preventDefault();
            event.stopPropagation();
            return true;
        } else {
            throw new IllegalStateException("received unexpected event: "
                    + event.getType());
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

    private int getLogicalRowIndex(final Element target) {
        /*
         * We can't simply go backwards until we find a <tr> first element,
         * because of the table-in-table scenario. We need to, unfortunately, go
         * up from our known root.
         */
        final Element tbody = getTbodyElement();
        Element tr = tbody.getFirstChildElement();
        while (tr != null) {
            if (tr.isOrHasChild(target)) {
                final Element td = tr.getFirstChildElement();
                assert td != null : "Cell has disappeared";

                final Element checkbox = td.getFirstChildElement();
                assert checkbox != null : "Checkbox has disappeared";

                return checkbox.getPropertyInt(LOGICAL_ROW_PROPERTY_INT);
            }
            tr = tr.getNextSiblingElement();
        }
        return -1;
    }

    private Element getTbodyElement() {
        final Element root = grid.getElement();
        final Element tablewrapper = Element.as(root.getChild(2));
        if (tablewrapper != null) {
            final TableElement table = TableElement.as(tablewrapper
                    .getFirstChildElement());
            return table.getTBodies().getItem(0);
        } else {
            return null;
        }
    }

    protected boolean isSelected(final int logicalRow) {
        return grid.isSelected(grid.getDataSource().getRow(logicalRow));
    }

    protected void setSelected(final int logicalRow, final boolean select) {
        T row = grid.getDataSource().getRow(logicalRow);
        if (select) {
            grid.select(row);
        } else {
            grid.deselect(row);
        }
    }
}
