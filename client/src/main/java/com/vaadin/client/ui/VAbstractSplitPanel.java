/*
 * Copyright 2000-2021 Vaadin Ltd.
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

package com.vaadin.client.ui;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.client.ui.VAbstractSplitPanel.SplitterMoveHandler.SplitterMoveEvent;
import com.vaadin.client.widgets.Overlay;
import com.vaadin.shared.ui.Orientation;

/**
 * Base class for the SplitPanel widgets.
 *
 * @author Vaadin Ltd
 *
 */
public abstract class VAbstractSplitPanel extends ComplexPanel {

    private boolean enabled = false;

    /** Default classname for this widget. */
    public static final String CLASSNAME = "v-splitpanel";

    private static final int MIN_SIZE = 30;

    private Orientation orientation = Orientation.HORIZONTAL;

    Widget firstChild;

    Widget secondChild;

    private final Element wrapper = DOM.createDiv();

    private final Element firstContainer = DOM.createDiv();

    private final Element secondContainer = DOM.createDiv();

    /** For internal use only. May be removed or replaced in the future. */
    public final Element splitter = DOM.createDiv();

    private boolean resizing;

    private boolean resized = false;

    private int origX;

    private int origY;

    private int origMouseX;

    private int origMouseY;

    private boolean locked = false;

    private boolean positionReversed = false;

    /** For internal use only. May be removed or replaced in the future. */
    public List<String> componentStyleNames = Collections.emptyList();

    private Element draggingCurtain;

    /** For internal use only. May be removed or replaced in the future. */
    public ApplicationConnection client;

    /**
     * The current position of the split handle in either percentages or pixels
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public String position;

    /** For internal use only. May be removed or replaced in the future. */
    public String maximumPosition;

    /** For internal use only. May be removed or replaced in the future. */
    public String minimumPosition;

    private TouchScrollHandler touchScrollHandler;

    /**
     * @deprecated this element is no longer used or updated by the framework
     */
    @Deprecated
    protected Element scrolledContainer;

    /**
     * @deprecated this value is no longer used or updated by the framework
     */
    @Deprecated
    protected int origScrollTop;

    /**
     * Constructs a base widget for a SplitPanel component. Uses horizontal
     * orientation.
     */
    public VAbstractSplitPanel() {
        this(Orientation.HORIZONTAL);
    }

    /**
     * Constructs a base widget for a SplitPanel component with the given
     * orientation.
     *
     * @param orientation
     *            the orientation to use
     */
    @SuppressWarnings("deprecation")
    public VAbstractSplitPanel(Orientation orientation) {
        setElement(DOM.createDiv());
        switch (orientation) {
        case HORIZONTAL:
            setStyleName(CLASSNAME + "-horizontal");
            break;
        case VERTICAL:
        default:
            setStyleName(CLASSNAME + "-vertical");
            break;
        }
        // size below will be overridden in update from uidl, initial size
        // needed to keep IE alive
        setWidth(MIN_SIZE + "px");
        setHeight(MIN_SIZE + "px");
        constructDom();
        setOrientation(orientation);
        sinkEvents(Event.MOUSEEVENTS);

        makeScrollable();

        addDomHandler(event -> {
            // TODO When does this actually happen??
            getLogger().info("TOUCH CANCEL");
        }, TouchCancelEvent.getType());
        addDomHandler(event -> {
            Node target = event.getTouches().get(0).getTarget().cast();
            if (splitter.isOrHasChild(target)) {
                onMouseDown(Event.as(event.getNativeEvent()));
            }
        }, TouchStartEvent.getType());
        addDomHandler(event -> {
            if (resizing) {
                onMouseMove(Event.as(event.getNativeEvent()));
            }
        }, TouchMoveEvent.getType());
        addDomHandler(event -> {
            if (resizing) {
                onMouseUp(Event.as(event.getNativeEvent()));
            }
        }, TouchEndEvent.getType());

    }

    /**
     * Constructs the DOM structure for this widget.
     */
    protected void constructDom() {
        DOM.appendChild(splitter, DOM.createDiv()); // for styling
        DOM.appendChild(getElement(), wrapper);
        wrapper.getStyle().setPosition(Position.RELATIVE);
        wrapper.getStyle().setWidth(100, Unit.PCT);
        wrapper.getStyle().setHeight(100, Unit.PCT);

        DOM.appendChild(wrapper, firstContainer);
        DOM.appendChild(wrapper, splitter);
        DOM.appendChild(wrapper, secondContainer);

        splitter.getStyle().setPosition(Position.ABSOLUTE);
        secondContainer.getStyle().setPosition(Position.ABSOLUTE);

        setStylenames();
    }

    private void setOrientation(Orientation orientation) {
        this.orientation = orientation;

        if (orientation == Orientation.HORIZONTAL) {
            splitter.getStyle().setHeight(100, Unit.PCT);
            splitter.getStyle().setTop(0, Unit.PX);
            firstContainer.getStyle().setHeight(100, Unit.PCT);
            secondContainer.getStyle().setTop(0, Unit.PX);
            secondContainer.getStyle().setHeight(100, Unit.PCT);
        } else {
            splitter.getStyle().setWidth(100, Unit.PCT);
            splitter.getStyle().setLeft(0, Unit.PX);
            firstContainer.getStyle().setWidth(100, Unit.PCT);
            secondContainer.getStyle().setWidth(100, Unit.PCT);
        }
    }

    @Override
    public boolean remove(Widget w) {
        boolean removed = super.remove(w);
        if (removed) {
            if (firstChild == w) {
                firstChild = null;
            } else {
                secondChild = null;
            }
        }
        return removed;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param newValue
     *            {@code true} if split position should be locked, {@code false}
     *            otherwise
     */
    public void setLocked(boolean newValue) {
        if (locked != newValue) {
            locked = newValue;
            splitterSize = -1;
            setStylenames();
        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param reversed
     *            {@code true} if split position should be measured from the
     *            second region, {@code false} (default) if from the the first
     *            region
     */
    public void setPositionReversed(boolean reversed) {
        if (positionReversed != reversed) {
            if (orientation == Orientation.HORIZONTAL) {
                splitter.getStyle().clearRight();
                splitter.getStyle().clearLeft();
            } else if (orientation == Orientation.VERTICAL) {
                splitter.getStyle().clearTop();
                splitter.getStyle().clearBottom();
            }

            positionReversed = reversed;
        }
    }

    /**
     * Converts given split position string (in pixels or percentage) to a
     * floating point pixel value.
     *
     * @param pos
     * @return
     */
    private float convertToPixels(String pos) {
        float posAsFloat;
        if (pos.indexOf("%") > 0) {
            posAsFloat = Math.round(
                    Float.parseFloat(pos.substring(0, pos.length() - 1)) / 100
                            * (orientation == Orientation.HORIZONTAL
                                    ? getOffsetWidth()
                                    : getOffsetHeight()));
        } else {
            posAsFloat = Float.parseFloat(pos.substring(0, pos.length() - 2));
        }
        return posAsFloat;
    }

    /**
     * Converts given split position string (in pixels or percentage) to a float
     * percentage value.
     *
     * @param pos
     * @return
     */
    private float convertToPercentage(String pos) {
        if (pos.endsWith("px")) {
            float pixelPosition = Float
                    .parseFloat(pos.substring(0, pos.length() - 2));
            int offsetLength = orientation == Orientation.HORIZONTAL
                    ? getOffsetWidth()
                    : getOffsetHeight();

            // Take splitter size into account at the edge
            if (pixelPosition + getSplitterSize() >= offsetLength) {
                return 100;
            }

            return pixelPosition / offsetLength * 100;
        } else {
            assert pos.endsWith("%");
            return Float.parseFloat(pos.substring(0, pos.length() - 1));
        }
    }

    /**
     * Returns the given position clamped to the range between current minimum
     * and maximum positions.
     *
     * TODO Should this be in the connector?
     *
     * @param pos
     *            Position of the splitter as a CSS string, either pixels or a
     *            percentage.
     * @return minimumPosition if pos is less than minimumPosition;
     *         maximumPosition if pos is greater than maximumPosition; pos
     *         otherwise.
     */
    private String checkSplitPositionLimits(String pos) {
        float positionAsFloat = convertToPixels(pos);

        if (maximumPosition != null
                && convertToPixels(maximumPosition) < positionAsFloat) {
            pos = maximumPosition;
        } else if (minimumPosition != null
                && convertToPixels(minimumPosition) > positionAsFloat) {
            pos = minimumPosition;
        }
        return pos;
    }

    /**
     * Converts given string to the same units as the split position is.
     *
     * @param pos
     *            position to be converted
     * @return converted position string
     */
    private String convertToPositionUnits(String pos) {
        if (position.indexOf("%") != -1 && pos.indexOf("%") == -1) {
            // position is in percentage, pos in pixels
            pos = convertToPercentage(pos) + "%";
        } else if (position.indexOf("px") > 0 && pos.indexOf("px") == -1) {
            // position is in pixels and pos in percentage
            pos = convertToPixels(pos) + "px";
        }

        return pos;
    }

    /**
     * Sets the position of the splitter element.
     *
     * @param pos
     *            the required position as either percentage or pixels
     */
    public void setSplitPosition(String pos) {
        setSplitPosition(pos, true);
    }

    private void setSplitPosition(String pos, boolean rememberPosition) {
        if (pos == null) {
            return;
        }

        pos = checkSplitPositionLimits(pos);
        if (rememberPosition && !pos.equals(position)) {
            position = convertToPositionUnits(pos);
        }

        // Convert percentage values to pixels
        if (pos.indexOf("%") > 0) {
            int size = orientation == Orientation.HORIZONTAL ? getOffsetWidth()
                    : getOffsetHeight();
            float percentage = Float
                    .parseFloat(pos.substring(0, pos.length() - 1));
            pos = percentage / 100 * size + "px";
        }

        String attributeName;
        if (orientation == Orientation.HORIZONTAL) {
            if (positionReversed) {
                attributeName = "right";
            } else {
                attributeName = "left";
            }
        } else {
            if (positionReversed) {
                attributeName = "bottom";
            } else {
                attributeName = "top";
            }
        }

        Style style = splitter.getStyle();
        if (!pos.equals(style.getProperty(attributeName))) {
            style.setProperty(attributeName, pos);
            updateSizes();
        }
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void updateSizes() {
        if (!isAttached()) {
            return;
        }

        switch (orientation) {
        case HORIZONTAL:
            horizontalOrientationUpdateSizes();
            break;
        case VERTICAL:
            verticalOrientationUpdateSizes();
            break;
        default:
            throw new IllegalStateException(
                    "New orientation option has been added "
                            + "without configuring handling for it.");
        }
    }

    private void verticalOrientationUpdateSizes() {
        int wholeSize = wrapper.getPropertyInt("clientHeight");
        int pixelPosition = splitter.getPropertyInt("offsetTop");

        // reposition splitter in case it is out of box
        if (pixelPosition > 0 && pixelPosition + getSplitterSize() > wholeSize
                || positionReversed && pixelPosition < 0) {
            pixelPosition = wholeSize - getSplitterSize();
            if (pixelPosition < 0) {
                pixelPosition = 0;
            }
            // Move splitter within bounds, but don't remember the new value
            setSplitPosition(pixelPosition + "px", false);
            return;
        }

        firstContainer.getStyle().setHeight(pixelPosition, Unit.PX);
        int secondContainerHeight = wholeSize - pixelPosition
                - getSplitterSize();
        if (secondContainerHeight < 0) {
            secondContainerHeight = 0;
        }
        secondContainer.getStyle().setHeight(secondContainerHeight, Unit.PX);
        secondContainer.getStyle().setTop(pixelPosition + getSplitterSize(),
                Unit.PX);

        LayoutManager layoutManager = LayoutManager.get(client);
        ConnectorMap connectorMap = ConnectorMap.get(client);
        if (firstChild != null) {
            ComponentConnector connector = connectorMap
                    .getConnector(firstChild);
            if (connector.isRelativeHeight()) {
                layoutManager.reportHeightAssignedToRelative(connector,
                        pixelPosition);
            } else {
                layoutManager.setNeedsMeasure(connector);
            }
        }
        if (secondChild != null) {
            ComponentConnector connector = connectorMap
                    .getConnector(secondChild);
            if (connector.isRelativeHeight()) {
                layoutManager.reportHeightAssignedToRelative(connector,
                        secondContainerHeight);
            } else {
                layoutManager.setNeedsMeasure(connector);
            }
        }
    }

    private void horizontalOrientationUpdateSizes() {
        int wholeSize = wrapper.getPropertyInt("clientWidth");
        int pixelPosition = splitter.getPropertyInt("offsetLeft");

        // reposition splitter in case it is out of box
        if (pixelPosition > 0 && pixelPosition + getSplitterSize() > wholeSize
                || positionReversed && pixelPosition < 0) {
            pixelPosition = wholeSize - getSplitterSize();
            if (pixelPosition < 0) {
                pixelPosition = 0;
            }
            // Move splitter within bounds, but don't remember the new value
            setSplitPosition(pixelPosition + "px", false);
            return;
        }

        firstContainer.getStyle().setWidth(pixelPosition, Unit.PX);
        int secondContainerWidth = wholeSize - pixelPosition
                - getSplitterSize();
        if (secondContainerWidth < 0) {
            secondContainerWidth = 0;
        }
        secondContainer.getStyle().setWidth(secondContainerWidth, Unit.PX);
        secondContainer.getStyle().setLeft(pixelPosition + getSplitterSize(),
                Unit.PX);

        LayoutManager layoutManager = LayoutManager.get(client);
        ConnectorMap connectorMap = ConnectorMap.get(client);
        if (firstChild != null) {
            ComponentConnector connector = connectorMap
                    .getConnector(firstChild);
            if (connector.isRelativeWidth()) {
                layoutManager.reportWidthAssignedToRelative(connector,
                        pixelPosition);
            } else {
                layoutManager.setNeedsMeasure(connector);
            }
        }
        if (secondChild != null) {
            ComponentConnector connector = connectorMap
                    .getConnector(secondChild);
            if (connector.isRelativeWidth()) {
                layoutManager.reportWidthAssignedToRelative(connector,
                        secondContainerWidth);
            } else {
                layoutManager.setNeedsMeasure(connector);
            }
        }

        // previous layout pass may have changed the position already, needs to
        // be reset before calculating which positioning should be used
        secondContainer.getStyle().setPosition(Position.ABSOLUTE);
        if (getOffsetHeight() == 0) {
            secondContainer.getStyle().setPosition(Position.RELATIVE);
        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param w
     *            the widget to set to the first region or {@code null} to
     *            remove previously set widget
     */
    public void setFirstWidget(Widget w) {
        if (firstChild == w) {
            return;
        }
        if (firstChild != null) {
            firstChild.removeFromParent();
        }
        if (w != null) {
            super.add(w, firstContainer);
        }
        firstChild = w;
    }

    /**
     * Returns the widget in the first region, if any.
     *
     * @return the widget in the first region, or {@code null} if not set
     */
    public Widget getFirstWidget() {
        return firstChild;
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @param w
     *            the widget to set to the second region or {@code null} to
     *            remove previously set widget
     */
    public void setSecondWidget(Widget w) {
        if (secondChild == w) {
            return;
        }
        if (secondChild != null) {
            secondChild.removeFromParent();
        }
        if (w != null) {
            super.add(w, secondContainer);
        }
        secondChild = w;
    }

    /**
     * Returns the widget in the second region, if any.
     *
     * @return the widget in the second region, or {@code null} if not set
     */
    public Widget getSecondWidget() {
        return secondChild;
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEMOVE:
            // case Event.ONTOUCHMOVE:
            if (resizing) {
                onMouseMove(event);
            }
            break;
        case Event.ONMOUSEDOWN:
            // case Event.ONTOUCHSTART:
            onMouseDown(event);
            break;
        case Event.ONMOUSEOUT:
            // Dragging curtain interferes with click events if added in
            // mousedown so we add it only when needed i.e., if the mouse moves
            // outside the splitter.
            if (resizing) {
                showDraggingCurtain();
            }
            break;
        case Event.ONMOUSEUP:
            // case Event.ONTOUCHEND:
            if (resizing) {
                onMouseUp(event);
            }
            break;
        case Event.ONCLICK:
            stopResize();
            resizing = false;
            break;
        default:
            // NOP
            break;
        }
        // Only fire click event listeners if the splitter isn't moved
        if (WidgetUtil.isTouchEvent(event) || !resized) {
            super.onBrowserEvent(event);
        } else if (DOM.eventGetType(event) == Event.ONMOUSEUP) {
            // Reset the resized flag after a mouseup has occurred so the next
            // mousedown/mouseup can be interpreted as a click.
            resized = false;
        }
    }

    /**
     * Handle initiating content resize and moving of the split position when
     * clicking the splitter with a mouse. If the click targets any other
     * element, the split position is locked, or this split panel is not
     * enabled, nothing is done.
     *
     * @param event
     *            the browser event
     */
    public void onMouseDown(Event event) {
        if (locked || !isEnabled()) {
            return;
        }
        final Element trg = event.getEventTarget().cast();
        if (trg == splitter || trg == DOM.getChild(splitter, 0)) {
            startResize();
            resizing = true;
            DOM.setCapture(getElement());
            origX = splitter.getPropertyInt("offsetLeft");
            origY = splitter.getPropertyInt("offsetTop");
            origMouseX = WidgetUtil.getTouchOrMouseClientX(event);
            origMouseY = WidgetUtil.getTouchOrMouseClientY(event);
            event.stopPropagation();
            event.preventDefault();
        }
    }

    /**
     * Called when starting splitter drag resize of contents.
     *
     * @since 7.5.1
     */
    protected abstract void startResize();

    /**
     * Called when stopping splitter drag resize of contents.
     *
     * @since 7.5.1
     */
    protected abstract void stopResize();

    /**
     * Gets the first region's container element.
     *
     * @since 7.5.1
     * @return the container element
     */
    protected Element getFirstContainer() {
        return firstContainer;
    }

    /**
     * Gets the second region's container element.
     *
     * @since 7.5.1
     * @return the container element
     */
    protected Element getSecondContainer() {
        return secondContainer;
    }

    /**
     * Handle updating the splitter position when dragging the splitter with a
     * mouse. This should only be called if content resizing has been
     * successfully initialized via a mouse down event.
     *
     * @param event
     *            the browser event
     */
    public void onMouseMove(Event event) {
        switch (orientation) {
        case HORIZONTAL:
            final int x = WidgetUtil.getTouchOrMouseClientX(event);
            onHorizontalMouseMove(x);
            break;
        case VERTICAL:
        default:
            final int y = WidgetUtil.getTouchOrMouseClientY(event);
            onVerticalMouseMove(y);
            break;
        }

    }

    private void onHorizontalMouseMove(int x) {
        int newX = origX + x - origMouseX;
        if (newX < 0) {
            newX = 0;
        }
        if (newX + getSplitterSize() > getOffsetWidth()) {
            newX = getOffsetWidth() - getSplitterSize();
        }

        if (position.indexOf("%") > 0) {
            position = convertToPositionUnits(newX + "px");
        } else {
            // Reversed position
            if (positionReversed) {
                position = getOffsetWidth() - newX - getSplitterSize() + "px";
            } else {
                position = newX + "px";
            }
        }

        if (origX != newX) {
            resized = true;
        }

        // Reversed position
        if (positionReversed) {
            newX = getOffsetWidth() - newX - getSplitterSize();
        }

        setSplitPosition(newX + "px");
    }

    private void onVerticalMouseMove(int y) {
        int newY = origY + y - origMouseY;
        if (newY < 0) {
            newY = 0;
        }

        if (newY + getSplitterSize() > getOffsetHeight()) {
            newY = getOffsetHeight() - getSplitterSize();
        }

        if (position.indexOf("%") > 0) {
            position = convertToPositionUnits(newY + "px");
        } else {
            // Reversed position
            if (positionReversed) {
                position = getOffsetHeight() - newY - getSplitterSize() + "px";
            } else {
                position = newY + "px";
            }
        }

        if (origY != newY) {
            resized = true;
        }

        // Reversed position
        if (positionReversed) {
            newY = getOffsetHeight() - newY - getSplitterSize();
        }

        setSplitPosition(newY + "px");
    }

    /**
     * Handle concluding the content resize when dragging the splitter with a
     * mouse. This should only be called if resizing has been successfully
     * initialized via a mouse down event.
     *
     * @param event
     *            the browser event
     */
    public void onMouseUp(Event event) {
        DOM.releaseCapture(getElement());
        hideDraggingCurtain();
        stopResize();
        resizing = false;
        if (!WidgetUtil.isTouchEvent(event)) {
            onMouseMove(event);
        }
        fireEvent(new SplitterMoveEvent(this));
    }

    /**
     * Event handler interface for moving the splitter element.
     *
     * @author Vaadin Ltd
     */
    public interface SplitterMoveHandler extends EventHandler {
        /**
         * Handle moving of the splitter element.
         *
         * @param event
         *            the splitter move event
         */
        public void splitterMoved(SplitterMoveEvent event);

        /**
         * Event class for splitter element move.
         *
         * @author Vaadin Ltd
         */
        public static class SplitterMoveEvent
                extends GwtEvent<SplitterMoveHandler> {

            /**
             * Handler type.
             */
            public static final Type<SplitterMoveHandler> TYPE = new Type<>();

            /**
             * Constructs a move event for the splitter element.
             *
             * @param splitPanel
             *            the split panel whose splitter is moved
             */
            public SplitterMoveEvent(Widget splitPanel) {
                // no default use for the given splitPanel
            }

            @Override
            public com.google.gwt.event.shared.GwtEvent.Type<SplitterMoveHandler> getAssociatedType() {
                return TYPE;
            }

            @Override
            protected void dispatch(SplitterMoveHandler handler) {
                handler.splitterMoved(this);
            }

        }
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * @return the current position of the split handle in either percentages or
     *         pixels
     */
    public String getSplitterPosition() {
        return position;
    }

    /**
     * Used in FF to avoid losing mouse capture when pointer is moved on an
     * iframe.
     */
    private void showDraggingCurtain() {
        if (!isDraggingCurtainRequired()) {
            return;
        }
        if (draggingCurtain == null) {
            draggingCurtain = DOM.createDiv();
            draggingCurtain.getStyle().setPosition(Position.ABSOLUTE);
            draggingCurtain.getStyle().setTop(0, Unit.PX);
            draggingCurtain.getStyle().setLeft(0, Unit.PX);
            draggingCurtain.getStyle().setWidth(100, Unit.PCT);
            draggingCurtain.getStyle().setHeight(100, Unit.PCT);
            draggingCurtain.getStyle().setZIndex(Overlay.Z_INDEX);

            DOM.appendChild(wrapper, draggingCurtain);
        }
    }

    /**
     * A dragging curtain is required in Gecko and Webkit.
     *
     * @return true if the browser requires a dragging curtain
     */
    private boolean isDraggingCurtainRequired() {
        return BrowserInfo.get().isGecko() || BrowserInfo.get().isWebkit();
    }

    /**
     * Hides dragging curtain
     */
    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            wrapper.removeChild(draggingCurtain);
            draggingCurtain = null;
        }
    }

    private int splitterSize = -1;

    private int getSplitterSize() {
        if (splitterSize < 0) {
            if (isAttached()) {
                switch (orientation) {
                case HORIZONTAL:
                    splitterSize = splitter.getPropertyInt("offsetWidth");
                    break;

                default:
                    splitterSize = splitter.getPropertyInt("offsetHeight");
                    break;
                }
            }
        }
        return splitterSize;
    }

    /** For internal use only. May be removed or replaced in the future. */
    public void setStylenames() {
        final String splitterClass = CLASSNAME
                + (orientation == Orientation.HORIZONTAL ? "-hsplitter"
                        : "-vsplitter");
        final String firstContainerClass = CLASSNAME + "-first-container";
        final String secondContainerClass = CLASSNAME + "-second-container";
        final String lockedSuffix = locked ? "-locked" : "";

        splitter.setClassName(splitterClass + lockedSuffix);
        firstContainer.setClassName(firstContainerClass);
        secondContainer.setClassName(secondContainerClass);

        for (String styleName : componentStyleNames) {
            splitter.addClassName(
                    splitterClass + "-" + styleName + lockedSuffix);
            firstContainer.addClassName(firstContainerClass + "-" + styleName);
            secondContainer
                    .addClassName(secondContainerClass + "-" + styleName);
        }
    }

    /**
     * Sets this split panel enabled.
     *
     * @param enabled
     *            {@code true} if enabled, {@code false} if disabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether this split panel is enabled or not.
     *
     * @return {@code true} if enabled, {@code false} if disabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Ensures the panels are scrollable e.g. after style name changes
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    public void makeScrollable() {
        if (touchScrollHandler == null) {
            touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this);
        }
        touchScrollHandler.addElement(firstContainer);
        touchScrollHandler.addElement(secondContainer);
    }

    private static Logger getLogger() {
        return Logger.getLogger(VAbstractSplitPanel.class.getName());
    }
}
