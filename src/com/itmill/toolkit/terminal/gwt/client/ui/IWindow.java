/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * "Sub window" component.
 * 
 * TODO update position / scrollposition / size to client
 * 
 * @author IT Mill Ltd
 */
public class IWindow extends PopupPanel implements Paintable, ScrollListener {

    private static final int DEFAULT_HEIGHT = 300;

    private static final int DEFAULT_WIDTH = 400;

    private static final int MIN_HEIGHT = 60;

    private static final int MIN_WIDTH = 80;

    private static Vector windowOrder = new Vector();

    public static final String CLASSNAME = "i-window";

    /** pixels used by inner borders and paddings horizontally */
    protected static final int BORDER_WIDTH_HORIZONTAL = 41;

    /** pixels used by headers, footers, inner borders and paddings vertically */
    protected static final int BORDER_WIDTH_VERTICAL = 58;

    private static final int STACKING_OFFSET_PIXELS = 15;

    private static final int Z_INDEX_BASE = 10000;

    private Paintable layout;

    private Element contents;

    private Element header;

    private Element footer;

    private Element resizeBox;

    private final ScrollPanel contentPanel = new ScrollPanel();

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

    private boolean modal = false;

    private Element headerText;

    public IWindow() {
        super();
        final int order = windowOrder.size();
        setWindowOrder(order);
        windowOrder.add(this);
        setStyleName(CLASSNAME);
        constructDOM();
        setPopupPosition(order * STACKING_OFFSET_PIXELS, order
                * STACKING_OFFSET_PIXELS);
        contentPanel.addScrollListener(this);
    }

    private void bringToFront() {
        int curIndex = windowOrder.indexOf(this);
        if (curIndex + 1 < windowOrder.size()) {
            windowOrder.remove(this);
            windowOrder.add(this);
            for (; curIndex < windowOrder.size(); curIndex++) {
                ((IWindow) windowOrder.get(curIndex)).setWindowOrder(curIndex);
            }
        }
    }

    /**
     * Returns true if window is the topmost window
     * 
     * @return
     */
    private boolean isActive() {
        return windowOrder.lastElement().equals(this);
    }

    public void setWindowOrder(int order) {
        DOM.setStyleAttribute(getElement(), "zIndex", ""
                + (order + Z_INDEX_BASE));
    }

    protected void constructDOM() {
        header = DOM.createDiv();
        DOM.setElementProperty(header, "className", CLASSNAME + "-outerheader");
        headerText = DOM.createDiv();
        DOM.setElementProperty(headerText, "className", CLASSNAME + "-header");
        contents = DOM.createDiv();
        DOM.setElementProperty(contents, "className", CLASSNAME + "-contents");
        footer = DOM.createDiv();
        DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
        resizeBox = DOM.createDiv();
        DOM
                .setElementProperty(resizeBox, "className", CLASSNAME
                        + "-resizebox");
        closeBox = DOM.createDiv();
        DOM.setElementProperty(closeBox, "className", CLASSNAME + "-closebox");
        DOM.appendChild(footer, resizeBox);

        DOM.sinkEvents(header, Event.MOUSEEVENTS);
        DOM.sinkEvents(resizeBox, Event.MOUSEEVENTS);
        DOM.sinkEvents(closeBox, Event.ONCLICK);
        DOM.sinkEvents(contents, Event.ONCLICK);

        final Element wrapper = DOM.createDiv();
        DOM.setElementProperty(wrapper, "className", CLASSNAME + "-wrap");
        final Element wrapper2 = DOM.createDiv();
        DOM.setElementProperty(wrapper2, "className", CLASSNAME + "-wrap2");

        DOM.sinkEvents(wrapper, Event.ONKEYDOWN);

        DOM.appendChild(wrapper2, closeBox);
        DOM.appendChild(wrapper2, header);
        DOM.appendChild(header, headerText);
        DOM.appendChild(wrapper2, contents);
        DOM.appendChild(wrapper2, footer);
        DOM.appendChild(wrapper, wrapper2);
        DOM.appendChild(getElement(), wrapper);
        setWidget(contentPanel);

        // set default size
        setWidth(DEFAULT_WIDTH + "px");
        setHeight(DEFAULT_HEIGHT + "px");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        id = uidl.getId();
        this.client = client;

        if (uidl.hasAttribute("invisible")) {
            this.hide();
            return;
        }

        if (client.updateComponent(this, uidl, false)) {
            return;
        }

        if (uidl.getBooleanAttribute("modal") != modal) {
            setModal(!modal);
        }

        // Initialize the width from UIDL
        if (uidl.hasVariable("width")) {
            final String width = uidl.getStringVariable("width");
            setWidth(width);
        }
        if (uidl.hasVariable("height")) {
            final String height = uidl.getStringVariable("height");
            setHeight(height);
        }

        // Initialize the position form UIDL
        try {
            final int positionx = uidl.getIntVariable("positionx");
            final int positiony = uidl.getIntVariable("positiony");
            if (positionx >= 0 && positiony >= 0) {
                setPopupPosition(positionx, positiony);
            }
        } catch (final IllegalArgumentException e) {
            // Silently ignored as positionx and positiony are not required
            // parameters
        }

        if (!isAttached()) {
            show();
        }

        if (uidl.hasAttribute("caption")) {
            setCaption(uidl.getStringAttribute("caption"));
        }

        UIDL childUidl = uidl.getChildUIDL(0);
        if ("open".equals(childUidl.getTag())) {
            final String parsedUri = client.translateToolkitUri(childUidl
                    .getStringAttribute("src"));
            // TODO this should be a while-loop for multiple opens
            if (!childUidl.hasAttribute("name")) {
                final Frame frame = new Frame();
                DOM.setStyleAttribute(frame.getElement(), "width", "100%");
                DOM.setStyleAttribute(frame.getElement(), "height", "100%");
                DOM.setStyleAttribute(frame.getElement(), "border", "0px");
                frame.setUrl(parsedUri);
                contentPanel.setWidget(frame);
            } else {
                final String target = childUidl.getStringAttribute("name");
                Window.open(parsedUri, target, "");
            }
        } else {
            final Paintable lo = client.getPaintable(childUidl);
            if (layout != null) {
                if (layout != lo) {
                    // remove old
                    client.unregisterPaintable(layout);
                    contentPanel.remove((Widget) layout);
                    // add new
                    contentPanel.setWidget((Widget) lo);
                    layout = lo;
                }
            } else {
                contentPanel.setWidget((Widget) lo);
            }
            lo.updateFromUIDL(childUidl, client);
        }

        // we may have actions and notifications
        if (uidl.getChidlCount() > 1) {
            final int cnt = uidl.getChidlCount();
            for (int i = 1; i < cnt; i++) {
                childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (shortcutHandler == null) {
                        shortcutHandler = new ShortcutActionHandler(id, client);
                    }
                    shortcutHandler.updateActionMap(childUidl);
                } else if (childUidl.getTag().equals("notifications")) {
                    // TODO needed? move ->
                    for (final Iterator it = childUidl.getChildIterator(); it
                            .hasNext();) {
                        final UIDL notification = (UIDL) it.next();
                        String html = "";
                        if (notification.hasAttribute("caption")) {
                            html += "<H1>"
                                    + notification
                                            .getStringAttribute("caption")
                                    + "</H1>";
                        }
                        if (notification.hasAttribute("message")) {
                            html += "<p>"
                                    + notification
                                            .getStringAttribute("message")
                                    + "</p>";
                        }

                        final String style = notification.hasAttribute("style") ? notification
                                .getStringAttribute("style")
                                : null;
                        final int position = notification
                                .getIntAttribute("position");
                        final int delay = notification.getIntAttribute("delay");
                        new Notification(delay).show(html, position, style);
                    }
                }
            }

        }

        // setting scrollposition must happen after children is rendered
        contentPanel.setScrollPosition(uidl.getIntVariable("scrolltop"));
        contentPanel.setHorizontalScrollPosition(uidl
                .getIntVariable("scrollleft"));

    }

    private void setModal(boolean modality) {
        // TODO create transparent curtain to create visual clue that window is
        // modal
        modal = modality;
    }

    public void setPopupPosition(int left, int top) {
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
        DOM.setInnerText(headerText, c);
    }

    protected Element getContainerElement() {
        return contents;
    }

    public void onBrowserEvent(final Event event) {
        final int type = DOM.eventGetType(event);
        if (type == Event.ONKEYDOWN && shortcutHandler != null) {
            shortcutHandler.handleKeyboardEvent(event);
            return;
        }

        final Element target = DOM.eventGetTarget(event);
        if (dragging || DOM.isOrHasChild(header, target)) {
            onHeaderEvent(event);
            DOM.eventCancelBubble(event, true);
        } else if (resizing || DOM.compare(resizeBox, target)) {
            onResizeEvent(event);
            DOM.eventCancelBubble(event, true);
        } else if (DOM.compare(target, closeBox) && type == Event.ONCLICK) {
            onCloseClick();
            DOM.eventCancelBubble(event, true);
        } else {
            // clicked inside window, ensure to be on top
            if (!isActive()) {
                bringToFront();
            }
        }
    }

    private void onCloseClick() {
        client.updateVariable(id, "close", true, true);
    }

    private void onResizeEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
            if (!isActive()) {
                bringToFront();
            }
            resizing = true;
            startX = DOM.eventGetScreenX(event);
            startY = DOM.eventGetScreenY(event);
            origW = DOM.getIntStyleAttribute(getElement(), "width")
                    - BORDER_WIDTH_HORIZONTAL;
            origH = getWidget().getOffsetHeight();
            DOM.eventPreventDefault(event);
            DOM.addEventPreview(this);
            break;
        case Event.ONMOUSEUP:
            resizing = false;
            DOM.removeEventPreview(this);
            setSize(event, true);
            break;
        case Event.ONMOUSEMOVE:
            if (resizing) {
                setSize(event, false);
                DOM.eventPreventDefault(event);
            }
            break;
        default:
            DOM.eventPreventDefault(event);
            break;
        }
    }

    public void setSize(Event event, boolean updateVariables) {
        int w = DOM.eventGetScreenX(event) - startX + origW;
        if (w < MIN_WIDTH) {
            w = MIN_WIDTH;
        }
        int h = DOM.eventGetScreenY(event) - startY + origH;
        if (h < MIN_HEIGHT) {
            h = MIN_HEIGHT;
        }
        setWidth(w + "px");
        setHeight(h + "px");
        if (updateVariables) {
            // sending width back always as pixels, no need for unit
            client.updateVariable(id, "width", w, false);
            client.updateVariable(id, "height", h, false);
        }
        // Update child widget dimensions
        Util.runDescendentsLayout(this);
    }

    public void setWidth(String width) {
        if (!"".equals(width)) {
            DOM
                    .setStyleAttribute(
                            getElement(),
                            "width",
                            (Integer.parseInt(width.substring(0,
                                    width.length() - 2)) + BORDER_WIDTH_HORIZONTAL)
                                    + "px");
        }
    }

    private void onHeaderEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        case Event.ONMOUSEDOWN:
            if (!isActive()) {
                bringToFront();
            }
            dragging = true;
            startX = DOM.eventGetScreenX(event);
            startY = DOM.eventGetScreenY(event);
            origX = DOM.getAbsoluteLeft(getElement());
            origY = DOM.getAbsoluteTop(getElement());
            DOM.eventPreventDefault(event);
            DOM.addEventPreview(this);
            break;
        case Event.ONMOUSEUP:
            dragging = false;
            DOM.removeEventPreview(this);
            break;
        case Event.ONMOUSEMOVE:
            if (dragging) {
                final int x = DOM.eventGetScreenX(event) - startX + origX;
                final int y = DOM.eventGetScreenY(event) - startY + origY;
                setPopupPosition(x, y);
                DOM.eventPreventDefault(event);
            }
            break;
        default:
            break;
        }
    }

    public boolean onEventPreview(Event event) {
        if (dragging) {
            onHeaderEvent(event);
            return false;
        } else if (resizing) {
            onResizeEvent(event);
            return false;
        } else if (modal) {
            // return false when modal and outside window
            final Element target = DOM.eventGetTarget(event);
            if (!DOM.isOrHasChild(getElement(), target)) {
                return false;
            }
        }
        return true;
    }

    public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
        client.updateVariable(id, "scrolltop", scrollTop, false);
        client.updateVariable(id, "scrollleft", scrollLeft, false);
    }

}
