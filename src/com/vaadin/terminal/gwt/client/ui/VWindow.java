/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.Console;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;

/**
 * "Sub window" component.
 * 
 * @author Vaadin Ltd
 */
public class VWindow extends VOverlay implements ShortcutActionHandlerOwner,
        ScrollHandler, KeyDownHandler, FocusHandler, BlurHandler, Focusable {

    /**
     * Minimum allowed height of a window. This refers to the content area, not
     * the outer borders.
     */
    private static final int MIN_CONTENT_AREA_HEIGHT = 100;

    /**
     * Minimum allowed width of a window. This refers to the content area, not
     * the outer borders.
     */
    private static final int MIN_CONTENT_AREA_WIDTH = 150;

    private static ArrayList<VWindow> windowOrder = new ArrayList<VWindow>();

    private static boolean orderingDefered;

    public static final String CLASSNAME = "v-window";

    private static final int STACKING_OFFSET_PIXELS = 15;

    public static final int Z_INDEX = 10000;

    ComponentConnector layout;

    Element contents;

    Element header;

    Element footer;

    private Element resizeBox;

    final FocusableScrollPanel contentPanel = new FocusableScrollPanel();

    private boolean dragging;

    private int startX;

    private int startY;

    private int origX;

    private int origY;

    private boolean resizing;

    private int origW;

    private int origH;

    Element closeBox;

    protected ApplicationConnection client;

    String id;

    ShortcutActionHandler shortcutHandler;

    /** Last known positionx read from UIDL or updated to application connection */
    private int uidlPositionX = -1;

    /** Last known positiony read from UIDL or updated to application connection */
    private int uidlPositionY = -1;

    boolean vaadinModality = false;

    boolean resizable = true;

    private boolean draggable = true;

    boolean resizeLazy = false;

    private Element modalityCurtain;
    private Element draggingCurtain;
    private Element resizingCurtain;

    private Element headerText;

    private boolean closable = true;

    // If centered (via UIDL), the window should stay in the centered -mode
    // until a position is received from the server, or the user moves or
    // resizes the window.
    boolean centered = false;

    boolean immediate;

    private Element wrapper;

    boolean visibilityChangesDisabled;

    int bringToFrontSequence = -1;

    private VLazyExecutor delayedContentsSizeUpdater = new VLazyExecutor(200,
            new ScheduledCommand() {

                public void execute() {
                    updateContentsSize();
                }
            });

    public VWindow() {
        super(false, false, true); // no autohide, not modal, shadow
        // Different style of shadow for windows
        setShadowStyle("window");

        constructDOM();
        contentPanel.addScrollHandler(this);
        contentPanel.addKeyDownHandler(this);
        contentPanel.addFocusHandler(this);
        contentPanel.addBlurHandler(this);
    }

    public void bringToFront() {
        int curIndex = windowOrder.indexOf(this);
        if (curIndex + 1 < windowOrder.size()) {
            windowOrder.remove(this);
            windowOrder.add(this);
            for (; curIndex < windowOrder.size(); curIndex++) {
                windowOrder.get(curIndex).setWindowOrder(curIndex);
            }
        }
    }

    /**
     * Returns true if this window is the topmost VWindow
     * 
     * @return
     */
    private boolean isActive() {
        return equals(getTopmostWindow());
    }

    private static VWindow getTopmostWindow() {
        return windowOrder.get(windowOrder.size() - 1);
    }

    void setWindowOrderAndPosition() {
        // This cannot be done in the constructor as the widgets are created in
        // a different order than on they should appear on screen
        if (windowOrder.contains(this)) {
            // Already set
            return;
        }
        final int order = windowOrder.size();
        setWindowOrder(order);
        windowOrder.add(this);
        setPopupPosition(order * STACKING_OFFSET_PIXELS, order
                * STACKING_OFFSET_PIXELS);

    }

    private void setWindowOrder(int order) {
        setZIndex(order + Z_INDEX);
    }

    @Override
    protected void setZIndex(int zIndex) {
        super.setZIndex(zIndex);
        if (vaadinModality) {
            DOM.setStyleAttribute(getModalityCurtain(), "zIndex", "" + zIndex);
        }
    }

    protected Element getModalityCurtain() {
        if (modalityCurtain == null) {
            modalityCurtain = DOM.createDiv();
            modalityCurtain.setClassName(CLASSNAME + "-modalitycurtain");
        }
        return modalityCurtain;
    }

    protected void constructDOM() {
        setStyleName(CLASSNAME);

        header = DOM.createDiv();
        DOM.setElementProperty(header, "className", CLASSNAME + "-outerheader");
        headerText = DOM.createDiv();
        DOM.setElementProperty(headerText, "className", CLASSNAME + "-header");
        contents = DOM.createDiv();
        DOM.setElementProperty(contents, "className", CLASSNAME + "-contents");
        footer = DOM.createDiv();
        DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
        resizeBox = DOM.createDiv();
        DOM.setElementProperty(resizeBox, "className", CLASSNAME + "-resizebox");
        closeBox = DOM.createDiv();
        DOM.setElementProperty(closeBox, "className", CLASSNAME + "-closebox");
        DOM.appendChild(footer, resizeBox);

        wrapper = DOM.createDiv();
        DOM.setElementProperty(wrapper, "className", CLASSNAME + "-wrap");

        DOM.appendChild(wrapper, header);
        DOM.appendChild(wrapper, closeBox);
        DOM.appendChild(header, headerText);
        DOM.appendChild(wrapper, contents);
        DOM.appendChild(wrapper, footer);
        DOM.appendChild(super.getContainerElement(), wrapper);

        sinkEvents(Event.MOUSEEVENTS | Event.TOUCHEVENTS | Event.ONCLICK
                | Event.ONLOSECAPTURE);

        setWidget(contentPanel);

    }

    /**
     * Calling this method will defer ordering algorithm, to order windows based
     * on servers bringToFront and modality instructions. Non changed windows
     * will be left intact.
     */
    static void deferOrdering() {
        if (!orderingDefered) {
            orderingDefered = true;
            Scheduler.get().scheduleFinally(new Command() {
                public void execute() {
                    doServerSideOrdering();
                }
            });
        }
    }

    private static void doServerSideOrdering() {
        orderingDefered = false;
        VWindow[] array = windowOrder.toArray(new VWindow[windowOrder.size()]);
        Arrays.sort(array, new Comparator<VWindow>() {
            public int compare(VWindow o1, VWindow o2) {
                /*
                 * Order by modality, then by bringtofront sequence.
                 */

                if (o1.vaadinModality && !o2.vaadinModality) {
                    return 1;
                } else if (!o1.vaadinModality && o2.vaadinModality) {
                    return -1;
                } else if (o1.bringToFrontSequence > o2.bringToFrontSequence) {
                    return 1;
                } else if (o1.bringToFrontSequence < o2.bringToFrontSequence) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (int i = 0; i < array.length; i++) {
            VWindow w = array[i];
            if (w.bringToFrontSequence != -1 || w.vaadinModality) {
                w.bringToFront();
                w.bringToFrontSequence = -1;
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        /*
         * Visibility with VWindow works differently than with other Paintables
         * in Vaadin. Invisible VWindows are not attached to DOM at all. Flag is
         * used to avoid visibility call from
         * ApplicationConnection.updateComponent();
         */
        if (!visibilityChangesDisabled) {
            super.setVisible(visible);
        }
    }

    void setDraggable(boolean draggable) {
        if (this.draggable == draggable) {
            return;
        }

        this.draggable = draggable;

        setCursorProperties();
    }

    private void setCursorProperties() {
        if (!draggable) {
            header.getStyle().setProperty("cursor", "default");
            footer.getStyle().setProperty("cursor", "default");
        } else {
            header.getStyle().setProperty("cursor", "");
            footer.getStyle().setProperty("cursor", "");
        }
    }

    /**
     * Sets the closable state of the window. Additionally hides/shows the close
     * button according to the new state.
     * 
     * @param closable
     *            true if the window can be closed by the user
     */
    protected void setClosable(boolean closable) {
        if (this.closable == closable) {
            return;
        }

        this.closable = closable;
        if (closable) {
            DOM.setStyleAttribute(closeBox, "display", "");
        } else {
            DOM.setStyleAttribute(closeBox, "display", "none");
        }

    }

    /**
     * Returns the closable state of the sub window. If the sub window is
     * closable a decoration (typically an X) is shown to the user. By clicking
     * on the X the user can close the window.
     * 
     * @return true if the sub window is closable
     */
    protected boolean isClosable() {
        return closable;
    }

    @Override
    public void show() {
        if (!windowOrder.contains(this)) {
            windowOrder.add(this);
        }

        if (vaadinModality) {
            showModalityCurtain();
        }
        super.show();
    }

    @Override
    public void hide() {
        if (vaadinModality) {
            hideModalityCurtain();
        }
        super.hide();

        windowOrder.remove(this);
    }

    void setVaadinModality(boolean modality) {
        vaadinModality = modality;
        if (vaadinModality) {
            if (isAttached()) {
                showModalityCurtain();
            }
            deferOrdering();
        } else {
            if (modalityCurtain != null) {
                if (isAttached()) {
                    hideModalityCurtain();
                }
                modalityCurtain = null;
            }
        }
    }

    private void showModalityCurtain() {
        DOM.setStyleAttribute(getModalityCurtain(), "zIndex",
                "" + (windowOrder.indexOf(this) + Z_INDEX));
        if (isShowing()) {
            RootPanel.getBodyElement().insertBefore(getModalityCurtain(),
                    getElement());
        } else {
            DOM.appendChild(RootPanel.getBodyElement(), getModalityCurtain());
        }
    }

    private void hideModalityCurtain() {
        DOM.removeChild(RootPanel.getBodyElement(), modalityCurtain);
    }

    /*
     * Shows an empty div on top of all other content; used when moving, so that
     * iframes (etc) do not steal event.
     */
    private void showDraggingCurtain() {
        DOM.appendChild(RootPanel.getBodyElement(), getDraggingCurtain());
    }

    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            DOM.removeChild(RootPanel.getBodyElement(), draggingCurtain);
        }
    }

    /*
     * Shows an empty div on top of all other content; used when resizing, so
     * that iframes (etc) do not steal event.
     */
    private void showResizingCurtain() {
        DOM.appendChild(RootPanel.getBodyElement(), getResizingCurtain());
    }

    private void hideResizingCurtain() {
        if (resizingCurtain != null) {
            DOM.removeChild(RootPanel.getBodyElement(), resizingCurtain);
        }
    }

    private Element getDraggingCurtain() {
        if (draggingCurtain == null) {
            draggingCurtain = createCurtain();
            draggingCurtain.setClassName(CLASSNAME + "-draggingCurtain");
        }

        return draggingCurtain;
    }

    private Element getResizingCurtain() {
        if (resizingCurtain == null) {
            resizingCurtain = createCurtain();
            resizingCurtain.setClassName(CLASSNAME + "-resizingCurtain");
        }

        return resizingCurtain;
    }

    private Element createCurtain() {
        Element curtain = DOM.createDiv();

        DOM.setStyleAttribute(curtain, "position", "absolute");
        DOM.setStyleAttribute(curtain, "top", "0px");
        DOM.setStyleAttribute(curtain, "left", "0px");
        DOM.setStyleAttribute(curtain, "width", "100%");
        DOM.setStyleAttribute(curtain, "height", "100%");
        DOM.setStyleAttribute(curtain, "zIndex", "" + VOverlay.Z_INDEX);

        return curtain;
    }

    void setResizable(boolean resizability) {
        resizable = resizability;
        if (resizability) {
            DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
            DOM.setElementProperty(resizeBox, "className", CLASSNAME
                    + "-resizebox");
        } else {
            DOM.setElementProperty(footer, "className", CLASSNAME + "-footer "
                    + CLASSNAME + "-footer-noresize");
            DOM.setElementProperty(resizeBox, "className", CLASSNAME
                    + "-resizebox " + CLASSNAME + "-resizebox-disabled");
        }
    }

    @Override
    public void setPopupPosition(int left, int top) {
        if (top < 0) {
            // ensure window is not moved out of browser window from top of the
            // screen
            top = 0;
        }
        super.setPopupPosition(left, top);
        if (left != uidlPositionX && client != null) {
            client.updateVariable(id, "positionx", left, false);
            uidlPositionX = left;
        }
        if (top != uidlPositionY && client != null) {
            client.updateVariable(id, "positiony", top, false);
            uidlPositionY = top;
        }
    }

    public void setCaption(String c) {
        setCaption(c, null);
    }

    public void setCaption(String c, String icon) {
        String html = Util.escapeHTML(c);
        if (icon != null) {
            icon = client.translateVaadinUri(icon);
            html = "<img src=\"" + Util.escapeAttribute(icon)
                    + "\" class=\"v-icon\" />" + html;
        }
        DOM.setInnerHTML(headerText, html);
    }

    @Override
    protected Element getContainerElement() {
        // in GWT 1.5 this method is used in PopupPanel constructor
        if (contents == null) {
            return super.getContainerElement();
        }
        return contents;
    }

    @Override
    public void onBrowserEvent(final Event event) {
        boolean bubble = true;

        final int type = event.getTypeInt();

        final Element target = DOM.eventGetTarget(event);

        if (client != null && header.isOrHasChild(target)) {
            // Handle window caption tooltips
            client.handleTooltipEvent(event, this);
        }

        if (resizing || resizeBox == target) {
            onResizeEvent(event);
            bubble = false;
        } else if (isClosable() && target == closeBox) {
            if (type == Event.ONCLICK) {
                onCloseClick();
            }
            bubble = false;
        } else if (dragging || !contents.isOrHasChild(target)) {
            onDragEvent(event);
            bubble = false;
        } else if (type == Event.ONCLICK) {
            // clicked inside window, ensure to be on top
            if (!isActive()) {
                bringToFront();
            }
        }

        /*
         * If clicking on other than the content, move focus to the window.
         * After that this windows e.g. gets all keyboard shortcuts.
         */
        if (type == Event.ONMOUSEDOWN
                && !contentPanel.getElement().isOrHasChild(target)
                && target != closeBox) {
            contentPanel.focus();
        }

        if (!bubble) {
            event.stopPropagation();
        } else {
            // Super.onBrowserEvent takes care of Handlers added by the
            // ClickEventHandler
            super.onBrowserEvent(event);
        }
    }

    private void onCloseClick() {
        client.updateVariable(id, "close", true, true);
    }

    private void onResizeEvent(Event event) {
        if (resizable && Util.isTouchEventOrLeftMouseButton(event)) {
            switch (event.getTypeInt()) {
            case Event.ONMOUSEDOWN:
            case Event.ONTOUCHSTART:
                if (!isActive()) {
                    bringToFront();
                }
                showResizingCurtain();
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(resizeBox, "visibility", "hidden");
                }
                resizing = true;
                startX = Util.getTouchOrMouseClientX(event);
                startY = Util.getTouchOrMouseClientY(event);
                origW = getElement().getOffsetWidth();
                origH = getElement().getOffsetHeight();
                DOM.setCapture(getElement());
                event.preventDefault();
                break;
            case Event.ONMOUSEUP:
            case Event.ONTOUCHEND:
                setSize(event, true);
            case Event.ONTOUCHCANCEL:
                DOM.releaseCapture(getElement());
            case Event.ONLOSECAPTURE:
                hideResizingCurtain();
                if (BrowserInfo.get().isIE()) {
                    DOM.setStyleAttribute(resizeBox, "visibility", "");
                }
                resizing = false;
                break;
            case Event.ONMOUSEMOVE:
            case Event.ONTOUCHMOVE:
                if (resizing) {
                    centered = false;
                    setSize(event, false);
                    event.preventDefault();
                }
                break;
            default:
                event.preventDefault();
                break;
            }
        }
    }

    /**
     * TODO check if we need to support this with touch based devices.
     * 
     * Checks if the cursor was inside the browser content area when the event
     * happened.
     * 
     * @param event
     *            The event to be checked
     * @return true, if the cursor is inside the browser content area
     * 
     *         false, otherwise
     */
    private boolean cursorInsideBrowserContentArea(Event event) {
        if (event.getClientX() < 0 || event.getClientY() < 0) {
            // Outside to the left or above
            return false;
        }

        if (event.getClientX() > Window.getClientWidth()
                || event.getClientY() > Window.getClientHeight()) {
            // Outside to the right or below
            return false;
        }

        return true;
    }

    private void setSize(Event event, boolean updateVariables) {
        if (!cursorInsideBrowserContentArea(event)) {
            // Only drag while cursor is inside the browser client area
            return;
        }

        int w = Util.getTouchOrMouseClientX(event) - startX + origW;
        int minWidth = getMinWidth();
        if (w < minWidth) {
            w = minWidth;
        }

        int h = Util.getTouchOrMouseClientY(event) - startY + origH;
        int minHeight = getMinHeight();
        if (h < minHeight) {
            h = minHeight;
        }

        setWidth(w + "px");
        setHeight(h + "px");

        if (updateVariables) {
            // sending width back always as pixels, no need for unit
            client.updateVariable(id, "width", w, false);
            client.updateVariable(id, "height", h, immediate);
        }

        if (updateVariables || !resizeLazy) {
            // Resize has finished or is not lazy
            updateContentsSize();
        } else {
            // Lazy resize - wait for a while before re-rendering contents
            delayedContentsSizeUpdater.trigger();
        }
    }

    private void updateContentsSize() {
        // Update child widget dimensions
        if (client != null) {
            client.handleComponentRelativeSize(layout.getWidget());
            client.runDescendentsLayout((HasWidgets) layout.getWidget());
        }

        Util.runWebkitOverflowAutoFix(contentPanel.getElement());
        client.doLayout(false);
    }

    @Override
    public void setWidth(String width) {
        // Override PopupPanel which sets the width to the contents
        getElement().getStyle().setProperty("width", width);
        // Update v-has-width in case undefined window is resized
        setStyleName("v-has-width", width != null && width.length() > 0);
    }

    @Override
    public void setHeight(String height) {
        // Override PopupPanel which sets the height to the contents
        getElement().getStyle().setProperty("height", height);
        // Update v-has-height in case undefined window is resized
        setStyleName("v-has-height", height != null && height.length() > 0);
    }

    private void onDragEvent(Event event) {
        if (!Util.isTouchEventOrLeftMouseButton(event)) {
            return;
        }

        switch (DOM.eventGetType(event)) {
        case Event.ONTOUCHSTART:
            if (event.getTouches().length() > 1) {
                return;
            }
        case Event.ONMOUSEDOWN:
            if (!isActive()) {
                bringToFront();
            }
            beginMovingWindow(event);
            break;
        case Event.ONMOUSEUP:
        case Event.ONTOUCHEND:
        case Event.ONTOUCHCANCEL:
        case Event.ONLOSECAPTURE:
            stopMovingWindow();
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            moveWindow(event);
            break;
        default:
            break;
        }
    }

    private void moveWindow(Event event) {
        if (dragging) {
            centered = false;
            if (cursorInsideBrowserContentArea(event)) {
                // Only drag while cursor is inside the browser client area
                final int x = Util.getTouchOrMouseClientX(event) - startX
                        + origX;
                final int y = Util.getTouchOrMouseClientY(event) - startY
                        + origY;
                setPopupPosition(x, y);
            }
            DOM.eventPreventDefault(event);
        }
    }

    private void beginMovingWindow(Event event) {
        if (draggable) {
            showDraggingCurtain();
            dragging = true;
            startX = Util.getTouchOrMouseClientX(event);
            startY = Util.getTouchOrMouseClientY(event);
            origX = DOM.getAbsoluteLeft(getElement());
            origY = DOM.getAbsoluteTop(getElement());
            DOM.setCapture(getElement());
            DOM.eventPreventDefault(event);
        }
    }

    private void stopMovingWindow() {
        dragging = false;
        hideDraggingCurtain();
        DOM.releaseCapture(getElement());
    }

    @Override
    public boolean onEventPreview(Event event) {
        if (dragging) {
            onDragEvent(event);
            return false;
        } else if (resizing) {
            onResizeEvent(event);
            return false;
        }

        // TODO This is probably completely unnecessary as the modality curtain
        // prevents events from reaching other windows and any security check
        // must be done on the server side and not here.
        // The code here is also run many times as each VWindow has an event
        // preview but we cannot check only the current VWindow here (e.g.
        // if(isTopMost) {...}) because PopupPanel will cause all events that
        // are not cancelled here and target this window to be consume():d
        // meaning the event won't be sent to the rest of the preview handlers.

        if (getTopmostWindow().vaadinModality) {
            // Topmost window is modal. Cancel the event if it targets something
            // outside that window (except debug console...)
            if (DOM.getCaptureElement() != null) {
                // Allow events when capture is set
                return true;
            }

            final Element target = event.getEventTarget().cast();
            if (!DOM.isOrHasChild(getTopmostWindow().getElement(), target)) {
                // not within the modal window, but let's see if it's in the
                // debug window
                Widget w = Util.findWidget(target, null);
                while (w != null) {
                    if (w instanceof Console) {
                        return true; // allow debug-window clicks
                    } else if (ConnectorMap.get(client).isConnector(w)) {
                        return false;
                    }
                    w = w.getParent();
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void addStyleDependentName(String styleSuffix) {
        // VWindow's getStyleElement() does not return the same element as
        // getElement(), so we need to override this.
        setStyleName(getElement(), getStylePrimaryName() + "-" + styleSuffix,
                true);
    }

    public ShortcutActionHandler getShortcutActionHandler() {
        return shortcutHandler;
    }

    public void onScroll(ScrollEvent event) {
        client.updateVariable(id, "scrollTop",
                contentPanel.getScrollPosition(), false);
        client.updateVariable(id, "scrollLeft",
                contentPanel.getHorizontalScrollPosition(), false);

    }

    public void onKeyDown(KeyDownEvent event) {
        if (shortcutHandler != null) {
            shortcutHandler
                    .handleKeyboardEvent(Event.as(event.getNativeEvent()));
            return;
        }
    }

    public void onBlur(BlurEvent event) {
        if (client.hasEventListeners(this, EventId.BLUR)) {
            client.updateVariable(id, EventId.BLUR, "", true);
        }
    }

    public void onFocus(FocusEvent event) {
        if (client.hasEventListeners(this, EventId.FOCUS)) {
            client.updateVariable(id, EventId.FOCUS, "", true);
        }
    }

    public void focus() {
        contentPanel.focus();
    }

    public int getMinHeight() {
        return MIN_CONTENT_AREA_HEIGHT + getDecorationHeight();
    }

    private int getDecorationHeight() {
        LayoutManager layoutManager = layout.getLayoutManager();
        return layoutManager.getOuterHeight(getElement())
                - layoutManager.getInnerHeight(contentPanel.getElement());
    }

    public int getMinWidth() {
        return MIN_CONTENT_AREA_WIDTH + getDecorationWidth();
    }

    private int getDecorationWidth() {
        LayoutManager layoutManager = layout.getLayoutManager();
        return layoutManager.getOuterWidth(getElement())
                - layoutManager.getInnerWidth(contentPanel.getElement());
    }

}
