/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

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
import com.vaadin.terminal.gwt.client.ComponentConnector;

public abstract class AbstractClickEventHandler implements DoubleClickHandler,
        ContextMenuHandler, MouseUpHandler {

    private HandlerRegistration doubleClickHandlerRegistration;
    private HandlerRegistration mouseUpHandlerRegistration;
    private HandlerRegistration contextMenuHandlerRegistration;

    protected ComponentConnector connector;
    private String clickEventIdentifier;

    public AbstractClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        this.connector = connector;
        this.clickEventIdentifier = clickEventIdentifier;
    }

    public void handleEventHandlerRegistration() {
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

    /**
     * Registers the given handler to the widget so that the necessary events
     * are passed to this {@link ClickEventHandler}.
     * <p>
     * By default registers the handler with the connector root widget.
     * </p>
     * 
     * @param <H>
     * @param handler
     *            The handler to register
     * @param type
     *            The type of the handler.
     * @return A reference for the registration of the handler.
     */
    protected <H extends EventHandler> HandlerRegistration registerHandler(
            final H handler, DomEvent.Type<H> type) {
        return connector.getWidget().addDomHandler(handler, type);
    }

    /**
     * Checks if there is a server side event listener registered for clicks
     * 
     * @return true if there is a server side event listener registered, false
     *         otherwise
     */
    public boolean hasEventListener() {
        return connector.hasEventListener(clickEventIdentifier);
    }

    /**
     * Event handler for context menu. Prevents the browser context menu from
     * popping up if there is a listener for right clicks.
     */
    public void onContextMenu(ContextMenuEvent event) {
        if (hasEventListener() && shouldFireEvent(event)) {
            // Prevent showing the browser's context menu when there is a right
            // click listener.
            event.preventDefault();
        }
    }

    /**
     * Event handler for mouse up. This is used to detect all single click
     * events.
     */
    public void onMouseUp(MouseUpEvent event) {
        // TODO For perfect accuracy we should check that a mousedown has
        // occured on this element before this mouseup and that no mouseup
        // has occured anywhere after that.
        if (hasEventListener() && shouldFireEvent(event)) {
            // "Click" with left, right or middle button
            fireClick(event.getNativeEvent());
        }
    }

    /**
     * Sends the click event based on the given native event.
     * 
     * @param event
     *            The native event that caused this click event
     */
    protected abstract void fireClick(NativeEvent event);

    /**
     * Called before firing a click event. Allows sub classes to decide if this
     * in an event that should cause an event or not.
     * 
     * @param event
     *            The user event
     * @return true if the event should be fired, false otherwise
     */
    protected boolean shouldFireEvent(DomEvent<?> event) {
        return true;
    }

    /**
     * Event handler for double clicks. Used to fire double click events. Note
     * that browsers typically fail to prevent the second click event so a
     * double click will result in two click events and one double click event.
     */
    public void onDoubleClick(DoubleClickEvent event) {
        if (hasEventListener() && shouldFireEvent(event)) {
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
        return connector.getWidget().getElement();
    }

}