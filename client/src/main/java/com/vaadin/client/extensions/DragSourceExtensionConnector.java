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

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DragSourceExtension}.
 */
@Connect(DragSourceExtension.class)
public class DragSourceExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAGGABLE = "v-draggable";

    // Create event listeners
    private final EventListener dragStartListener = this::onDragStart;
    private final EventListener dragEndListener = this::onDragEnd;

    /**
     * Widget of the drag source component.
     */
    private Widget dragSourceWidget;

    @Override
    protected void extend(ServerConnector target) {
        dragSourceWidget = ((ComponentConnector) target).getWidget();

        setDraggable(getDraggableElement());
        addDragListeners(getDraggableElement());
    }

    /**
     * Sets the given element draggable and adds class name.
     *
     * @param element
     *         Element to be set draggable.
     */
    protected void setDraggable(Element element) {
        element.setDraggable(Element.DRAGGABLE_TRUE);
        element.addClassName(CLASS_DRAGGABLE);
    }

    /**
     * Removes draggable and class name from the given element.
     *
     * @param element
     *         Element to remove draggable from.
     */
    protected void removeDraggable(Element element) {
        element.setDraggable(Element.DRAGGABLE_FALSE);
        element.removeClassName(CLASS_DRAGGABLE);
    }

    /**
     * Adds dragstart and dragend event listeners to the given DOM element.
     *
     * @param element
     *         DOM element to attach event listeners to.
     */
    protected void addDragListeners(Element element) {
        EventTarget target = element.cast();

        target.addEventListener(Event.DRAGSTART, dragStartListener);
        target.addEventListener(Event.DRAGEND, dragEndListener);
    }

    /**
     * Removes dragstart and dragend event listeners from the given DOM element.
     *
     * @param element
     *         DOM element to remove event listeners from.
     */
    protected void removeDragListeners(Element element) {
        EventTarget target = element.cast();

        target.removeEventListener(Event.DRAGSTART, dragStartListener);
        target.removeEventListener(Event.DRAGEND, dragEndListener);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        Element dragSource = getDraggableElement();

        removeDraggable(dragSource);
        removeDragListeners(dragSource);
    }

    /**
     * Event handler for the {@code dragstart} event. Called when {@code
     * dragstart} event occurs.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragStart(Event event) {
        // Convert elemental event to have access to dataTransfer
        NativeEvent nativeEvent = (NativeEvent) event;

        // Set effectAllowed parameter
        if (getState().effectAllowed != null) {
            setEffectAllowed(nativeEvent.getDataTransfer(),
                    getState().effectAllowed.getValue());
        }

        // Set data parameter
        List<String> types = getState().types;
        Map<String, String> data = getState().data;
        for (String format : types) {
            nativeEvent.getDataTransfer().setData(format, data.get(format));
        }

        // Store the extension's connector ID in DataTransfer.data
        nativeEvent.getDataTransfer()
                .setData(DragSourceState.DATA_TYPE_DRAG_SOURCE_ID,
                        getConnectorId());

        // Initiate firing server side dragstart event when there is a
        // DragStartListener attached on the server side
        if (hasEventListener(DragSourceState.EVENT_DRAGSTART)) {
            getRpcProxy(DragSourceRpc.class).dragStart();
        }
    }

    /**
     * Event handler for the {@code dragend} event. Called when {@code dragend}
     * event occurs.
     *
     * @param event
     */
    protected void onDragEnd(Event event) {
        // Initiate server start dragend event when there is a DragEndListener
        // attached on the server side
        if (hasEventListener(DragSourceState.EVENT_DRAGEND)) {
            getRpcProxy(DragSourceRpc.class).dragEnd();
        }
    }

    /**
     * Finds the draggable element within the widget. By default, returns the
     * topmost element.
     *
     * @return the draggable element in the parent widget.
     */
    protected Element getDraggableElement() {
        return dragSourceWidget.getElement();
    }

    private native void setEffectAllowed(DataTransfer dataTransfer,
            String effectAllowed)/*-{
        dataTransfer.effectAllowed = effectAllowed;
    }-*/;

    @Override
    public DragSourceState getState() {
        return (DragSourceState) super.getState();
    }
}
