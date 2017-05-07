/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.extensions;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.DropTargetRpc;
import com.vaadin.shared.ui.dnd.DropTargetState;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

/**
 * Extension to add drop target functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DropTargetExtension}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(DropTargetExtension.class)
public class DropTargetExtensionConnector extends AbstractExtensionConnector {

    /**
     * Style name suffix for dragging data over the center of the drop target.
     */
    protected static final String STYLE_SUFFIX_DRAG_CENTER = "-drag-center";

    /**
     * Style name suffix for dragging data over the top part of the drop target.
     */
    protected static final String STYLE_SUFFIX_DRAG_TOP = "-drag-top";

    /**
     * Style name suffix for dragging data over the bottom part of the drop
     * target.
     */
    protected static final String STYLE_SUFFIX_DRAG_BOTTOM = "-drag-bottom";

    // Create event listeners
    private final EventListener dragEnterListener = this::onDragEnter;
    private final EventListener dragOverListener = this::onDragOver;
    private final EventListener dragLeaveListener = this::onDragLeave;
    private final EventListener dropListener = this::onDrop;

    /**
     * Widget of the drop target component.
     */
    private Widget dropTargetWidget;

    /**
     * Class name to apply when an element is dragged over the center of the
     * target.
     */
    private String styleDragCenter;

    @Override
    protected void extend(ServerConnector target) {
        dropTargetWidget = ((ComponentConnector) target).getWidget();

        // HTML5 DnD is by default not enabled for mobile devices
        if (BrowserInfo.get().isTouchDevice() && !getConnection()
                .getUIConnector().isMobileHTML5DndEnabled()) {
            return;
        }

        addDropListeners(getDropTargetElement());
    }

    /**
     * Adds dragenter, dragover, dragleave and drop event listeners to the given
     * DOM element.
     *
     * @param element
     *            DOM element to attach event listeners to.
     */
    protected void addDropListeners(Element element) {
        EventTarget target = element.cast();

        target.addEventListener(Event.DRAGENTER, dragEnterListener);
        target.addEventListener(Event.DRAGOVER, dragOverListener);
        target.addEventListener(Event.DRAGLEAVE, dragLeaveListener);
        target.addEventListener(Event.DROP, dropListener);
    }

    /**
     * Removes dragenter, dragover, dragleave and drop event listeners from the
     * given DOM element.
     *
     * @param element
     *            DOM element to remove event listeners from.
     */
    protected void removeDropListeners(Element element) {
        EventTarget target = element.cast();

        target.removeEventListener(Event.DRAGENTER, dragEnterListener);
        target.removeEventListener(Event.DRAGOVER, dragOverListener);
        target.removeEventListener(Event.DRAGLEAVE, dragLeaveListener);
        target.removeEventListener(Event.DROP, dropListener);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        removeDropListeners(getDropTargetElement());
    }

    /**
     * Finds the drop target element within the widget. By default, returns the
     * topmost element.
     *
     * @return the drop target element in the parent widget.
     */
    protected Element getDropTargetElement() {
        return dropTargetWidget.getElement();
    }

    /**
     * Event handler for the {@code dragenter} event.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragEnter(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDropAllowed(nativeEvent)) {
            // Generate style name for drop target
            styleDragCenter = dropTargetWidget.getStylePrimaryName()
                    + STYLE_SUFFIX_DRAG_CENTER;

            setTargetClassIndicator(event);

            setDropEffect(nativeEvent);

            // According to spec, need to call this for allowing dropping, the
            // default action would be to reject as target
            event.preventDefault();
        } else {
            // Remove drop effect
            nativeEvent.getDataTransfer()
                    .setDropEffect(DataTransfer.DropEffect.NONE);
        }
    }

    /**
     * Set the drop effect for the dragenter / dragover event, if one has been
     * set from server side.
     * <p>
     * From Moz Foundation: "You can modify the dropEffect property during the
     * dragenter or dragover events, if for example, a particular drop target
     * only supports certain operations. You can modify the dropEffect property
     * to override the user effect, and enforce a specific drop operation to
     * occur. Note that this effect must be one listed within the effectAllowed
     * property. Otherwise, it will be set to an alternate value that is
     * allowed."
     *
     * @param event
     *            the dragenter or dragover event.
     */
    protected void setDropEffect(NativeEvent event) {
        if (getState().dropEffect != null) {

            DataTransfer.DropEffect dropEffect = DataTransfer.DropEffect
                    // the valueOf() needs to have equal string and name()
                    // doesn't return in all upper case
                    .valueOf(getState().dropEffect.name().toUpperCase());
            event.getDataTransfer().setDropEffect(dropEffect);
        }
    }

    /**
     * Event handler for the {@code dragover} event.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragOver(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDropAllowed(nativeEvent)) {
            setDropEffect(nativeEvent);

            // Add drop target indicator in case the element doesn't have one
            setTargetClassIndicator(event);

            // Prevent default to allow drop
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();
        } else {
            // Remove drop effect
            nativeEvent.getDataTransfer()
                    .setDropEffect(DataTransfer.DropEffect.NONE);

            // Remove drop target indicator
            removeTargetClassIndicator(event);
        }
    }

    /**
     * Event handler for the {@code dragleave} event.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDragLeave(Event event) {
        removeTargetClassIndicator(event);
    }

    /**
     * Event handler for the {@code drop} event.
     *
     * @param event
     *            browser event to be handled
     */
    protected void onDrop(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDropAllowed(nativeEvent)) {
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();

            String dataTransferText = nativeEvent.getDataTransfer()
                    .getData(DragSourceState.DATA_TYPE_TEXT);

            String dropEffect = DragSourceExtensionConnector
                    .getDropEffect(nativeEvent.getDataTransfer());

            sendDropEventToServer(dataTransferText, dropEffect, event);
        }

        removeTargetClassIndicator(event);
    }

    private boolean isDropAllowed(NativeEvent event) {
        // there never should be a drop when effect has been set to none
        if (getState().dropEffect != null
                && getState().dropEffect == DropEffect.NONE) {
            return false;
        }
        // TODO #9246: Should add verification for checking effectAllowed and
        // dropEffect from event and comparing that to target's dropEffect.
        // Currently Safari, Edge and IE don't follow the spec by allowing drop
        // if those don't match

        if (getState().dropCriteria != null) {
            return executeScript(event, getState().dropCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    /**
     * Initiates a server RPC for the drop event.
     *
     * @param dataTransferText
     *            Client side textual data that can be set for the drag source
     *            and is transferred to the drop target.
     * @param dropEffect
     *            the desired drop effect
     * @param dropEvent
     *            Client side drop event.
     */
    protected void sendDropEventToServer(String dataTransferText,
            String dropEffect, Event dropEvent) {
        getRpcProxy(DropTargetRpc.class).drop(dataTransferText, dropEffect);
    }

    /**
     * Add class that indicates that the component is a target.
     *
     * @param event
     *            The drag enter or dragover event that triggered the
     *            indication.
     */
    protected void setTargetClassIndicator(Event event) {
        getDropTargetElement().addClassName(styleDragCenter);
    }

    /**
     * Remove the drag target indicator class name from the target element.
     * <p>
     * This is triggered on dragleave, drop and dragover events.
     *
     * @param event
     *            the event that triggered the removal of the indicator
     */
    protected void removeTargetClassIndicator(Event event) {
        getDropTargetElement().removeClassName(styleDragCenter);
    }

    private native boolean executeScript(NativeEvent event, String script)
    /*-{
        return new Function('event', script)(event);
    }-*/;

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
