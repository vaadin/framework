/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Console;
import com.vaadin.terminal.gwt.client.Container;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.RenderSpace;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler.ShortcutActionHandlerOwner;

/**
 * "Sub window" component.
 * 
 * @author Vaadin Ltd
 */
public class VWindow extends VOverlay implements Container,
        ShortcutActionHandlerOwner, ScrollHandler, KeyDownHandler,
        FocusHandler, BlurHandler, BeforeShortcutActionListener, Focusable {

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

    /**
     * Difference between offsetWidth and inner width for the content area.
     */
    private int contentAreaBorderPadding = -1;
    /**
     * Pixels used by inner borders and paddings horizontally (calculated only
     * once). This is the difference between the width of the root element and
     * the content area, such that if root element width is set to "XYZpx" the
     * inner width (width-border-padding) of the content area is
     * X-contentAreaRootDifference.
     */
    private int contentAreaToRootDifference = -1;

    private static final int STACKING_OFFSET_PIXELS = 15;

    public static final int Z_INDEX = 10000;

    private Paintable layout;

    private Element contents;

    private Element header;

    private Element footer;

    private Element resizeBox;

    private final FocusableScrollPanel contentPanel = new FocusableScrollPanel();

    private boolean dragging;

    private int startX;

    private int startY;

    private int origX;

    private int origY;

    private boolean resizing;

    private int origW;

    private int origH;

    private Element closeBox;

    protected ApplicationConnection client;

    private String id;

    ShortcutActionHandler shortcutHandler;

    /** Last known positionx read from UIDL or updated to application connection */
    private int uidlPositionX = -1;

    /** Last known positiony read from UIDL or updated to application connection */
    private int uidlPositionY = -1;

    private boolean vaadinModality = false;

    private boolean resizable = true;

    private boolean draggable = true;

    private boolean resizeLazy = false;

    private Element modalityCurtain;
    private Element draggingCurtain;
    private Element resizingCurtain;

    private Element headerText;

    private boolean closable = true;

    boolean dynamicWidth = false;
    boolean dynamicHeight = false;
    boolean layoutRelativeWidth = false;
    boolean layoutRelativeHeight = false;

    // If centered (via UIDL), the window should stay in the centered -mode
    // until a position is received from the server, or the user moves or
    // resizes the window.
    boolean centered = false;

    private RenderSpace renderSpace = new RenderSpace(MIN_CONTENT_AREA_WIDTH,
            MIN_CONTENT_AREA_HEIGHT, true);

    private String width;

    private String height;

    private boolean immediate;

    private Element wrapper, wrapper2;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            VPanel.CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return addDomHandler(handler, type);
        }
    };

    private boolean visibilityChangesDisabled;

    private int bringToFrontSequence = -1;

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

        final int order = windowOrder.size();
        setWindowOrder(order);
        windowOrder.add(this);
        constructDOM();
        setPopupPosition(order * STACKING_OFFSET_PIXELS, order
                * STACKING_OFFSET_PIXELS);
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
        return windowOrder.get(windowOrder.size() - 1).equals(this);
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

        wrapper2 = DOM.createDiv();
        DOM.setElementProperty(wrapper2, "className", CLASSNAME + "-wrap2");

        DOM.appendChild(wrapper2, closeBox);
        DOM.appendChild(wrapper2, header);
        DOM.appendChild(header, headerText);
        DOM.appendChild(wrapper2, contents);
        DOM.appendChild(wrapper2, footer);
        DOM.appendChild(wrapper, wrapper2);
        DOM.appendChild(super.getContainerElement(), wrapper);

        sinkEvents(Event.MOUSEEVENTS | Event.TOUCHEVENTS | Event.ONCLICK
                | Event.ONLOSECAPTURE);

        setWidget(contentPanel);

    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        id = uidl.getId();
        this.client = client;

        // Workaround needed for Testing Tools (GWT generates window DOM
        // slightly different in different browsers).
        DOM.setElementProperty(closeBox, "id", id + "_window_close");

        if (uidl.hasAttribute("invisible")) {
            hide();
            return;
        }

        if (!uidl.hasAttribute("cached")) {
            if (uidl.getBooleanAttribute("modal") != vaadinModality) {
                setVaadinModality(!vaadinModality);
            }
            if (!isAttached()) {
                setVisible(false); // hide until possible centering
                show();
            }
            if (uidl.getBooleanAttribute("resizable") != resizable) {
                setResizable(!resizable);
            }
            resizeLazy = uidl.hasAttribute(VView.RESIZE_LAZY);

            setDraggable(!uidl.hasAttribute("fixedposition"));

            // Caption must be set before required header size is measured. If
            // the caption attribute is missing the caption should be cleared.
            setCaption(uidl.getStringAttribute("caption"),
                    uidl.getStringAttribute("icon"));
        }

        visibilityChangesDisabled = true;
        if (client.updateComponent(this, uidl, false)) {
            return;
        }
        visibilityChangesDisabled = false;

        clickEventHandler.handleEventHandlerRegistration(client);

        immediate = uidl.hasAttribute("immediate");

        setClosable(!uidl.getBooleanAttribute("readonly"));

        // Initialize the position form UIDL
        int positionx = uidl.getIntVariable("positionx");
        int positiony = uidl.getIntVariable("positiony");
        if (positionx >= 0 || positiony >= 0) {
            if (positionx < 0) {
                positionx = 0;
            }
            if (positiony < 0) {
                positiony = 0;
            }
            setPopupPosition(positionx, positiony);
        }

        boolean showingUrl = false;
        int childIndex = 0;
        UIDL childUidl = uidl.getChildUIDL(childIndex++);
        while ("open".equals(childUidl.getTag())) {
            // TODO multiple opens with the same target will in practice just
            // open the last one - should we fix that somehow?
            final String parsedUri = client.translateVaadinUri(childUidl
                    .getStringAttribute("src"));
            if (!childUidl.hasAttribute("name")) {
                final Frame frame = new Frame();
                DOM.setStyleAttribute(frame.getElement(), "width", "100%");
                DOM.setStyleAttribute(frame.getElement(), "height", "100%");
                DOM.setStyleAttribute(frame.getElement(), "border", "0px");
                frame.setUrl(parsedUri);
                contentPanel.setWidget(frame);
                showingUrl = true;
            } else {
                final String target = childUidl.getStringAttribute("name");
                Window.open(parsedUri, target, "");
            }
            childUidl = uidl.getChildUIDL(childIndex++);
        }

        final Paintable lo = client.getPaintable(childUidl);
        if (layout != null) {
            if (layout != lo) {
                // remove old
                client.unregisterPaintable(layout);
                contentPanel.remove((Widget) layout);
                // add new
                if (!showingUrl) {
                    contentPanel.setWidget((Widget) lo);
                }
                layout = lo;
            }
        } else if (!showingUrl) {
            contentPanel.setWidget((Widget) lo);
            layout = lo;
        }

        dynamicWidth = !uidl.hasAttribute("width");
        dynamicHeight = !uidl.hasAttribute("height");

        layoutRelativeWidth = uidl.hasAttribute("layoutRelativeWidth");
        layoutRelativeHeight = uidl.hasAttribute("layoutRelativeHeight");

        if (dynamicWidth && layoutRelativeWidth) {
            /*
             * Relative layout width, fix window width before rendering (width
             * according to caption)
             */
            setNaturalWidth();
        }

        layout.updateFromUIDL(childUidl, client);
        if (!dynamicHeight && layoutRelativeWidth) {
            /*
             * Relative layout width, and fixed height. Must update the size to
             * be able to take scrollbars into account (layout gets narrower
             * space if it is higher than the window) -> only vertical scrollbar
             */
            client.runDescendentsLayout(this);
        }

        /*
         * No explicit width is set and the layout does not have relative width
         * so fix the size according to the layout.
         */
        if (dynamicWidth && !layoutRelativeWidth) {
            setNaturalWidth();
        }

        if (dynamicHeight && layoutRelativeHeight) {
            // Prevent resizing until height has been fixed
            resizable = false;
        }

        // we may have actions and notifications
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (shortcutHandler == null) {
                        shortcutHandler = new ShortcutActionHandler(id, client);
                    }
                    shortcutHandler.updateActionMap(childUidl);
                } else if (childUidl.getTag().equals("notifications")) {
                    for (final Iterator<?> it = childUidl.getChildIterator(); it
                            .hasNext();) {
                        final UIDL notification = (UIDL) it.next();
                        VNotification.showNotification(client, notification);
                    }
                }
            }

        }

        // setting scrollposition must happen after children is rendered
        contentPanel.setScrollPosition(uidl.getIntVariable("scrollTop"));
        contentPanel.setHorizontalScrollPosition(uidl
                .getIntVariable("scrollLeft"));

        // Center this window on screen if requested
        // This has to be here because we might not know the content size before
        // everything is painted into the window
        if (uidl.getBooleanAttribute("center")) {
            // mark as centered - this is unset on move/resize
            centered = true;
            center();
        } else {
            // don't try to center the window anymore
            centered = false;
        }
        updateShadowSizeAndPosition();
        setVisible(true);

        boolean sizeReduced = false;
        // ensure window is not larger than browser window
        if (getOffsetWidth() > Window.getClientWidth()) {
            setWidth(Window.getClientWidth() + "px");
            sizeReduced = true;
        }
        if (getOffsetHeight() > Window.getClientHeight()) {
            setHeight(Window.getClientHeight() + "px");
            sizeReduced = true;
        }

        if (dynamicHeight && layoutRelativeHeight) {
            /*
             * Window height is undefined, layout is 100% high so the layout
             * should define the initial window height but on resize the layout
             * should be as high as the window. We fix the height to deal with
             * this.
             */

            int h = contents.getOffsetHeight() + getExtraHeight();
            int w = getElement().getOffsetWidth();

            client.updateVariable(id, "height", h, false);
            client.updateVariable(id, "width", w, true);
        }

        if (sizeReduced) {
            // If we changed the size we need to update the size of the child
            // component if it is relative (#3407)
            client.runDescendentsLayout(this);
        }

        Util.runWebkitOverflowAutoFix(contentPanel.getElement());

        client.getView().scrollIntoView(uidl);

        if (uidl.hasAttribute("bringToFront")) {
            /*
             * Focus as a side-efect. Will be overridden by
             * ApplicationConnection if another component was focused by the
             * server side.
             */
            contentPanel.focus();
            bringToFrontSequence = uidl.getIntAttribute("bringToFront");
            deferOrdering();
        }
    }

    /**
     * Calling this method will defer ordering algorithm, to order windows based
     * on servers bringToFront and modality instructions. Non changed windows
     * will be left intact.
     */
    private static void deferOrdering() {
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

    private void setDraggable(boolean draggable) {
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

    private void setNaturalWidth() {
        /*
         * Use max(layout width, window width) i.e layout content width or
         * caption width. We remove the previous set width so the width is
         * allowed to shrink. All widths are measured as outer sizes, i.e. the
         * borderWidth is added to the content.
         */

        DOM.setStyleAttribute(getElement(), "width", "");

        String oldHeaderWidth = ""; // Only for IE6
        if (BrowserInfo.get().isIE6()) {
            /*
             * For some reason IE6 has title DIV set to width 100% which
             * interferes with the header measuring. Also IE6 has width set to
             * the contentPanel.
             */
            oldHeaderWidth = headerText.getStyle().getProperty("width");
            DOM.setStyleAttribute(contentPanel.getElement(), "width", "auto");
            DOM.setStyleAttribute(contentPanel.getElement(), "zoom", "1");
            headerText.getStyle().setProperty("width", "auto");
        }

        // Content
        int contentWidth = contentPanel.getElement().getScrollWidth();
        contentWidth += getContentAreaToRootDifference();

        // Window width (caption)
        int windowCaptionWidth = getOffsetWidth();

        int naturalWidth = (contentWidth > windowCaptionWidth ? contentWidth
                : windowCaptionWidth);

        if (BrowserInfo.get().isIE6()) {
            headerText.getStyle().setProperty("width", oldHeaderWidth);
        }

        setWidth(naturalWidth + "px");
    }

    private int getContentAreaToRootDifference() {
        if (contentAreaToRootDifference < 0) {
            measure();
        }
        return contentAreaToRootDifference;
    }

    private void measure() {
        if (!isAttached()) {
            return;
        }

        contentAreaBorderPadding = Util.measureHorizontalPaddingAndBorder(
                contents, 4);
        int wrapperPaddingBorder = Util.measureHorizontalPaddingAndBorder(
                wrapper, 0)
                + Util.measureHorizontalPaddingAndBorder(wrapper2, 0);

        contentAreaToRootDifference = wrapperPaddingBorder
                + contentAreaBorderPadding;

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
            // This is needed if the window is hidden and then shown again.
            // Otherwise this VWindow is added to windowOrder in the
            // constructor.
            windowOrder.add(this);
        }

        if (vaadinModality) {
            showModalityCurtain();
        }
        super.show();

        setFF2CaretFixEnabled(true);
        fixFF3OverflowBug();
    }

    /** Disable overflow auto with FF3 to fix #1837. */
    private void fixFF3OverflowBug() {
        if (BrowserInfo.get().isFF3()) {
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    DOM.setStyleAttribute(getElement(), "overflow", "");
                }
            });
        }
    }

    /**
     * Fix "missing cursor" browser bug workaround for FF2 in Windows and Linux.
     * 
     * Calling this method has no effect on other browsers than the ones based
     * on Gecko 1.8
     * 
     * @param enable
     */
    private void setFF2CaretFixEnabled(boolean enable) {
        if (BrowserInfo.get().isFF2()) {
            if (enable) {
                Scheduler.get().scheduleDeferred(new Command() {
                    public void execute() {
                        DOM.setStyleAttribute(getElement(), "overflow", "auto");
                    }
                });
            } else {
                DOM.setStyleAttribute(getElement(), "overflow", "");
            }
        }
    }

    @Override
    public void hide() {
        if (vaadinModality) {
            hideModalityCurtain();
        }
        super.hide();

        // Remove window from windowOrder to avoid references being left
        // hanging.
        windowOrder.remove(this);
    }

    private void setVaadinModality(boolean modality) {
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
        if (BrowserInfo.get().isFF2()) {
            DOM.setStyleAttribute(
                    getModalityCurtain(),
                    "height",
                    DOM.getElementPropertyInt(RootPanel.getBodyElement(),
                            "offsetHeight") + "px");
            DOM.setStyleAttribute(getModalityCurtain(), "position", "absolute");
        }
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
        setFF2CaretFixEnabled(false); // makes FF2 slow

        DOM.appendChild(RootPanel.getBodyElement(), getDraggingCurtain());
    }

    private void hideDraggingCurtain() {
        if (draggingCurtain != null) {
            setFF2CaretFixEnabled(true); // makes FF2 slow

            DOM.removeChild(RootPanel.getBodyElement(), draggingCurtain);
        }
    }

    /*
     * Shows an empty div on top of all other content; used when resizing, so
     * that iframes (etc) do not steal event.
     */
    private void showResizingCurtain() {
        setFF2CaretFixEnabled(false); // makes FF2 slow

        DOM.appendChild(RootPanel.getBodyElement(), getResizingCurtain());
    }

    private void hideResizingCurtain() {
        if (resizingCurtain != null) {
            setFF2CaretFixEnabled(true); // makes FF2 slow

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

    private void setResizable(boolean resizability) {
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
        if (w < MIN_CONTENT_AREA_WIDTH + getContentAreaToRootDifference()) {
            w = MIN_CONTENT_AREA_WIDTH + getContentAreaToRootDifference();
        }

        int h = Util.getTouchOrMouseClientY(event) - startY + origH;
        if (h < MIN_CONTENT_AREA_HEIGHT + getExtraHeight()) {
            h = MIN_CONTENT_AREA_HEIGHT + getExtraHeight();
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
            client.handleComponentRelativeSize((Widget) layout);
            client.runDescendentsLayout((HasWidgets) layout);
        }

        Util.runWebkitOverflowAutoFix(contentPanel.getElement());
    }

    @Override
    /**
     * Width is set to the out-most element (v-window).
     * 
     * This function should never be called with percentage values (it will
     * throw an exception)
     */
    public void setWidth(String width) {
        this.width = width;
        if (!isAttached()) {
            return;
        }
        if (width != null && !"".equals(width)) {
            int rootPixelWidth = -1;
            if (width.indexOf("px") < 0) {
                /*
                 * Convert non-pixel values to pixels by setting the width and
                 * then measuring it. Updates the "width" variable with the
                 * pixel width.
                 */
                DOM.setStyleAttribute(getElement(), "width", width);
                rootPixelWidth = getElement().getOffsetWidth();
                width = rootPixelWidth + "px";
            } else {
                rootPixelWidth = Integer.parseInt(width.substring(0,
                        width.indexOf("px")));
            }

            // "width" now contains the new width in pixels

            if (BrowserInfo.get().isIE6()) {
                getElement().getStyle().setProperty("overflow", "hidden");
            }

            // Apply the new pixel width
            getElement().getStyle().setProperty("width", width);

            // Caculate the inner width of the content area
            int contentAreaInnerWidth = rootPixelWidth
                    - getContentAreaToRootDifference();
            if (contentAreaInnerWidth < MIN_CONTENT_AREA_WIDTH) {
                contentAreaInnerWidth = MIN_CONTENT_AREA_WIDTH;
                int rootWidth = contentAreaInnerWidth
                        + getContentAreaToRootDifference();
                DOM.setStyleAttribute(getElement(), "width", rootWidth + "px");
            }

            // IE6 needs the actual inner content width on the content element,
            // otherwise it won't wrap the content properly (no scrollbars
            // appear, content flows out of window)
            if (BrowserInfo.get().isIE6()) {
                DOM.setStyleAttribute(contentPanel.getElement(), "width",
                        contentAreaInnerWidth + "px");
            }

            renderSpace.setWidth(contentAreaInnerWidth);

            updateShadowSizeAndPosition();
        }
    }

    @Override
    /**
     * Height is set to the out-most element (v-window).
     * 
     * This function should never be called with percentage values (it will
     * throw an exception)
     * 
     * @param height A CSS string specifying the new height of the window.
     *               An empty string or null clears the height and lets
     *               the browser to compute it based on the window contents. 
     */
    public void setHeight(String height) {
        if (!isAttached()
                || (height == null ? this.height == null : height
                        .equals(this.height))) {
            return;
        }
        if (height == null || "".equals(height)) {
            getElement().getStyle().clearHeight();
            contentPanel.getElement().getStyle().clearHeight();
            // Reset to default, the exact value does not actually
            // matter as an undefined-height parent should not have
            // a relative-height child anyway.
            renderSpace.setHeight(MIN_CONTENT_AREA_HEIGHT);
        } else {
            getElement().getStyle().setProperty("height", height);
            int contentHeight = getElement().getOffsetHeight()
                    - getExtraHeight();
            if (contentHeight < MIN_CONTENT_AREA_HEIGHT) {
                contentHeight = MIN_CONTENT_AREA_HEIGHT;
                int rootHeight = contentHeight + getExtraHeight();
                getElement().getStyle()
                        .setProperty("height", rootHeight + "px");
            }
            renderSpace.setHeight(contentHeight);
            contentPanel.getElement().getStyle()
                    .setProperty("height", contentHeight + "px");
        }
        this.height = height;
        updateShadowSizeAndPosition();
    }

    private int extraH = 0;

    private int getExtraHeight() {
        extraH = header.getOffsetHeight() + footer.getOffsetHeight();
        return extraH;
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
        } else if (vaadinModality) {
            // return false when modal and outside window
            final Element target = event.getEventTarget().cast();
            if (DOM.getCaptureElement() != null) {
                // Allow events when capture is set
                return true;
            }

            if (!DOM.isOrHasChild(getElement(), target)) {
                // not within the modal window, but let's see if it's in the
                // debug window
                Widget w = Util.findWidget(target, null);
                while (w != null) {
                    if (w instanceof Console) {
                        return true; // allow debug-window clicks
                    } else if (w instanceof Paintable) {
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

    @Override
    protected void onAttach() {
        super.onAttach();

        setWidth(width);
        setHeight(height);
    }

    public RenderSpace getAllocatedSpace(Widget child) {
        if (child == layout) {
            return renderSpace;
        } else {
            // Exception ??
            return null;
        }
    }

    public boolean hasChildComponent(Widget component) {
        if (component == layout) {
            return true;
        } else {
            return false;
        }
    }

    public void replaceChildComponent(Widget oldComponent, Widget newComponent) {
        contentPanel.setWidget(newComponent);
    }

    public boolean requestLayout(Set<Paintable> child) {
        if (dynamicWidth && !layoutRelativeWidth) {
            setNaturalWidth();
        }
        if (centered) {
            center();
        }
        updateShadowSizeAndPosition();
        // layout size change may affect its available space (scrollbars)
        client.handleComponentRelativeSize((Widget) layout);
        return true;
    }

    public void updateCaption(Paintable component, UIDL uidl) {
        // NOP, window has own caption, layout captio not rendered
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

    public void onBeforeShortcutAction(Event e) {
        // NOP, nothing to update just avoid workaround ( causes excess
        // blur/focus )
    }

    public void focus() {
        contentPanel.focus();
    }

}
