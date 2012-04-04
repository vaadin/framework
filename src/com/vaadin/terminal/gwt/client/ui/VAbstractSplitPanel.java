/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

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
import com.vaadin.terminal.gwt.client.ui.VAbstractSplitPanel.SplitterMoveHandler.SplitterMoveEvent;

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

    protected Element scrolledContainer;

    protected int origScrollTop;

    private TouchScrollDelegate touchScrollDelegate;

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

        addDomHandler(new TouchCancelHandler() {
            public void onTouchCancel(TouchCancelEvent event) {
                // TODO When does this actually happen??
                VConsole.log("TOUCH CANCEL");
            }
        }, TouchCancelEvent.getType());
        addDomHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                Node target = event.getTouches().get(0).getTarget().cast();
                if (splitter.isOrHasChild(target)) {
                    onMouseDown(Event.as(event.getNativeEvent()));
                } else {
                    getTouchScrollDelegate().onTouchStart(event);
                }
            }

        }, TouchStartEvent.getType());
        addDomHandler(new TouchMoveHandler() {
            public void onTouchMove(TouchMoveEvent event) {
                if (resizing) {
                    onMouseMove(Event.as(event.getNativeEvent()));
                }
            }
        }, TouchMoveEvent.getType());
        addDomHandler(new TouchEndHandler() {
            public void onTouchEnd(TouchEndEvent event) {
                if (resizing) {
                    onMouseUp(Event.as(event.getNativeEvent()));
                }
            }
        }, TouchEndEvent.getType());

    }

    private TouchScrollDelegate getTouchScrollDelegate() {
        if (touchScrollDelegate == null) {
            touchScrollDelegate = new TouchScrollDelegate(firstContainer,
                    secondContainer);
        }
        return touchScrollDelegate;
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

        DOM.setStyleAttribute(firstContainer, "overflow", "auto");
        DOM.setStyleAttribute(secondContainer, "overflow", "auto");

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

        DOM.setElementProperty(firstContainer, "className", CLASSNAME
                + "-first-container");
        DOM.setElementProperty(secondContainer, "className", CLASSNAME
                + "-second-container");
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

    void setSplitPosition(String pos) {
        if (pos == null) {
            return;
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
            if (layoutManager.isLayoutRunning()) {
                ConnectorMap connectorMap = ConnectorMap.get(client);
                if (firstChild != null) {
                    ComponentConnector connector = connectorMap
                            .getConnector(firstChild);
                    if (connector.isRelativeWidth()) {
                        layoutManager.reportWidthAssignedToRelative(connector,
                                pixelPosition);
                    }
                }
                if (secondChild != null) {
                    ComponentConnector connector = connectorMap
                            .getConnector(secondChild);
                    if (connector.isRelativeWidth()) {
                        layoutManager.reportWidthAssignedToRelative(connector,
                                secondContainerWidth);
                    }
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
            if (layoutManager.isLayoutRunning()) {
                ConnectorMap connectorMap = ConnectorMap.get(client);
                if (firstChild != null) {
                    ComponentConnector connector = connectorMap
                            .getConnector(firstChild);
                    if (connector.isRelativeHeight()) {
                        layoutManager.reportHeightAssignedToRelative(connector,
                                pixelPosition);
                    }
                }
                if (secondChild != null) {
                    ComponentConnector connector = connectorMap
                            .getConnector(secondChild);
                    if (connector.isRelativeHeight()) {
                        layoutManager.reportHeightAssignedToRelative(connector,
                                secondContainerHeight);
                    }
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
            float pos = newX;
            // 100% needs special handling
            if (newX + getSplitterSize() >= getOffsetWidth()) {
                pos = getOffsetWidth();
            }
            // Reversed position
            if (positionReversed) {
                pos = getOffsetWidth() - pos;
            }
            position = (pos / getOffsetWidth() * 100) + "%";
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
        client.doLayout(false);
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
            float pos = newY;
            // 100% needs special handling
            if (newY + getSplitterSize() >= getOffsetHeight()) {
                pos = getOffsetHeight();
            }
            // Reversed position
            if (positionReversed) {
                pos = getOffsetHeight() - pos - getSplitterSize();
            }
            position = pos / getOffsetHeight() * 100 + "%";
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
        client.doLayout(false);
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
        final String splitterSuffix = (orientation == ORIENTATION_HORIZONTAL ? "-hsplitter"
                : "-vsplitter");
        final String firstContainerSuffix = "-first-container";
        final String secondContainerSuffix = "-second-container";
        String lockedSuffix = "";

        String splitterStyle = CLASSNAME + splitterSuffix;
        String firstStyle = CLASSNAME + firstContainerSuffix;
        String secondStyle = CLASSNAME + secondContainerSuffix;

        if (locked) {
            splitterStyle = CLASSNAME + splitterSuffix + "-locked";
            lockedSuffix = "-locked";
        }
        for (String style : componentStyleNames) {
            splitterStyle += " " + CLASSNAME + splitterSuffix + "-" + style
                    + lockedSuffix;
            firstStyle += " " + CLASSNAME + firstContainerSuffix + "-" + style;
            secondStyle += " " + CLASSNAME + secondContainerSuffix + "-"
                    + style;
        }
        DOM.setElementProperty(splitter, "className", splitterStyle);
        DOM.setElementProperty(firstContainer, "className", firstStyle);
        DOM.setElementProperty(secondContainer, "className", secondStyle);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
