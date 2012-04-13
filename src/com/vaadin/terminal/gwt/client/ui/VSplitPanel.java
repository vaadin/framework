/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderInformation;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;

public class VSplitPanel extends ComplexPanel implements Container,
        ContainerResizedListener {

    private boolean enabled = false;

    public static final String CLASSNAME = "v-splitpanel";

    public static final String SPLITTER_CLICK_EVENT_IDENTIFIER = "sp_click";

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            SPLITTER_CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            if ((Event.getEventsSunk(splitter) & Event.getTypeInt(type
                    .getName())) != 0) {
                // If we are already sinking the event for the splitter we do
                // not want to additionally sink it for the root element
                return addHandler(handler, type);
            } else {
                return addDomHandler(handler, type);
            }
        }

        @Override
        public void onContextMenu(
                com.google.gwt.event.dom.client.ContextMenuEvent event) {
            Element target = event.getNativeEvent().getEventTarget().cast();
            if (splitter.isOrHasChild(target)) {
                super.onContextMenu(event);
            }
        };

        @Override
        protected void fireClick(NativeEvent event) {
            Element target = event.getEventTarget().cast();
            if (splitter.isOrHasChild(target)) {
                super.fireClick(event);
            }
        }

        @Override
        protected Element getRelativeToElement() {
            return null;
        }

    };

    public static final int ORIENTATION_HORIZONTAL = 0;

    public static final int ORIENTATION_VERTICAL = 1;

    private static final int MIN_SIZE = 30;

    private int orientation = ORIENTATION_HORIZONTAL;

    private Widget firstChild;

    private Widget secondChild;

    private final Element wrapper = DOM.createDiv();

    private final Element firstContainer = DOM.createDiv();

    private final Element secondContainer = DOM.createDiv();

    private final Element splitter = DOM.createDiv();

    private boolean resizing;

    private boolean resized = false;

    private int origX;

    private int origY;

    private int origMouseX;

    private int origMouseY;

    private boolean locked = false;

    private boolean positionReversed = false;

    private String[] componentStyleNames;

    private Element draggingCurtain;

    private ApplicationConnection client;

    private String width = "";

    private String height = "";

    private RenderSpace firstRenderSpace = new RenderSpace(0, 0, true);
    private RenderSpace secondRenderSpace = new RenderSpace(0, 0, true);

    RenderInformation renderInformation = new RenderInformation();

    private String id;

    private boolean immediate;

    private boolean rendering = false;

    /* The current position of the split handle in either percentages or pixels */
    private String position;

    protected Element scrolledContainer;

    protected int origScrollTop;

    private TouchScrollDelegate touchScrollDelegate;

    public VSplitPanel() {
        this(ORIENTATION_HORIZONTAL);
    }

    public VSplitPanel(int orientation) {
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

        if (BrowserInfo.get().requiresTouchScrollDelegate()) {
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
        }
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

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();
        rendering = true;

        immediate = uidl.hasAttribute("immediate");

        if (client.updateComponent(this, uidl, true)) {
            rendering = false;
            return;
        }
        setEnabled(!uidl.getBooleanAttribute("disabled"));

        clickEventHandler.handleEventHandlerRegistration(client);
        if (uidl.hasAttribute("style")) {
            componentStyleNames = uidl.getStringAttribute("style").split(" ");
        } else {
            componentStyleNames = new String[0];
        }

        setLocked(uidl.getBooleanAttribute("locked"));

        setPositionReversed(uidl.getBooleanAttribute("reversed"));

        setStylenames();

        position = uidl.getStringAttribute("position");
        setSplitPosition(position);

        final Paintable newFirstChild = client.getPaintable(uidl
                .getChildUIDL(0));
        final Paintable newSecondChild = client.getPaintable(uidl
                .getChildUIDL(1));
        if (firstChild != newFirstChild) {
            if (firstChild != null) {
                client.unregisterPaintable((Paintable) firstChild);
            }
            setFirstWidget((Widget) newFirstChild);
        }
        if (secondChild != newSecondChild) {
            if (secondChild != null) {
                client.unregisterPaintable((Paintable) secondChild);
            }
            setSecondWidget((Widget) newSecondChild);
        }
        newFirstChild.updateFromUIDL(uidl.getChildUIDL(0), client);
        newSecondChild.updateFromUIDL(uidl.getChildUIDL(1), client);

        renderInformation.updateSize(getElement());

        if (BrowserInfo.get().isIE7()) {
            // Part III of IE7 hack
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    iLayout();
                }
            });
        }

        // This is needed at least for cases like #3458 to take
        // appearing/disappearing scrollbars into account.
        client.runDescendentsLayout(this);

        rendering = false;

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

    private void setLocked(boolean newValue) {
        if (locked != newValue) {
            locked = newValue;
            splitterSize = -1;
            setStylenames();
        }
    }

    private void setPositionReversed(boolean reversed) {
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

    private void setSplitPosition(String pos) {
        if (pos == null) {
            return;
        }

        // Convert percentage values to pixels
        if (pos.indexOf("%") > 0) {
            pos = Float.parseFloat(pos.substring(0, pos.length() - 1))
                    / 100
                    * (orientation == ORIENTATION_HORIZONTAL ? getOffsetWidth()
                            : getOffsetHeight()) + "px";
        }

        if (orientation == ORIENTATION_HORIZONTAL) {
            if (positionReversed) {
                DOM.setStyleAttribute(splitter, "right", pos);
            } else {
                DOM.setStyleAttribute(splitter, "left", pos);
            }
        } else {
            if (positionReversed) {
                DOM.setStyleAttribute(splitter, "bottom", pos);
            } else {
                DOM.setStyleAttribute(splitter, "top", pos);
            }
        }

        iLayout();
        client.runDescendentsLayout(this);
    }

    /*
     * Calculates absolutely positioned container places/sizes (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.NeedsLayout#layout()
     */
    public void iLayout() {
        if (!isAttached()) {
            return;
        }

        renderInformation.updateSize(getElement());

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

            int contentHeight = renderInformation.getRenderedSize().getHeight();
            firstRenderSpace.setHeight(contentHeight);
            firstRenderSpace.setWidth(pixelPosition);
            secondRenderSpace.setHeight(contentHeight);
            secondRenderSpace.setWidth(secondContainerWidth);

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

            int contentWidth = renderInformation.getRenderedSize().getWidth();
            firstRenderSpace.setHeight(pixelPosition);
            firstRenderSpace.setWidth(contentWidth);
            secondRenderSpace.setHeight(secondContainerHeight);
            secondRenderSpace.setWidth(contentWidth);

            break;
        }

        // fixes scrollbars issues on webkit based browsers
        Util.runWebkitOverflowAutoFix(secondContainer);
        Util.runWebkitOverflowAutoFix(firstContainer);

    }

    private void setFirstWidget(Widget w) {
        if (firstChild != null) {
            firstChild.removeFromParent();
        }
        super.add(w, firstContainer);
        firstChild = w;
    }

    private void setSecondWidget(Widget w) {
        if (secondChild != null) {
            secondChild.removeFromParent();
        }
        super.add(w, secondContainer);
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
                pos = getOffsetWidth() - pos - getSplitterSize();
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
    }

    public void onMouseUp(Event event) {
        DOM.releaseCapture(getElement());
        hideDraggingCurtain();
        resizing = false;
        if (!Util.isTouchEvent(event)) {
            onMouseMove(event);
        }
        updateSplitPositionToServer();
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

    @Override
    public void setHeight(String height) {
        if (this.height.equals(height)) {
            return;
        }

        this.height = height;
        super.setHeight(height);

        if (!rendering && client != null) {
            setSplitPosition(position);
        }
    }

    @Override
    public void setWidth(String width) {
        if (this.width.equals(width)) {
            return;
        }

        this.width = width;
        super.setWidth(width);

        if (!rendering && client != null) {
            setSplitPosition(position);
        }
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == firstChild) {
            return firstRenderSpace;
        } else if (child == secondChild) {
            return secondRenderSpace;
        }

        return null;
    }

    public boolean hasChildComponent(Widget component) {
        return (component != null && (component == firstChild || component == secondChild));
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        if (oldComponent == firstChild) {
            setFirstWidget(newComponent);
        } else if (oldComponent == secondChild) {
            setSecondWidget(newComponent);
        }
    }

    public boolean requestLayout(Set<Paintable> child) {
        // content size change might cause change to its available space
        // (scrollbars)
        for (Paintable paintable : child) {
            client.handleComponentRelativeSize((Widget) paintable);
        }
        if (height != null && width != null) {
            /*
             * If the height and width has been specified the child components
             * cannot make the size of the layout change
             */

            return true;
        }

        if (renderInformation.updateSize(getElement())) {
            return false;
        } else {
            return true;
        }

    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // TODO Implement caption handling
    }

    /**
     * Updates the new split position back to server.
     */
    private void updateSplitPositionToServer() {
        float pos = 0;
        if (position.indexOf("%") > 0) {
            pos = Float.valueOf(position.substring(0, position.length() - 1));
        } else {
            pos = Integer
                    .parseInt(position.substring(0, position.length() - 2));
        }
        client.updateVariable(id, "position", pos, immediate);
    }

    private void setStylenames() {
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
        for (int i = 0; i < componentStyleNames.length; i++) {
            splitterStyle += " " + CLASSNAME + splitterSuffix + "-"
                    + componentStyleNames[i] + lockedSuffix;
            firstStyle += " " + CLASSNAME + firstContainerSuffix + "-"
                    + componentStyleNames[i];
            secondStyle += " " + CLASSNAME + secondContainerSuffix + "-"
                    + componentStyleNames[i];
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
