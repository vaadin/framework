/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.splitpanel;

import java.util.List;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate;
import com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate.TouchScrollHandler;
import com.vaadin.terminal.gwt.client.ui.VOverlay;
import com.vaadin.terminal.gwt.client.ui.splitpanel.VAbstractSplitPanel.SplitterMoveHandler.SplitterMoveEvent;

public class VAbstractSplitPanel extends ComplexPanel {

    private boolean enabled = false;

    public static final String CLASSNAME = "v-splitpanel";

    public static final int ORIENTATION_HORIZONTAL = 0;

    public static final int ORIENTATION_VERTICAL = 1;

    private static final int MIN_SIZE = 30;

    private int orientation = ORIENTATION_HORIZONTAL;

    Widget firstChild;

    Widget secondChild;

    private final Element wrapper = DOM.createDiv();

    private final Element firstContainer = DOM.createDiv();

    private final Element secondContainer = DOM.createDiv();

    final Element splitter = DOM.createDiv();

    private boolean resizing;

    private boolean resized = false;

    private int origX;

    private int origY;

    private int origMouseX;

    private int origMouseY;

    private boolean locked = false;

    private boolean positionReversed = false;

    List<String> componentStyleNames;

    private Element draggingCurtain;

    ApplicationConnection client;

    boolean immediate;

    /* The current position of the split handle in either percentages or pixels */
    String position;

    String maximumPosition;

    String minimumPosition;

    private final TouchScrollHandler touchScrollHandler;

    protected Element scrolledContainer;

    protected int origScrollTop;

    public VAbstractSplitPanel() {
        this(ORIENTATION_HORIZONTAL);
    }

    public VAbstractSplitPanel(int orientation) {
        setElement(DOM.createDiv());
        switch (orientation) {
        case ORIENTATION_HORIZONTAL:
            setStyleName(CLASSNAME + "-horizontal");
            break;
        case ORIENTATION_VERTICAL:
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

        touchScrollHandler = TouchScrollDelegate.enableTouchScrolling(this,
                firstContainer, secondContainer);

        addDomHandler(new TouchCancelHandler() {
            @Override
            public void onTouchCancel(TouchCancelEvent event) {
                // TODO When does this actually happen??
                VConsole.log("TOUCH CANCEL");
            }
        }, TouchCancelEvent.getType());
        addDomHandler(new TouchStartHandler() {
            @Override
            public void onTouchStart(TouchStartEvent event) {
                Node target = event.getTouches().get(0).getTarget().cast();
                if (splitter.isOrHasChild(target)) {
                    onMouseDown(Event.as(event.getNativeEvent()));
                }
            }
        }, TouchStartEvent.getType());
        addDomHandler(new TouchMoveHandler() {
            @Override
            public void onTouchMove(TouchMoveEvent event) {
                if (resizing) {
                    onMouseMove(Event.as(event.getNativeEvent()));
                }
            }
        }, TouchMoveEvent.getType());
        addDomHandler(new TouchEndHandler() {
            @Override
            public void onTouchEnd(TouchEndEvent event) {
                if (resizing) {
                    onMouseUp(Event.as(event.getNativeEvent()));
                }
            }
        }, TouchEndEvent.getType());

    }

    protected void constructDom() {
        DOM.appendChild(splitter, DOM.createDiv()); // for styling
        DOM.appendChild(getElement(), wrapper);
        DOM.setStyleAttribute(wrapper, "position", "relative");
        DOM.setStyleAttribute(wrapper, "width", "100%");
        DOM.setStyleAttribute(wrapper, "height", "100%");

        DOM.appendChild(wrapper, secondContainer);
        DOM.appendChild(wrapper, firstContainer);
        DOM.appendChild(wrapper, splitter);

        DOM.setStyleAttribute(splitter, "position", "absolute");
        DOM.setStyleAttribute(secondContainer, "position", "absolute");

        setStylenames();
    }

    private void setOrientation(int orientation) {
        this.orientation = orientation;
        if (orientation == ORIENTATION_HORIZONTAL) {
            DOM.setStyleAttribute(splitter, "height", "100%");
            DOM.setStyleAttribute(splitter, "top", "0");
            DOM.setStyleAttribute(firstContainer, "height", "100%");
            DOM.setStyleAttribute(secondContainer, "height", "100%");
        } else {
            DOM.setStyleAttribute(splitter, "width", "100%");
            DOM.setStyleAttribute(splitter, "left", "0");
            DOM.setStyleAttribute(firstContainer, "width", "100%");
            DOM.setStyleAttribute(secondContainer, "width", "100%");
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

    void setLocked(boolean newValue) {
        if (locked != newValue) {
            locked = newValue;
            splitterSize = -1;
            setStylenames();
        }
    }

    void setPositionReversed(boolean reversed) {
        if (positionReversed != reversed) {
            if (orientation == ORIENTATION_HORIZONTAL) {
                DOM.setStyleAttribute(splitter, "right", "");
                DOM.setStyleAttribute(splitter, "left", "");
            } else if (orientation == ORIENTATION_VERTICAL) {
                DOM.setStyleAttribute(splitter, "top", "");
                DOM.setStyleAttribute(splitter, "bottom", "");
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
            posAsFloat = Math.round(Float.parseFloat(pos.substring(0,
                    pos.length() - 1))
                    / 100
                    * (orientation == ORIENTATION_HORIZONTAL ? getOffsetWidth()
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
        float posAsFloat = 0;

        if (pos.indexOf("px") > 0) {
            int posAsInt = Integer.parseInt(pos.substring(0, pos.length() - 2));
            int offsetLength = orientation == ORIENTATION_HORIZONTAL ? getOffsetWidth()
                    : getOffsetHeight();

            // 100% needs special handling
            if (posAsInt + getSplitterSize() >= offsetLength) {
                posAsInt = offsetLength;
            }
            // Reversed position
            if (positionReversed) {
                posAsInt = offsetLength - posAsInt - getSplitterSize();
            }
            posAsFloat = ((float) posAsInt / (float) offsetLength * 100);

        } else {
            posAsFloat = Float.parseFloat(pos.substring(0, pos.length() - 1));
        }
        return posAsFloat;
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
        float maximumAsFloat = convertToPixels(maximumPosition);
        float minimumAsFloat = convertToPixels(minimumPosition);

        if (maximumAsFloat < positionAsFloat) {
            pos = maximumPosition;
        } else if (minimumAsFloat > positionAsFloat) {
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

    void setSplitPosition(String pos) {
        if (pos == null) {
            return;
        }

        pos = checkSplitPositionLimits(pos);
        if (!pos.equals(position)) {
            position = convertToPositionUnits(pos);
        }

        // Convert percentage values to pixels
        if (pos.indexOf("%") > 0) {
            int size = orientation == ORIENTATION_HORIZONTAL ? getOffsetWidth()
                    : getOffsetHeight();
            float percentage = Float.parseFloat(pos.substring(0,
                    pos.length() - 1));
            pos = percentage / 100 * size + "px";
        }

        String attributeName;
        if (orientation == ORIENTATION_HORIZONTAL) {
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

    void updateSizes() {
        if (!isAttached()) {
            return;
        }

        int wholeSize;
        int pixelPosition;

        switch (orientation) {
        case ORIENTATION_HORIZONTAL:
            wholeSize = DOM.getElementPropertyInt(wrapper, "clientWidth");
            pixelPosition = DOM.getElementPropertyInt(splitter, "offsetLeft");

            // reposition splitter in case it is out of box
            if ((pixelPosition > 0 && pixelPosition + getSplitterSize() > wholeSize)
                    || (positionReversed && pixelPosition < 0)) {
                pixelPosition = wholeSize - getSplitterSize();
                if (pixelPosition < 0) {
                    pixelPosition = 0;
                }
                setSplitPosition(pixelPosition + "px");
                return;
            }

            DOM.setStyleAttribute(firstContainer, "width", pixelPosition + "px");
            int secondContainerWidth = (wholeSize - pixelPosition - getSplitterSize());
            if (secondContainerWidth < 0) {
                secondContainerWidth = 0;
            }
            DOM.setStyleAttribute(secondContainer, "width",
                    secondContainerWidth + "px");
            DOM.setStyleAttribute(secondContainer, "left",
                    (pixelPosition + getSplitterSize()) + "px");

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
            break;
        case ORIENTATION_VERTICAL:
            wholeSize = DOM.getElementPropertyInt(wrapper, "clientHeight");
            pixelPosition = DOM.getElementPropertyInt(splitter, "offsetTop");

            // reposition splitter in case it is out of box
            if ((pixelPosition > 0 && pixelPosition + getSplitterSize() > wholeSize)
                    || (positionReversed && pixelPosition < 0)) {
                pixelPosition = wholeSize - getSplitterSize();
                if (pixelPosition < 0) {
                    pixelPosition = 0;
                }
                setSplitPosition(pixelPosition + "px");
                return;
            }

            DOM.setStyleAttribute(firstContainer, "height", pixelPosition
                    + "px");
            int secondContainerHeight = (wholeSize - pixelPosition - getSplitterSize());
            if (secondContainerHeight < 0) {
                secondContainerHeight = 0;
            }
            DOM.setStyleAttribute(secondContainer, "height",
                    secondContainerHeight + "px");
            DOM.setStyleAttribute(secondContainer, "top",
                    (pixelPosition + getSplitterSize()) + "px");

            layoutManager = LayoutManager.get(client);
            connectorMap = ConnectorMap.get(client);
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
            break;
        }
    }

    void setFirstWidget(Widget w) {
        if (firstChild != null) {
            firstChild.removeFromParent();
        }
        if (w != null) {
            super.add(w, firstContainer);
        }
        firstChild = w;
    }

    void setSecondWidget(Widget w) {
        if (secondChild != null) {
            secondChild.removeFromParent();
        }
        if (w != null) {
            super.add(w, secondContainer);
        }
        secondChild = w;
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
            resizing = false;
            break;
        }
        // Only fire click event listeners if the splitter isn't moved
        if (Util.isTouchEvent(event) || !resized) {
            super.onBrowserEvent(event);
        } else if (DOM.eventGetType(event) == Event.ONMOUSEUP) {
            // Reset the resized flag after a mouseup has occured so the next
            // mousedown/mouseup can be interpreted as a click.
            resized = false;
        }
    }

    public void onMouseDown(Event event) {
        if (locked || !isEnabled()) {
            return;
        }
        final Element trg = event.getEventTarget().cast();
        if (trg == splitter || trg == DOM.getChild(splitter, 0)) {
            resizing = true;
            DOM.setCapture(getElement());
            origX = DOM.getElementPropertyInt(splitter, "offsetLeft");
            origY = DOM.getElementPropertyInt(splitter, "offsetTop");
            origMouseX = Util.getTouchOrMouseClientX(event);
            origMouseY = Util.getTouchOrMouseClientY(event);
            event.stopPropagation();
            event.preventDefault();
        }
    }

    public void onMouseMove(Event event) {
        switch (orientation) {
        case ORIENTATION_HORIZONTAL:
            final int x = Util.getTouchOrMouseClientX(event);
            onHorizontalMouseMove(x);
            break;
        case ORIENTATION_VERTICAL:
        default:
            final int y = Util.getTouchOrMouseClientY(event);
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
                position = (getOffsetWidth() - newX - getSplitterSize()) + "px";
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
                position = (getOffsetHeight() - newY - getSplitterSize())
                        + "px";
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

    public void onMouseUp(Event event) {
        DOM.releaseCapture(getElement());
        hideDraggingCurtain();
        resizing = false;
        if (!Util.isTouchEvent(event)) {
            onMouseMove(event);
        }
        fireEvent(new SplitterMoveEvent(this));
    }

    public interface SplitterMoveHandler extends EventHandler {
        public void splitterMoved(SplitterMoveEvent event);

        public static class SplitterMoveEvent extends
                GwtEvent<SplitterMoveHandler> {

            public static final Type<SplitterMoveHandler> TYPE = new Type<SplitterMoveHandler>();

            private Widget splitPanel;

            public SplitterMoveEvent(Widget splitPanel) {
                this.splitPanel = splitPanel;
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

    String getSplitterPosition() {
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
            DOM.setStyleAttribute(draggingCurtain, "position", "absolute");
            DOM.setStyleAttribute(draggingCurtain, "top", "0px");
            DOM.setStyleAttribute(draggingCurtain, "left", "0px");
            DOM.setStyleAttribute(draggingCurtain, "width", "100%");
            DOM.setStyleAttribute(draggingCurtain, "height", "100%");
            DOM.setStyleAttribute(draggingCurtain, "zIndex", ""
                    + VOverlay.Z_INDEX);

            DOM.appendChild(wrapper, draggingCurtain);
        }
    }

    /**
     * A dragging curtain is required in Gecko and Webkit.
     * 
     * @return true if the browser requires a dragging curtain
     */
    private boolean isDraggingCurtainRequired() {
        return (BrowserInfo.get().isGecko() || BrowserInfo.get().isWebkit());
    }

    /**
     * Hides dragging curtain
     */
    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            DOM.removeChild(wrapper, draggingCurtain);
            draggingCurtain = null;
        }
    }

    private int splitterSize = -1;

    private int getSplitterSize() {
        if (splitterSize < 0) {
            if (isAttached()) {
                switch (orientation) {
                case ORIENTATION_HORIZONTAL:
                    splitterSize = DOM.getElementPropertyInt(splitter,
                            "offsetWidth");
                    break;

                default:
                    splitterSize = DOM.getElementPropertyInt(splitter,
                            "offsetHeight");
                    break;
                }
            }
        }
        return splitterSize;
    }

    void setStylenames() {
        final String splitterClass = CLASSNAME
                + (orientation == ORIENTATION_HORIZONTAL ? "-hsplitter"
                        : "-vsplitter");
        final String firstContainerClass = CLASSNAME + "-first-container";
        final String secondContainerClass = CLASSNAME + "-second-container";
        final String lockedSuffix = locked ? "-locked" : "";

        splitter.setClassName(splitterClass + lockedSuffix);
        firstContainer.setClassName(firstContainerClass);
        secondContainer.setClassName(secondContainerClass);

        for (String styleName : componentStyleNames) {
            splitter.addClassName(splitterClass + "-" + styleName
                    + lockedSuffix);
            firstContainer.addClassName(firstContainerClass + "-" + styleName);
            secondContainer
                    .addClassName(secondContainerClass + "-" + styleName);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
