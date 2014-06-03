/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;

public abstract class AbstractClickEventHandler implements MouseDownHandler,
        MouseUpHandler, DoubleClickHandler, ContextMenuHandler {

    private HandlerRegistration mouseDownHandlerRegistration;
    private HandlerRegistration mouseUpHandlerRegistration;
    private HandlerRegistration doubleClickHandlerRegistration;
    private HandlerRegistration contextMenuHandlerRegistration;

    protected ComponentConnector connector;
    private String clickEventIdentifier;

    /**
     * The element where the last mouse down event was registered.
     */
    private Element lastMouseDownTarget;

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

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (event.getTypeInt() == Event.ONMOUSEUP) {
                mouseUpEventPreviewRegistration.removeHandler();

                // Event's reported target not always correct if event
                // capture is in use
                Element elementUnderMouse = Util.getElementUnderMouse(event
                        .getNativeEvent());
                if (lastMouseDownTarget != null
                        && elementUnderMouse == lastMouseDownTarget) {
                    mouseUpPreviewMatched = true;
                } else {
                    VConsole.log("Ignoring mouseup from " + elementUnderMouse
                            + " when mousedown was on " + lastMouseDownTarget);
                }
            }
        }
    };

    public AbstractClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        this.connector = connector;
        this.clickEventIdentifier = clickEventIdentifier;
    }

    public void handleEventHandlerRegistration() {
        // Handle registering/unregistering of click handler depending on if
        // server side listeners have been added or removed.
        if (hasEventListener()) {
            if (mouseDownHandlerRegistration == null) {
                mouseDownHandlerRegistration = registerHandler(this,
                        MouseDownEvent.getType());
                mouseUpHandlerRegistration = registerHandler(this,
                        MouseUpEvent.getType());
                doubleClickHandlerRegistration = registerHandler(this,
                        DoubleClickEvent.getType());
                contextMenuHandlerRegistration = registerHandler(this,
                        ContextMenuEvent.getType());
            }
        } else {
            if (mouseDownHandlerRegistration != null) {
                // Remove existing handlers
                mouseDownHandlerRegistration.removeHandler();
                mouseUpHandlerRegistration.removeHandler();
                doubleClickHandlerRegistration.removeHandler();
                contextMenuHandlerRegistration.removeHandler();

                mouseDownHandlerRegistration = null;
                mouseUpHandlerRegistration = null;
                doubleClickHandlerRegistration = null;
                contextMenuHandlerRegistration = null;
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

    @Override
    public void onContextMenu(ContextMenuEvent event) {
        if (hasEventListener() && shouldFireEvent(event)) {
            // Prevent showing the browser's context menu when there is a right
            // click listener.
            event.preventDefault();
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        /*
         * When getting a mousedown event, we must detect where the
         * corresponding mouseup event if it's on a different part of the page.
         */
        lastMouseDownTarget = Util.getElementUnderMouse(event.getNativeEvent());
        mouseUpPreviewMatched = false;
        mouseUpEventPreviewRegistration = Event
                .addNativePreviewHandler(mouseUpPreviewHandler);
    }

    @Override
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
                && Util.getElementUnderMouse(event.getNativeEvent()) == lastMouseDownTarget
                && shouldFireEvent(event)) {
            // "Click" with left, right or middle button
            fireClick(event.getNativeEvent());
        }
        mouseUpPreviewMatched = false;
        lastMouseDownTarget = null;
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

    @Override
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
    protected com.google.gwt.user.client.Element getRelativeToElement() {
        return connector.getWidget().getElement();
    }

}
