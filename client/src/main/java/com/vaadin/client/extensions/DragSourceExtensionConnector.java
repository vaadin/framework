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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DataTransfer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.event.dnd.DragSourceExtension;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.dnd.DragSourceRpc;
import com.vaadin.shared.ui.dnd.DragSourceState;

/**
 * Extension to add drag source functionality to a widget for using HTML5 drag
 * and drop. Client side counterpart of {@link DragSourceExtension}.
 */
@Connect(DragSourceExtension.class)
public class DragSourceExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_DRAGGABLE = "v-draggable";

    // Create native event listeners
    private final JavaScriptObject dragStartListener = createNativeFunction(
            this::onDragStart);
    private final JavaScriptObject dragEndListener = createNativeFunction(
            this::onDragEnd);

    @Override
    protected void extend(ServerConnector target) {
        Element dragSourceElement = getDraggableElement();

        dragSourceElement.setDraggable(Element.DRAGGABLE_TRUE);
        dragSourceElement.addClassName(CLASS_DRAGGABLE);

        // dragstart
        addEventListener(dragSourceElement, BrowserEvents.DRAGSTART,
                dragStartListener);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        Element dragSourceElement = getDraggableElement();

        // Remove listeners
        removeEventListener(dragSourceElement, BrowserEvents.DRAGSTART,
                dragStartListener);

        removeEventListener(dragSourceElement, BrowserEvents.DRAGEND,
                dragEndListener);
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

         // Add event listener only when listener added on server side
        if (hasEventListener(DragSourceState.EVENT_DRAGEND)) {
            addEventListener(getDraggableElement(), BrowserEvents.DRAGEND,
                    dragEndListener);
        } else {
            removeEventListener(getDraggableElement(), BrowserEvents.DRAGEND,
                    dragEndListener);
        }
    }

    /**
     * Event handler for the {@code dragstart} event. Called when {@code
     * dragstart} event occurs.
     *
     * @param event
     *         browser event to be handled
     */
    protected void onDragStart(Event event) {
        // Set effectAllowed parameter
        if (getState().effectAllowed != null) {
            setEffectAllowed(event.getDataTransfer(),
                    getState().effectAllowed.getValue());
        }

        // Set data parameter
        List<String> types = getState().types;
        Map<String, String> data = getState().data;
        for (String format : types) {
            event.getDataTransfer().setData(format, data.get(format));
        }

        // Initiate firing server side dragstart event
        getRpcProxy(DragSourceRpc.class).dragStart();
    }

    /**
     * Event handler for the {@code dragend} event. Called when {@code dragend}
     * event occurs.
     *
     * @param event
     */
    protected void onDragEnd(Event event) {
        // Initiate server start dragend event
        getRpcProxy(DragSourceRpc.class).dragEnd();
    }

    /**
     * Finds the draggable element within the widget. By default, returns the
     * topmost element.
     *
     * @return the draggable element in the parent widget.
     */
    protected Element getDraggableElement() {
        return ((ComponentConnector) getParent()).getWidget().getElement();
    }

    private native JavaScriptObject createNativeFunction(
            EventListener listener)/*-{
        return $entry(function (event) {
            listener.@com.google.gwt.user.client.EventListener::onBrowserEvent(*)(event);
        });
    }-*/;

    private native void addEventListener(Element element, String eventName,
            JavaScriptObject listenerFunction)/*-{
        element.addEventListener(eventName, listenerFunction, false);
    }-*/;

    private native void removeEventListener(Element element, String eventName,
            JavaScriptObject listenerFunction)/*-{
        element.removeEventListener(eventName, listenerFunction, false);
    }-*/;

    private native void setEffectAllowed(DataTransfer dataTransfer,
            String effectAllowed)/*-{
        dataTransfer.effectAllowed = effectAllowed;
    }-*/;

    @Override
    public DragSourceState getState() {
        return (DragSourceState) super.getState();
    }
}
