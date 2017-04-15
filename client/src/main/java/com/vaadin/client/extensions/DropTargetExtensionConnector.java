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

import java.util.Optional;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceState;
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

    /**
     * Style name suffix for indicating that the element is drop target.
     */
    private static final String STYLE_SUFFIX_DROPTARGET = "-droptarget";

    // Create event listeners
    private final EventListener dragEnterListener = this::onDragEnter;
    private final EventListener dragOverListener = this::onDragOver;
    private final EventListener dragLeaveListener = this::onDragLeave;
    private final EventListener dropListener = this::onDrop;

    /**
     * Drop target component's connector.
     */
    private ComponentConnector dropTargetConnector;

    /**
     * Class name to apply when an element is dragged over the center of the
     * target.
     */
    private String styleDragCenter;

    /**
     * Style change handler registration.
     */
    private HandlerRegistration styleChangeHandler;

    /**
     * Style name for indicating that the element is drop target.
     */
    private String styleDropTarget;

    @Override
    protected void extend(ServerConnector target) {
        dropTargetConnector = (ComponentConnector) target;

        addDropListeners(getDropTargetElement());

        // Add style for indicating drop target and update when primary style changes
        styleChangeHandler = dropTargetConnector
                .addStateChangeHandler("primaryStyleName", event -> {
                    if (styleDropTarget != null) {
                        getDropTargetElement().removeClassName(styleDropTarget);
                    }
                    styleDropTarget = dropTargetConnector.getWidget()
                            .getStylePrimaryName() + STYLE_SUFFIX_DROPTARGET;
                    getDropTargetElement().addClassName(styleDropTarget);
                });
    }

    /**
     * Adds dragenter, dragover, dragleave and drop event listeners to the given
     * DOM element.
     *
     * @param element
     *         DOM element to attach event listeners to.
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
     *         DOM element to remove event listeners from.
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

        // Remove drop target style and style change handler
        getDropTargetElement().removeClassName(styleDropTarget);
        Optional.ofNullable(styleChangeHandler)
                .ifPresent(HandlerRegistration::removeHandler);
    }

    /**
     * Finds the drop target element within the widget. By default, returns the
     * topmost element.
     *
     * @return the drop target element in the parent widget.
     */
    protected Element getDropTargetElement() {
        return dropTargetConnector.getWidget().getElement();
    }

    /**
     * Event handler for the {@code dragenter} event.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragEnter(Event event) {
        // Generate style name for drop target
        styleDragCenter = dropTargetConnector.getWidget().getStylePrimaryName()
                + STYLE_SUFFIX_DRAG_CENTER;

        setTargetIndicator(event);
    }

    /**
     * Event handler for the {@code dragover} event.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragOver(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (isDragOverAllowed(nativeEvent)) {
            // Set dropEffect parameter
            if (getState().dropEffect != null) {
                nativeEvent.getDataTransfer().setDropEffect(
                        DataTransfer.DropEffect
                                .valueOf(getState().dropEffect.name()));
            }

            // Add drop target indicator in case the element doesn't have one
            setTargetIndicator(event);

            // Prevent default to allow drop
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();
        } else {
            // Remove drop effect
            nativeEvent.getDataTransfer()
                    .setDropEffect(DataTransfer.DropEffect.NONE);

            // Remove drop target indicator
            removeTargetIndicator(event);
        }
    }

    /**
     * Determines if dragover event is allowed on this drop target according to
     * the dragover criteria.
     *
     * @param event
     *         Native dragover event.
     * @return {@code true} if dragover is allowed, {@code false} otherwise.
     * @see DropTargetExtension#setDragOverCriteria(String)
     */
    protected boolean isDragOverAllowed(NativeEvent event) {
        if (getState().dragOverCriteria != null) {
            return executeScript(event, getState().dragOverCriteria);
        }

        // Allow when criteria not set
        return true;
    }

    /**
     * Event handler for the {@code dragleave} event.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragLeave(Event event) {
        removeTargetIndicator(event);
    }

    /**
     * Event handler for the {@code drop} event.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDrop(Event event) {
        NativeEvent nativeEvent = (NativeEvent) event;
        if (dropAllowed(nativeEvent)) {
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();

            String dataTransferText = nativeEvent.getDataTransfer().getData(
                    DragSourceState.DATA_TYPE_TEXT);

            sendDropEventToServer(dataTransferText, event);
        }

        removeTargetIndicator(event);
    }

    private boolean dropAllowed(NativeEvent event) {
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
     *         Client side textual data that can be set for the drag source and
     *         is transferred to the drop target.
     * @param dropEvent
     *         Client side drop event.
     */
    protected void sendDropEventToServer(String dataTransferText,
            Event dropEvent) {
        getRpcProxy(DropTargetRpc.class).drop(dataTransferText);
    }

    /**
     * Add class that indicates that the component is a target.
     *
     * @param event
     *         The drag enter or dragover event that triggered the indication.
     */
    protected void setTargetIndicator(Event event) {
        getDropTargetElement().addClassName(styleDragCenter);
    }

    /**
     * Remove the drag target indicator class name from the target element.
     * <p>
     * This is triggered on dragleave, drop and dragover events.
     *
     * @param event
     *         the event that triggered the removal of the indicator
     */
    protected void removeTargetIndicator(Event event) {
        getDropTargetElement().removeClassName(styleDragCenter);
    }

    private native boolean executeScript(NativeEvent event, String script)/*-{
        return new Function('event', script)(event);
    }-*/;

    @Override
    public DropTargetState getState() {
        return (DropTargetState) super.getState();
    }
}
