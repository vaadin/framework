/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class ClickEventHandler implements DoubleClickHandler,
        ContextMenuHandler, MouseUpHandler {

    private HandlerRegistration doubleClickHandlerRegistration;
    private HandlerRegistration mouseUpHandlerRegistration;
    private HandlerRegistration contextMenuHandlerRegistration;

    protected String clickEventIdentifier;
    protected VPaintableWidget paintable;
    private ApplicationConnection client;

    public ClickEventHandler(VPaintableWidget paintable,
            String clickEventIdentifier) {
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
                contextMenuHandlerRegistration.removeHandler();

                contextMenuHandlerRegistration = null;
                mouseUpHandlerRegistration = null;
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
        String pid = VPaintableMap.get(getApplicationConnection()).getPid(
                paintable);

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

    public void onMouseUp(MouseUpEvent event) {
        // TODO For perfect accuracy we should check that a mousedown has
        // occured on this element before this mouseup and that no mouseup
        // has occured anywhere after that.
        if (hasEventListener()) {
            // "Click" with left, right or middle button
            fireClick(event.getNativeEvent());
        }
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
        return paintable.getWidget().getElement();
    }

}