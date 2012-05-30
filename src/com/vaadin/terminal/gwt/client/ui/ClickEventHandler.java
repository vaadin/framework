/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.Util;

public abstract class ClickEventHandler implements DoubleClickHandler,
        ContextMenuHandler, MouseUpHandler, MouseDownHandler {

    private HandlerRegistration doubleClickHandlerRegistration;
    private HandlerRegistration mouseUpHandlerRegistration;
    private HandlerRegistration mouseDownHandlerRegistration;
    private HandlerRegistration contextMenuHandlerRegistration;

    protected String clickEventIdentifier;
    protected Paintable paintable;
    private ApplicationConnection client;

    /**
     * The element where the last mouse down event was registered.
     */
    private JavaScriptObject lastMouseDownTarget;

    /**
     * Set to true by {@link #mouseUpPreviewHandler} if it gets a mouseup at the
     * same element as {@link #lastMouseDownTarget}.
     */
    private boolean mouseUpPreviewMatched = false;

    private HandlerRegistration mouseUpEventPreviewRegistration;

    /**
     * Previews events after a mousedown to detect where the following mouseup
     * hits.
     */
    private final NativePreviewHandler mouseUpPreviewHandler = new NativePreviewHandler() {
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONMOUSEUP) {
                mouseUpEventPreviewRegistration.removeHandler();

                // Event's reported target not always correct if event
                // capture is in use
                Element elementUnderMouse = Util.getElementUnderMouse(event
                        .getNativeEvent());
                if (lastMouseDownTarget != null
                        && elementUnderMouse.cast() == lastMouseDownTarget) {
                    mouseUpPreviewMatched = true;
                } else {
                    System.out.println("Ignoring mouseup from "
                            + elementUnderMouse + " when mousedown was on "
                            + lastMouseDownTarget);
                }
            }
        }
    };

    public ClickEventHandler(Paintable paintable, String clickEventIdentifier) {
        this.paintable = paintable;
        this.clickEventIdentifier = clickEventIdentifier;
    }

    public void handleEventHandlerRegistration(ApplicationConnection client) {
        this.client = client;
        // Handle registering/unregistering of click handler depending on if
        // server side listeners have been added or removed.
        if (hasEventListener()) {
            if (mouseUpHandlerRegistration == null) {
                mouseUpHandlerRegistration = registerHandler(this,
                        MouseUpEvent.getType());
                mouseDownHandlerRegistration = registerHandler(this,
                        MouseDownEvent.getType());
                contextMenuHandlerRegistration = registerHandler(this,
                        ContextMenuEvent.getType());
                doubleClickHandlerRegistration = registerHandler(this,
                        DoubleClickEvent.getType());
            }
        } else {
            if (mouseUpHandlerRegistration != null) {
                // Remove existing handlers
                doubleClickHandlerRegistration.removeHandler();
                mouseUpHandlerRegistration.removeHandler();
                mouseDownHandlerRegistration.removeHandler();
                contextMenuHandlerRegistration.removeHandler();

                contextMenuHandlerRegistration = null;
                mouseUpHandlerRegistration = null;
                mouseDownHandlerRegistration = null;
                doubleClickHandlerRegistration = null;

            }
        }

    }

    protected abstract <H extends EventHandler> HandlerRegistration registerHandler(
            final H handler, DomEvent.Type<H> type);

    protected ApplicationConnection getApplicationConnection() {
        return client;
    }

    public boolean hasEventListener() {
        return getApplicationConnection().hasEventListeners(paintable,
                clickEventIdentifier);
    }

    protected void fireClick(NativeEvent event) {
        ApplicationConnection client = getApplicationConnection();
        String pid = getApplicationConnection().getPid(paintable);

        MouseEventDetails mouseDetails = new MouseEventDetails(event,
                getRelativeToElement());

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("mouseDetails", mouseDetails.serialize());
        client.updateVariable(pid, clickEventIdentifier, parameters, true);

    }

    public void onContextMenu(ContextMenuEvent event) {
        if (hasEventListener()) {
            // Prevent showing the browser's context menu when there is a right
            // click listener.
            event.preventDefault();
        }

    }

    public void onMouseDown(MouseDownEvent event) {
        /*
         * When getting a mousedown event, we must detect where the
         * corresponding mouseup event if it's on a different part of the page.
         */
        lastMouseDownTarget = event.getNativeEvent().getEventTarget();
        mouseUpPreviewMatched = false;
        mouseUpEventPreviewRegistration = Event
                .addNativePreviewHandler(mouseUpPreviewHandler);
    }

    public void onMouseUp(MouseUpEvent event) {
        /*
         * Only fire a click if the mouseup hits the same element as the
         * corresponding mousedown. This is first checked in the event preview
         * but we can't fire the even there as the event might get canceled
         * before it gets here.
         */
        if (hasEventListener()
                && mouseUpPreviewMatched
                && lastMouseDownTarget != null
                && Util.getElementUnderMouse(event.getNativeEvent()) == lastMouseDownTarget) {
            // "Click" with left, right or middle button
            fireClick(event.getNativeEvent());
        }
        mouseUpPreviewMatched = false;
        lastMouseDownTarget = null;
    }

    public void onDoubleClick(DoubleClickEvent event) {
        if (hasEventListener()) {
            fireClick(event.getNativeEvent());
        }
    }

    /**
     * Click event calculates and returns coordinates relative to the element
     * returned by this method. Default implementation uses the root element of
     * the widget. Override to provide a different relative element.
     * 
     * @return The Element used for calculating relative coordinates for a click
     *         or null if no relative coordinates can be calculated.
     */
    protected Element getRelativeToElement() {
        if (paintable instanceof Widget) {
            return ((Widget) paintable).getElement();
        }

        return null;
    }

}