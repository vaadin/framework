/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.BrowserInfo;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.RenderInformation;
import com.itmill.toolkit.terminal.gwt.client.RenderSpace;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ISplitPanel extends ComplexPanel implements Container,
        ContainerResizedListener {
    public static final String CLASSNAME = "i-splitpanel";

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

    private int origX;

    private int origY;

    private int origMouseX;

    private int origMouseY;

    private boolean locked;

    private String splitterStyleName;

    private Element draggingCurtain;

    private ApplicationConnection client;

    private String width = null;

    private String height = null;

    private RenderSpace firstRenderSpace = new RenderSpace(0, 0, true);
    private RenderSpace secondRenderSpace = new RenderSpace(0, 0, true);

    RenderInformation renderInformation = new RenderInformation();

    private String id;

    private boolean immediate;

    public ISplitPanel() {
        this(ORIENTATION_HORIZONTAL);
    }

    public ISplitPanel(int orientation) {
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
        DOM.sinkEvents(splitter, (Event.MOUSEEVENTS));
        DOM.sinkEvents(getElement(), (Event.MOUSEEVENTS));
    }

    protected void constructDom() {
        DOM.appendChild(splitter, DOM.createDiv()); // for styling
        DOM.appendChild(getElement(), wrapper);
        DOM.setStyleAttribute(wrapper, "position", "relative");
        DOM.setStyleAttribute(wrapper, "width", "100%");
        DOM.setStyleAttribute(wrapper, "height", "100%");

        DOM.appendChild(wrapper, splitter);
        DOM.appendChild(wrapper, secondContainer);
        DOM.appendChild(wrapper, firstContainer);

        DOM.setStyleAttribute(splitter, "position", "absolute");
        DOM.setStyleAttribute(secondContainer, "position", "absolute");

    }

    private void setOrientation(int orientation) {
        this.orientation = orientation;
        if (orientation == ORIENTATION_HORIZONTAL) {
            DOM.setStyleAttribute(splitter, "height", "100%");
            DOM.setStyleAttribute(firstContainer, "height", "100%");
            DOM.setStyleAttribute(secondContainer, "height", "100%");
        } else {
            DOM.setStyleAttribute(splitter, "width", "100%");
            DOM.setStyleAttribute(firstContainer, "width", "100%");
            DOM.setStyleAttribute(secondContainer, "width", "100%");
        }

        splitterStyleName = CLASSNAME
                + (orientation == ORIENTATION_HORIZONTAL ? "-hsplitter"
                        : "-vsplitter");
        DOM.setElementProperty(splitter, "className", splitterStyleName);
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        id = uidl.getId();

        immediate = uidl.hasAttribute("immediate");

        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        renderInformation.updateSize(getElement());

        setSplitPosition(uidl.getStringAttribute("position"));

        locked = uidl.hasAttribute("locked");
        if (locked) {
            DOM.setElementProperty(splitter, "className", splitterStyleName
                    + "-locked");
        } else {
            DOM.setElementProperty(splitter, "className", splitterStyleName);
        }

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

        if (Util.isIE7()) {
            // Part III of IE7 hack
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    iLayout();
                }
            });
        }
    }

    private void setSplitPosition(String pos) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            DOM.setStyleAttribute(splitter, "left", pos);
        } else {
            DOM.setStyleAttribute(splitter, "top", pos);
        }
        iLayout();
    }

    /*
     * Calculates absolutely positioned container places/sizes (non-Javadoc)
     * 
     * @see com.itmill.toolkit.terminal.gwt.client.NeedsLayout#layout()
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
            if (pixelPosition > 0
                    && pixelPosition + getSplitterSize() > wholeSize) {
                pixelPosition = wholeSize - getSplitterSize();
                if (pixelPosition < 0) {
                    pixelPosition = 0;
                }
                setSplitPosition(pixelPosition + "px");
                return;
            }

            DOM
                    .setStyleAttribute(firstContainer, "width", pixelPosition
                            + "px");
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
            if (pixelPosition > 0
                    && pixelPosition + getSplitterSize() > wholeSize) {
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

        client.runDescendentsLayout(this);

        renderInformation.updateSize(getElement());

        // fixes scrollbars sometimes seen on webkit 528.5, but not in Safari
        // 3.1
        Util.runWebkitOverflowAutoFix(secondContainer);

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
            if (resizing) {
                onMouseMove(event);
            }
            break;
        case Event.ONMOUSEDOWN:
            onMouseDown(event);
            break;
        case Event.ONMOUSEUP:
            if (resizing) {
                onMouseUp(event);
            }
            break;
        case Event.ONCLICK:
            resizing = false;
            break;
        }
    }

    public void onMouseDown(Event event) {
        if (locked) {
            return;
        }
        final Element trg = DOM.eventGetTarget(event);
        if (DOM.compare(trg, splitter)
                || DOM.compare(trg, DOM.getChild(splitter, 0))) {
            resizing = true;
            if (BrowserInfo.get().isGecko()) {
                showDraggingCurtain();
            }
            DOM.setCapture(getElement());
            origX = DOM.getElementPropertyInt(splitter, "offsetLeft");
            origY = DOM.getElementPropertyInt(splitter, "offsetTop");
            origMouseX = DOM.eventGetClientX(event);
            origMouseY = DOM.eventGetClientY(event);
            DOM.eventCancelBubble(event, true);
            DOM.eventPreventDefault(event);
        }
    }

    public void onMouseMove(Event event) {
        switch (orientation) {
        case ORIENTATION_HORIZONTAL:
            final int x = DOM.eventGetClientX(event);
            onHorizontalMouseMove(x);
            break;
        case ORIENTATION_VERTICAL:
        default:
            final int y = DOM.eventGetClientY(event);
            onVerticalMouseMove(y);
            break;
        }
        iLayout();
    }

    private void onHorizontalMouseMove(int x) {
        int newX = origX + x - origMouseX;
        if (newX < 0) {
            newX = 0;
        }
        if (newX + getSplitterSize() > getOffsetWidth()) {
            newX = getOffsetWidth() - getSplitterSize();
        }
        DOM.setStyleAttribute(splitter, "left", newX + "px");
        updateSplitPosition(newX);
    }

    private void onVerticalMouseMove(int y) {
        int newY = origY + y - origMouseY;
        if (newY < 0) {
            newY = 0;
        }

        if (newY + getSplitterSize() > getOffsetHeight()) {
            newY = getOffsetHeight() - getSplitterSize();
        }
        DOM.setStyleAttribute(splitter, "top", newY + "px");
        updateSplitPosition(newY);
    }

    public void onMouseUp(Event event) {
        DOM.releaseCapture(getElement());
        if (BrowserInfo.get().isGecko()) {
            hideDraggingCurtain();
        }
        resizing = false;
        onMouseMove(event);
    }

    /**
     * Used in FF to avoid losing mouse capture when pointer is moved on an
     * iframe.
     */
    private void showDraggingCurtain() {
        if (draggingCurtain == null) {
            draggingCurtain = DOM.createDiv();
            DOM.setStyleAttribute(draggingCurtain, "position", "absolute");
            DOM.setStyleAttribute(draggingCurtain, "top", "0px");
            DOM.setStyleAttribute(draggingCurtain, "left", "0px");
            DOM.setStyleAttribute(draggingCurtain, "width", "100%");
            DOM.setStyleAttribute(draggingCurtain, "height", "100%");
            DOM.setStyleAttribute(draggingCurtain, "zIndex", ""
                    + IToolkitOverlay.Z_INDEX);
            DOM.appendChild(RootPanel.getBodyElement(), draggingCurtain);
        }
    }

    /**
     * Hides dragging curtain
     */
    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            DOM.removeChild(RootPanel.getBodyElement(), draggingCurtain);
            draggingCurtain = null;
        }
    }

    private static int splitterSize = -1;

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
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
        super.setWidth(width);
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
        // TODO Auto-generated method stub
    }

    public boolean requestLayout(Set<Paintable> child) {
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
        // TODO Auto-generated method stub

    }

    /**
     * Updates the new split position back to server.
     * 
     * @param pos
     *            The new position of the split handle.
     */
    private void updateSplitPosition(int pos) {
        // We always send pixel values to server
        client.updateVariable(id, "position", pos, immediate);
    }

}
